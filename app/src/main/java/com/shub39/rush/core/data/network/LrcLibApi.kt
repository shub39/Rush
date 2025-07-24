package com.shub39.rush.core.data.network

import com.shub39.rush.core.data.network.dto.lrclib.LrcGetDto
import com.shub39.rush.core.data.safeCall
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class LrcLibApi(
    private val client: HttpClient
) {
    suspend fun getLrcLyrics(
        trackName: String,
        artistName: String
    ): LrcGetDto? {
        val result = safeCall<LrcGetDto> {
            client.get(
                urlString = "$LRC_BASE_URL/api/get"
            ) {
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
        artistName: String
    ): Result<List<LrcGetDto>, SourceError> = safeCall {
        client.get(
            urlString = "$LRC_BASE_URL/api/search"
        ) {
            parameter("track_name", trackName)
            parameter("artist_name", artistName)
        }
    }


    private companion object {
        private const val LRC_BASE_URL = "https://lrclib.net"
    }
}