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
import com.shub39.rush.shared.core.util.TrackMatcher
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.koin.core.annotation.Single

/** QQ Music, line synced lyrics only. */
@Single
class QQMusicProvider : LyricsProvider {
    override val id = LyricsProviderId.QQ_MUSIC

    private data class Song(
        val mid: String,
        val name: String,
        val artists: String,
        val album: String?,
        val durationMs: Long?,
    )

    private val client by lazy {
        lyricsHttpClient {
            defaultRequest {
                header(HttpHeaders.UserAgent, DESKTOP_USER_AGENT)
                header(HttpHeaders.Referrer, "https://y.qq.com/portal/player.html")
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
                .get("https://c.y.qq.com/soso/fcgi-bin/client_search_cp") {
                    parameter("w", "${getMainTitle(query.title)} ${getMainArtist(query.artist)}")
                    parameter("format", "json")
                    parameter("p", 1)
                    parameter("n", 10)
                    parameter("cr", 1)
                    parameter("new_json", 1)
                    parameter("inCharset", "utf8")
                    parameter("outCharset", "utf-8")
                    parameter("platform", "yqq.json")
                }
                .bodyAsText()

        val songs = parseJson(body).obj("data").obj("song").arr("list") ?: return emptyList()
        return songs.mapNotNull { song ->
            Song(
                mid = song.str("mid") ?: song.str("songmid") ?: return@mapNotNull null,
                name =
                    song.str("name")
                        ?: song.str("title")
                        ?: song.str("songname")
                        ?: return@mapNotNull null,
                artists =
                    song.arr("singer")?.mapNotNull { it.str("name") }?.joinToString(", ") ?: "",
                album = song.obj("album").str("name") ?: song.str("albumname"),
                durationMs = song.long("interval")?.times(1000),
            )
        }
    }

    private suspend fun fetchLyrics(song: Song): LyricsCandidate? {
        val body =
            client
                .get("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg") {
                    parameter("songmid", song.mid)
                    parameter("format", "json")
                    parameter("g_tk", 5381)
                    parameter("loginUin", 0)
                    parameter("hostUin", 0)
                    parameter("inCharset", "utf8")
                    parameter("outCharset", "utf-8")
                    parameter("notice", 0)
                    parameter("platform", "yqq.json")
                    parameter("needNewCode", 0)
                }
                .bodyAsText()

        val lrc =
            parseJson(body).str("lyric")?.let(::decodeLyric)?.let(LrcUtils::normalize)
                ?: return null

        return LyricsCandidate(
            provider = id,
            title = song.name,
            artist = song.artists,
            album = song.album,
            durationMs = song.durationMs,
            plain = LrcUtils.toPlain(lrc),
            lrc = lrc,
        )
    }

    companion object {
        private val numericEntity = Regex("""&#(\d+);""")

        /** QQ answers JSONP ("MusicJsonCallback({...})") unless format=json is honoured. */
        fun parseJson(body: String): JsonElement {
            val trimmed = body.trim()
            val json =
                if (trimmed.startsWith("{")) trimmed
                else trimmed.substringAfter("(").substringBeforeLast(")")
            return Json.parseToJsonElement(json)
        }

        /** Lyrics are base64 encoded, or html escaped when sent with nobase64=1. */
        fun decodeLyric(value: String): String {
            if (value.isBlank()) return value
            if (value.contains('[') || value.contains("&#")) {
                return numericEntity
                    .replace(value) { it.groupValues[1].toInt().toChar().toString() }
                    .replace("&amp;", "&")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'")
            }
            return runCatching { Base64.Default.decode(value.trim()).decodeToString() }
                .getOrDefault(value)
        }
    }
}
