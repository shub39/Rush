package com.shub39.rush.lyrics.data.backup

import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult

actual class RestoreImpl(): RestoreRepo {
    override suspend fun restoreSongs(path: String): RestoreResult {
        TODO("Not yet implemented")
    }
}