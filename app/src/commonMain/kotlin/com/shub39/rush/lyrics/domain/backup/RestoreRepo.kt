package com.shub39.rush.lyrics.domain.backup

interface RestoreRepo {
    suspend fun restoreSongs(path: String): RestoreResult
}