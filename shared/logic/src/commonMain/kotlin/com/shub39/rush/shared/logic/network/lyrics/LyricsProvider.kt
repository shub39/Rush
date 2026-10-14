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

import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

/** A lyrics source. Implementations may throw, use [safeSearch] to call them. */
interface LyricsProvider {
    val id: LyricsProviderId

    /** Lyrics for the tracks matching [query], best match first, at most [limit] results. */
    suspend fun search(query: TrackQuery, limit: Int): List<LyricsCandidate>
}

private const val TAG = "LyricsProvider"

/** Calls [LyricsProvider.search], turning every failure into a [Result.Error]. */
suspend fun LyricsProvider.safeSearch(
    query: TrackQuery,
    limit: Int,
    timeoutMillis: Long = 15_000,
): Result<List<LyricsCandidate>, SourceError> =
    try {
        val results = withTimeout(timeoutMillis) { search(query, limit) }
        if (results.isEmpty()) Result.Error(SourceError.Data.NO_RESULTS)
        else Result.Success(results)
    } catch (e: TimeoutCancellationException) {
        RushLogger.e(TAG, "${id.displayName}: timed out", e)
        Result.Error(SourceError.Network.REQUEST_FAILED, "${id.displayName}: timeout")
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RushLogger.e(TAG, "${id.displayName}: ${e::class.simpleName} ${e.message}", e)
        val error =
            when (e) {
                is UnresolvedAddressException -> SourceError.Network.NO_INTERNET
                is SocketTimeoutException,
                is HttpRequestTimeoutException,
                is ClientRequestException,
                is ServerResponseException -> SourceError.Network.REQUEST_FAILED
                is SerializationException,
                is IllegalArgumentException -> SourceError.Data.PARSE_ERROR
                else -> SourceError.Data.UNKNOWN
            }
        Result.Error(error, "${id.displayName}: ${e::class.simpleName} ${e.message}")
    }

internal const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/124.0.0.0 Safari/537.36"

internal fun lyricsHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient = HttpClient {
    install(HttpTimeout) {
        connectTimeoutMillis = 8_000
        socketTimeoutMillis = 10_000
        requestTimeoutMillis = 12_000
    }
    expectSuccess = true
    block()
}

// Lenient JSON accessors, provider responses are loosely typed
internal fun JsonElement?.obj(key: String): JsonObject? =
    (this as? JsonObject)?.get(key) as? JsonObject

internal fun JsonElement?.arr(key: String): JsonArray? =
    (this as? JsonObject)?.get(key) as? JsonArray

internal fun JsonElement?.str(key: String): String? =
    ((this as? JsonObject)?.get(key) as? JsonPrimitive)?.contentOrNull

internal fun JsonElement?.long(key: String): Long? {
    val primitive = (this as? JsonObject)?.get(key) as? JsonPrimitive ?: return null
    return primitive.longOrNull ?: primitive.doubleOrNull?.toLong()
}

internal fun JsonElement?.double(key: String): Double? =
    ((this as? JsonObject)?.get(key) as? JsonPrimitive)?.doubleOrNull

internal fun JsonElement?.bool(key: String): Boolean? {
    val primitive = (this as? JsonObject)?.get(key) as? JsonPrimitive ?: return null
    return primitive.booleanOrNull ?: primitive.longOrNull?.let { it != 0L }
}
