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
package com.shub39.rush.data

import com.shub39.rush.domain.Result
import com.shub39.rush.domain.SourceError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, SourceError> {
    val response =
        try {
            execute()
        } catch (e: SocketTimeoutException) {
            return Result.Error(
                SourceError.Network.REQUEST_FAILED,
                "SocketTimeoutException: ${e.message}",
            )
        } catch (e: UnresolvedAddressException) {
            return Result.Error(
                SourceError.Network.NO_INTERNET,
                "UnresolvedAddressException: ${e.message}",
            )
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(
                SourceError.Data.UNKNOWN,
                "Unexpected exception: " +
                    "${e::class.simpleName} - " +
                    "${e.message}\n${e.stackTraceToString().take(500)}",
            )
        }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, SourceError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(
                    SourceError.Data.PARSE_ERROR,
                    "Parse error for ${T::class.simpleName}: ${e.message}",
                )
            }
        }

        429 ->
            Result.Error(
                SourceError.Network.REQUEST_FAILED,
                "Too many requests: ${response.request.url}",
            )

        else ->
            Result.Error(
                SourceError.Data.UNKNOWN,
                "HTTP ${response.status.value}: ${response.status.description}\nURL: ${response.request.url}",
            )
    }
}
