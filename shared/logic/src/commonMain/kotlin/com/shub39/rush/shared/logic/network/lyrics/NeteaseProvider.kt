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

import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.getMainArtist
import com.shub39.rush.shared.core.getMainTitle
import com.shub39.rush.shared.core.util.LrcUtils
import com.shub39.rush.shared.core.util.TrackMatcher
import com.shub39.rush.shared.core.util.TtmlBuilder
import com.shub39.rush.shared.core.util.YrcParser
import com.shub39.rush.shared.logic.util.aesEcbEncrypt
import com.shub39.rush.shared.logic.util.md5Hex
import com.shub39.rush.shared.logic.util.toHex
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.koin.core.annotation.Single

/**
 * NetEase Cloud Music. Line synced lyrics come from the classic lyric api, word by word ("yrc")
 * lyrics from the "eapi" endpoint used by the desktop client.
 */
@Single
class NeteaseProvider : LyricsProvider {
    override val id = LyricsProviderId.NETEASE

    private data class Song(
        val id: Long,
        val name: String,
        val artists: String,
        val album: String?,
        val durationMs: Long?,
    )

    private val client by lazy {
        lyricsHttpClient {
            defaultRequest {
                header(HttpHeaders.UserAgent, DESKTOP_USER_AGENT)
                header(HttpHeaders.Referrer, "https://music.163.com/")
            }
        }
    }

    override suspend fun search(query: TrackQuery, limit: Int): List<LyricsCandidate> {
        val songs = searchSongs(query)
        return TrackMatcher.ranked(query, songs, { it.name }, { it.artists }, { it.durationMs })
            .take(limit)
            .mapNotNull { fetchLyrics(it) }
    }

    private suspend fun searchSongs(query: TrackQuery): List<Song> {
        val body =
            client
                .get("https://music.163.com/api/search/get/web") {
                    parameter("s", "${getMainTitle(query.title)} ${getMainArtist(query.artist)}")
                    parameter("type", 1)
                    parameter("offset", 0)
                    parameter("limit", 10)
                    parameter("total", true)
                }
                .bodyAsText()

        val songs = Json.parseToJsonElement(body).obj("result").arr("songs") ?: return emptyList()
        return songs.mapNotNull { song ->
            val songId = song.long("id") ?: return@mapNotNull null
            Song(
                id = songId,
                name = song.str("name") ?: return@mapNotNull null,
                artists =
                    (song.arr("artists") ?: song.arr("ar"))
                        ?.mapNotNull { it.str("name") }
                        ?.joinToString(", ") ?: "",
                album = (song.obj("album") ?: song.obj("al")).str("name"),
                durationMs = song.long("duration") ?: song.long("dt"),
            )
        }
    }

    private suspend fun fetchLyrics(song: Song): LyricsCandidate? {
        val response =
            try {
                eapiLyrics(song.id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                RushLogger.e(TAG, "eapi lyrics failed, falling back to the classic api", e)
                classicLyrics(song.id)
            }

        if (response.bool("pureMusic") == true || response.bool("nolyric") == true) return null

        val timedLines = response.obj("yrc").str("lyric")?.let(YrcParser::parse).orEmpty()
        val ttml = TtmlBuilder.build(timedLines)
        val lrc =
            LrcUtils.normalize(response.obj("lrc").str("lyric"))
                ?: LrcUtils.fromTimedLines(timedLines)

        if (lrc == null && ttml == null) return null

        return LyricsCandidate(
            provider = id,
            title = song.name,
            artist = song.artists,
            album = song.album,
            durationMs = song.durationMs,
            plain = lrc?.let(LrcUtils::toPlain),
            lrc = lrc,
            ttml = ttml,
        )
    }

    private suspend fun classicLyrics(songId: Long): JsonObject {
        val body =
            client
                .get("https://music.163.com/api/song/lyric") {
                    parameter("id", songId)
                    parameter("lv", -1)
                    parameter("kv", -1)
                    parameter("tv", -1)
                }
                .bodyAsText()
        return Json.parseToJsonElement(body).jsonObject
    }

    private suspend fun eapiLyrics(songId: Long): JsonObject {
        val now = Clock.System.now().toEpochMilliseconds()
        val header =
            linkedMapOf(
                "osver" to "undefined",
                "deviceId" to "undefined",
                "appver" to "8.0.0",
                "versioncode" to "140",
                "mobilename" to "undefined",
                "buildver" to (now / 1000).toString(),
                "resolution" to "1920x1080",
                "__csrf" to "",
                "os" to "pc",
                "channel" to "undefined",
                "requestId" to "${now}_${Random.nextInt(0, 1000).toString().padStart(4, '0')}",
            )

        val payload =
            buildJsonObject {
                    put("id", songId)
                    put("cp", false)
                    put("tv", 0)
                    put("lv", 0)
                    put("rv", 0)
                    put("kv", 0)
                    put("yv", 0)
                    put("ytv", 0)
                    put("yrv", 0)
                    put(
                        "header",
                        buildJsonObject { header.forEach { (key, value) -> put(key, value) } },
                    )
                }
                .toString()

        val body =
            client
                .submitForm(
                    url = "https://interface3.music.163.com/eapi/song/lyric/v1",
                    formParameters =
                        parameters { append("params", eapiParams("/api/song/lyric/v1", payload)) },
                ) {
                    header(
                        HttpHeaders.Cookie,
                        header.entries.joinToString("; ") { "${it.key}=${it.value}" },
                    )
                }
                .bodyAsText()

        val json = Json.parseToJsonElement(body).jsonObject
        require(json.long("code") == 200L) { "NetEase eapi code ${json["code"]}" }
        return json
    }

    companion object {
        private const val TAG = "NeteaseProvider"
        private const val EAPI_KEY = "e82ckenh8dichen8"

        /** Signs and encrypts an eapi request body for [apiPath] (e.g. "/api/song/lyric/v1"). */
        fun eapiParams(apiPath: String, payload: String): String {
            val digest = md5Hex("nobody${apiPath}use${payload}md5forencrypt".encodeToByteArray())
            val message = "$apiPath-36cd479b6b5-$payload-36cd479b6b5-$digest"
            return aesEcbEncrypt(message.encodeToByteArray(), EAPI_KEY.encodeToByteArray())
                .toHex()
                .uppercase()
        }
    }
}
