/*
 * Copyright (C) 2026  Karelisio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.logic.network.lyrics

import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.getMainArtist
import com.shub39.rush.shared.core.getMainTitle
import com.shub39.rush.shared.core.util.LrcUtils
import com.shub39.rush.shared.core.util.RichsyncParser
import com.shub39.rush.shared.core.util.TrackMatcher
import com.shub39.rush.shared.core.util.TtmlBuilder
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlin.time.Clock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Single

/**
 * Unofficial Musixmatch access through the desktop app API, with an anonymous user token. Word by
 * word "richsync" lyrics are preferred, then line synced subtitles, then plain lyrics.
 */
@Single
class MusixmatchProvider : LyricsProvider {
    override val id = LyricsProviderId.MUSIXMATCH

    private data class Track(
        val id: Long,
        val name: String,
        val artist: String,
        val album: String?,
        val durationMs: Long?,
        val hasRichsync: Boolean,
        val hasSubtitles: Boolean,
        val hasLyrics: Boolean,
    )

    private class MusixmatchException(message: String) : Exception(message)

    private val client by lazy { lyricsHttpClient { install(HttpCookies) } }

    private val tokenMutex = Mutex()
    private var token: String? = null
    private var tokenExpiresAt = 0L

    override suspend fun search(query: TrackQuery, limit: Int): List<LyricsCandidate> {
        val tracks =
            call("track.search") {
                    parameter("q_track", getMainTitle(query.title))
                    parameter("q_artist", getMainArtist(query.artist))
                    parameter("page_size", 5)
                    parameter("page", 1)
                    parameter("s_track_rating", "desc")
                }
                .arr("track_list")
                ?.mapNotNull { item -> item.obj("track")?.toTrack() }
                .orEmpty()
                .filter { it.hasRichsync || it.hasSubtitles || it.hasLyrics }

        return TrackMatcher.ranked(query, tracks, { it.name }, { it.artist }, { it.durationMs })
            .take(limit)
            .mapNotNull { fetchLyrics(it) }
    }

    private fun JsonObject.toTrack(): Track? {
        if (bool("instrumental") == true) return null
        return Track(
            id = long("track_id") ?: return null,
            name = str("track_name") ?: return null,
            artist = str("artist_name") ?: "",
            album = str("album_name"),
            durationMs = long("track_length")?.takeIf { it > 0 }?.times(1000),
            hasRichsync = bool("has_richsync") == true,
            hasSubtitles = bool("has_subtitles") == true,
            hasLyrics = bool("has_lyrics") == true,
        )
    }

    private suspend fun fetchLyrics(track: Track): LyricsCandidate? {
        val timedLines =
            if (track.hasRichsync) {
                runCatching {
                        call("track.richsync.get") { parameter("track_id", track.id) }
                            .obj("richsync")
                            .str("richsync_body")
                            ?.let(RichsyncParser::parse)
                    }
                    .getOrNull()
                    .orEmpty()
            } else emptyList()

        val ttml = TtmlBuilder.build(timedLines)

        val lrc =
            LrcUtils.fromTimedLines(timedLines)
                ?: if (track.hasSubtitles) {
                    runCatching {
                            call("track.subtitle.get") {
                                    parameter("track_id", track.id)
                                    parameter("subtitle_format", "lrc")
                                }
                                .obj("subtitle")
                                .str("subtitle_body")
                                ?.let(LrcUtils::normalize)
                        }
                        .getOrNull()
                } else null

        val plain =
            lrc?.let(LrcUtils::toPlain)
                ?: if (track.hasLyrics) {
                    runCatching {
                            call("track.lyrics.get") { parameter("track_id", track.id) }
                                .obj("lyrics")
                                .str("lyrics_body")
                                ?.let(::cleanPlainLyrics)
                        }
                        .getOrNull()
                } else null

        if (ttml == null && lrc == null && plain.isNullOrBlank()) return null

        return LyricsCandidate(
            provider = id,
            title = track.name,
            artist = track.artist,
            album = track.album,
            durationMs = track.durationMs,
            plain = plain,
            lrc = lrc,
            ttml = ttml,
        )
    }

    /** Calls a desktop API method and returns `message.body`, failing on a non 200 status. */
    private suspend fun call(
        method: String,
        params: io.ktor.client.request.HttpRequestBuilder.() -> Unit,
    ): JsonElement? {
        val userToken = getToken()
        val body =
            client
                .get("$BASE_URL/$method") {
                    parameter("app_id", APP_ID)
                    parameter("usertoken", userToken)
                    parameter("t", Clock.System.now().toEpochMilliseconds())
                    params()
                }
                .bodyAsText()

        val message = Json.parseToJsonElement(body).obj("message")
        val status = message.obj("header").long("status_code")
        if (status == 401L) {
            // Expired token or captcha, a new token is fetched on the next call
            tokenMutex.withLock { token = null }
            throw MusixmatchException("$method: unauthorized (401)")
        }
        if (status != 200L) throw MusixmatchException("$method: status $status")

        return message?.get("body")
    }

    private suspend fun getToken(): String =
        tokenMutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            token
                ?.takeIf { now < tokenExpiresAt }
                ?.let {
                    return@withLock it
                }

            val body =
                client
                    .get("$BASE_URL/token.get") {
                        parameter("app_id", APP_ID)
                        parameter("user_language", "en")
                        parameter("t", now)
                    }
                    .bodyAsText()

            val message = Json.parseToJsonElement(body).obj("message")
            val status = message.obj("header").long("status_code")
            val newToken = message.obj("body").str("user_token")

            if (status != 200L || newToken.isNullOrBlank() || newToken.contains("UpgradeOnly")) {
                throw MusixmatchException("token.get failed (status $status)")
            }

            token = newToken
            tokenExpiresAt = now + TOKEN_LIFETIME_MS
            newToken
        }

    companion object {
        private const val BASE_URL = "https://apic-desktop.musixmatch.com/ws/1.1"
        private const val APP_ID = "web-desktop-app-v1.0"
        private const val TOKEN_LIFETIME_MS = 10 * 60 * 1000L

        /** Removes the "not for commercial use" footer added to plain lyrics. */
        fun cleanPlainLyrics(body: String): String? =
            body
                .lines()
                .takeWhile { !it.trimStart().startsWith("*******") }
                .joinToString("\n")
                .trim()
                .takeIf { it.isNotBlank() }
    }
}
