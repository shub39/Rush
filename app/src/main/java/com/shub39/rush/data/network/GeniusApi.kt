package com.shub39.rush.data.network

import com.shub39.rush.data.network.dto.genius.GeniusSearchDto
import com.shub39.rush.data.safeCall
import com.shub39.rush.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

class GeniusApi(
    private val client: HttpClient
) {
    suspend fun geniusSearch(query: String): Result<GeniusSearchDto, com.shub39.rush.domain.SourceError> = safeCall {
        client.get(
            urlString = "$BASE_URL/search"
        ) {
            header(HttpHeaders.Authorization, BEARER_TOKEN)
            parameter("q", query)
        }
    }

    private companion object {
        private const val BASE_URL = "https://api.genius.com"
        private const val BEARER_TOKEN = "Bearer ${Tokens.GENIUS_API}"
    }
}