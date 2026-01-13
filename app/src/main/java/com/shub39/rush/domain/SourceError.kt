package com.shub39.rush.domain

sealed interface SourceError: Error {
    enum class Network : SourceError {
        NO_INTERNET,
        REQUEST_FAILED,
    }
    enum class Data: SourceError {
        NO_RESULTS,
        PARSE_ERROR,
        IO_ERROR,
        UNKNOWN
    }
}