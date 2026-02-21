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

import com.shub39.rush.data.network.dto.genius.GeniusSearchDto
import com.shub39.rush.data.safeCall
import com.shub39.rush.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import org.koin.core.annotation.Single

@Single
class GeniusApi(private val client: HttpClient) {
    suspend fun geniusSearch(
        query: String
    ): Result<GeniusSearchDto, com.shub39.rush.domain.SourceError> = safeCall {
        client.get(urlString = "$BASE_URL/search") {
            header(HttpHeaders.Authorization, BEARER_TOKEN)
            parameter("q", query)
        }
    }

    private companion object {
        private const val BASE_URL = "https://api.genius.com"
        private const val BEARER_TOKEN = "Bearer ${Tokens.GENIUS_API}"
    }
}
