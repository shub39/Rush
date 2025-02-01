package com.shub39.rush.lyrics.domain.backup

import android.net.Uri

interface RestoreRepo {
    suspend fun restoreSongs(uri: Uri): RestoreResult
}