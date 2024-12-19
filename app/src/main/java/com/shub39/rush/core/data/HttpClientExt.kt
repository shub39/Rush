package com.shub39.rush.core.data

import com.shub39.rush.core.domain.SourceError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import com.shub39.rush.core.domain.Result

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, SourceError> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(SourceError.Network.REQUEST_FAILED)
    } catch (e: UnresolvedAddressException) {
        return Result.Error(SourceError.Network.NO_INTERNET)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(SourceError.Data.UNKNOWN)
    }

    return responseToResult(response)
}

suspend inline fun <reified  T> responseToResult(
    response: HttpResponse
): Result<T, SourceError> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(SourceError.Data.PARSE_ERROR)
            }
        }

        else -> Result.Error(SourceError.Data.UNKNOWN)
    }
}