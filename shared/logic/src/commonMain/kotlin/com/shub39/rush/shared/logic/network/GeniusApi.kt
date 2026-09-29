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
package com.shub39.rush.shared.logic.network

import com.shub39.rush.logic.GENIUS_API_TOKEN
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.logic.network.dto.genius.GeniusFetchDto
import com.shub39.rush.shared.logic.network.dto.genius.GeniusResponse
import com.shub39.rush.shared.logic.network.dto.genius.GeniusSearchDto
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single

@Single
class GeniusApi {
    private val client by lazy {
        HttpClient {
            install(ContentNegotiation) { json(json = Json { ignoreUnknownKeys = true }) }

            install(HttpTimeout) {
                socketTimeoutMillis = 20_000
                requestTimeoutMillis = 20_000
            }

            defaultRequest { contentType(ContentType.Application.Json) }
        }
    }

    suspend fun geniusSearch(query: String): Result<GeniusSearchDto, SourceError> = safeCall {
        client.get(urlString = "$BASE_URL/search") {
            header(HttpHeaders.Authorization, BEARER_TOKEN)
            parameter("q", query)
        }
    }

    suspend fun getGeniusLyrics(id: Long): Result<String, SourceError> {
        return when (val result = geniusFetch(id)) {
            is Result.Success -> {
                val lyrics = result.data.response.song.lyrics
                if (lyrics == null) {
                    Result.Error<String, SourceError>(
                        SourceError.Data.UNKNOWN,
                        "No valid response from Genius",
                    )
                }

                val extractedLyrics = extractPlainLyrics(lyrics.toString())
                if (extractedLyrics.isBlank()) {
                    Result.Error<String, SourceError>(
                        SourceError.Data.UNKNOWN,
                        "Blank response from Genius",
                    )
                }

                Result.Success(extractPlainLyrics(lyrics.toString()))
            }
            is Result.Error -> Result.Error(error = result.error, message = result.message)
        }
    }

    // requires special api key
    private suspend fun geniusFetch(id: Long): Result<GeniusFetchDto, SourceError> = safeCall {
        client.get(urlString = "$BASE_URL/songs/$id") {
            header(HttpHeaders.Authorization, BEARER_TOKEN)
        }
    }

    private companion object {
        private const val BASE_URL = "https://api.genius.com"
        private const val BEARER_TOKEN = "Bearer $GENIUS_API_TOKEN"

        fun extractPlainLyrics(jsonString: String): String {
            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<GeniusResponse>(jsonString)
            val root = response.dom ?: return ""

            val stringBuilder = StringBuilder()
            parseChildren(root.children ?: emptyList(), stringBuilder)

            // Clean up multiple consecutive line breaks if needed
            return stringBuilder
                .toString()
                .lines()
                .dropWhile { it.isBlank() }
                .dropLastWhile { it.isBlank() }
                .joinToString("\n")
        }

        private fun parseChildren(children: List<JsonElement>, sb: StringBuilder) {
            for (element in children) {
                when (element) {
                    is JsonPrimitive -> {
                        if (element.isString) {
                            sb.append(element.content)
                        }
                    }
                    is JsonObject -> {
                        val tag = element["tag"]?.jsonPrimitive?.contentOrNull
                        if (tag == "br") {
                            sb.append("\n")
                        } else {
                            val subChildren = element["children"]?.jsonArray
                            if (subChildren != null) {
                                parseChildren(subChildren, sb)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
