package com.shub39.rush.lyrics.data.network

import com.shub39.rush.core.data.safeCall
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SourceError
import com.shub39.rush.lyrics.data.network.dto.genius.GeniusSearchDto
import com.shub39.rush.lyrics.data.network.dto.genius.GeniusSongDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

class GeniusApi(
    private val client: HttpClient
) {
    suspend fun geniusSearch(query: String): Result<GeniusSearchDto, SourceError> = safeCall {
        client.get(
            urlString = "$BASE_URL/search"
        ) {
            header(HttpHeaders.Authorization, BEARER_TOKEN)
            parameter("q", query)
        }
    }


    suspend fun geniusSong(id: Long): Result<GeniusSongDto, SourceError> = safeCall {
        client.get(
            urlString = "$BASE_URL/songs/$id"
        ) {
            header(HttpHeaders.Authorization, BEARER_TOKEN)
        }
    }


    private companion object {
        private const val BASE_URL = "https://api.genius.com"
        private const val BEARER_TOKEN = "Bearer ${Tokens.GENIUS_API}"
    }
}