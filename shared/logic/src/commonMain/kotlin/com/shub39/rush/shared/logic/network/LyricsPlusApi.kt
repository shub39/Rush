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

import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.util.TTMLParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class LyricsPlusApi {
    companion object {
        @Serializable data class TTMLResponse(val ttml: String)

        private val servers =
            listOf(
                "https://lyricsplus.atomix.one", // meow's mirror
                "https://lyricsplus-seven.vercel.app", // jigen's mirror
                "https://lyricsplus.prjktla.workers.dev", // ibra's cf worker
                "https://lyrics-plus-backend.vercel.app", // ibra's vercel
                "https://youlyplus.binimum.org", // binimum's server
            )
    }

    val client by lazy {
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
                socketTimeoutMillis = 3_000
                requestTimeoutMillis = 3_000
            }

            defaultRequest { contentType(ContentType.Application.Json) }

            expectSuccess = true
        }
    }

    suspend fun fetchTTML(title: String, artist: String): String? = coroutineScope {
        val resultChannel = Channel<String?>(servers.size)
        servers.forEach { url ->
            launch {
                val result =
                    safeCall<TTMLResponse> {
                        client.get(urlString = "$url/v1/ttml/get") {
                            parameter("title", title)
                            parameter("artist", artist)
                        }
                    }

                val ttml =
                    when (result) {
                        is Result.Success ->
                            if (TTMLParser.isValidTTML(result.data.ttml)) result.data.ttml else null
                        else -> null
                    }
                resultChannel.send(ttml)
            }
        }

        repeat(servers.size) {
            val res = resultChannel.receive()
            if (res != null) {
                coroutineContext.cancelChildren()
                return@coroutineScope res
            }
        }
        null
    }
}
