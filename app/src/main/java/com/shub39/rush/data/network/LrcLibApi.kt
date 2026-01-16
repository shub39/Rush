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
class LrcLibApi(
    private val client: HttpClient
) {
    suspend fun getLrcLyrics(
        trackName: String,
        artistName: String
    ): LrcGetDto? {
        val result = searchLrcLyrics(
            trackName = trackName,
            artistName = artistName
        )

        return when (result) {
            is Result.Error -> null
            is Result.Success -> {
                if (result.data.any { it.syncedLyrics != null }) {
                    result.data.first { it.syncedLyrics != null }
                } else {
                    result.data.firstOrNull()
                }
            }
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