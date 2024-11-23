package com.shub39.rush.core.presentation

import com.shub39.rush.core.domain.Error
import com.shub39.rush.core.domain.SourceError
import com.shub39.rush.R

fun errorStringRes(error: Error): Int {
    return when (error) {
        SourceError.Data.NO_RESULTS -> R.string.no_results
        SourceError.Data.PARSE_ERROR -> R.string.parse_error
        SourceError.Data.IO_ERROR -> R.string.io_error
        SourceError.Data.UNKNOWN -> R.string.unknown_error
        SourceError.Network.NO_INTERNET -> R.string.no_internet
        SourceError.Network.REQUEST_FAILED -> R.string.request_failed
    }
}