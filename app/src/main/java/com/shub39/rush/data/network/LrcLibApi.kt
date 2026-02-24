/*
 * Copyright (C) 2026  Shubham Gorai
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
package com.shub39.rush.data.network

import com.shub39.rush.BuildConfig
import com.shub39.rush.data.network.dto.lrclib.LrcGetDto
import com.shub39.rush.data.safeCall
import com.shub39.rush.domain.Result
import com.shub39.rush.domain.SourceError
import com.shub39.rush.presentation.getMainArtist
import com.shub39.rush.presentation.getMainTitle
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class LrcLibApi {
    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 15_000
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
            }

            if (BuildConfig.DEBUG) {
                install(Logging) {
                    logger =
                        object : Logger {
                            override fun log(message: String) {
                                println(message)
                            }
                        }
                }
            }

            defaultRequest {
                url("https://lrclib.net")
                header(
                    HttpHeaders.UserAgent,
                    "Rush v${BuildConfig.VERSION_NAME} (https://github.com/shub39/Rush)",
                )
            }

            expectSuccess = true
        }
    }

    suspend fun getLrcLyrics(trackName: String, artistName: String): LrcGetDto? {
        return when (val result = searchLrcLyrics(trackName, artistName)) {
            is Result.Error -> null
            is Result.Success ->
                result.data.firstOrNull { it.syncedLyrics != null || it.plainLyrics != null }
        }
    }

    suspend fun searchLrcLyrics(
        trackName: String,
        artistName: String,
        albumName: String? = null,
    ): Result<List<LrcGetDto>, SourceError> = safeCall {
        val cleanedTitle = getMainTitle(trackName)
        val cleanedArtist = getMainArtist(artistName)

        // Strategy 1: Search with cleaned title and artist
        var results =
            queryLyricsWithParams(
                    trackName = cleanedTitle,
                    artistName = cleanedArtist,
                    albumName = albumName,
                )
                .filter { it.syncedLyrics != null || it.plainLyrics != null }

        if (results.isNotEmpty()) return Result.Success(results)

        // Strategy 2: Search with cleaned title only (artist might be different)
        results =
            queryLyricsWithParams(trackName = cleanedTitle).filter {
                it.syncedLyrics != null || it.plainLyrics != null
            }

        if (results.isNotEmpty()) return Result.Success(results)

        // Strategy 3: Use q parameter with combined search
        results =
            queryLyricsWithParams(query = "$cleanedArtist $cleanedTitle").filter {
                it.syncedLyrics != null || it.plainLyrics != null
            }

        if (results.isNotEmpty()) return Result.Success(results)

        // Strategy 4: Use q parameter with just title
        results =
            queryLyricsWithParams(query = cleanedTitle).filter {
                it.syncedLyrics != null || it.plainLyrics != null
            }

        if (results.isNotEmpty()) return Result.Success(results)

        // Strategy 5: Try original title if different from cleaned
        if (cleanedTitle != trackName.trim()) {
            results =
                queryLyricsWithParams(trackName = trackName.trim(), artistName = artistName.trim())
                    .filter { it.syncedLyrics != null || it.plainLyrics != null }
        }

        return Result.Error(SourceError.Data.NO_RESULTS)
    }

    private suspend fun queryLyricsWithParams(
        trackName: String? = null,
        artistName: String? = null,
        albumName: String? = null,
        query: String? = null,
    ): List<LrcGetDto> =
        runCatching {
                client
                    .get("/api/search") {
                        if (query != null) parameter("q", query)
                        if (trackName != null) parameter("track_name", trackName)
                        if (artistName != null) parameter("artist_name", artistName)
                        if (albumName != null) parameter("album_name", albumName)
                    }
                    .body<List<LrcGetDto>>()
            }
            .getOrDefault(emptyList())
}
