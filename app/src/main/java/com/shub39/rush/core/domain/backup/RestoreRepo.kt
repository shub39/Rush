package com.shub39.rush.core.domain.backup

interface RestoreRepo {
    suspend fun restoreSongs(path: String): RestoreResult
}