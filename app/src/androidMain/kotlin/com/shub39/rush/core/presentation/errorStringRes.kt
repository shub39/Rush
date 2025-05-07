package com.shub39.rush.core.presentation

import com.shub39.rush.core.domain.Error
import com.shub39.rush.core.domain.SourceError
import org.jetbrains.compose.resources.StringResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.io_error
import rush.app.generated.resources.no_internet
import rush.app.generated.resources.no_results
import rush.app.generated.resources.parse_error
import rush.app.generated.resources.request_failed
import rush.app.generated.resources.unknown_error

fun errorStringRes(error: Error): StringResource {
    return when (error) {
        SourceError.Data.NO_RESULTS -> Res.string.no_results
        SourceError.Data.PARSE_ERROR -> Res.string.parse_error
        SourceError.Data.IO_ERROR -> Res.string.io_error
        SourceError.Data.UNKNOWN -> Res.string.unknown_error
        SourceError.Network.NO_INTERNET -> Res.string.no_internet
        SourceError.Network.REQUEST_FAILED -> Res.string.request_failed
    }
}