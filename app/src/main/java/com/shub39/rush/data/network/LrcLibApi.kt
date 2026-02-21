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

import com.shub39.rush.data.network.dto.lrclib.LrcGetDto
import com.shub39.rush.data.safeCall
import com.shub39.rush.domain.Result
import com.shub39.rush.domain.SourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class LrcLibApi(private val client: HttpClient) {
    suspend fun getLrcLyrics(trackName: String, artistName: String): LrcGetDto? {
        val result =
            safeCall<LrcGetDto> {
                client.get(urlString = "$LRC_BASE_URL/api/get") {
                    parameter("track_name", trackName)
                    parameter("artist_name", artistName)
                }
            }

        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> null
        }
    }

    suspend fun searchLrcLyrics(
        trackName: String,
        artistName: String,
    ): Result<List<LrcGetDto>, SourceError> = safeCall {
        client.get(urlString = "$LRC_BASE_URL/api/search") {
            parameter("track_name", trackName)
            parameter("artist_name", artistName)
        }
    }

    private companion object {
        private const val LRC_BASE_URL = "https://lrclib.net"
    }
}
