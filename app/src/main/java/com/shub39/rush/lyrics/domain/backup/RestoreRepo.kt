package com.shub39.rush.lyrics.domain.backup

import android.content.Context
import android.net.Uri

interface RestoreRepo {
    suspend fun restoreSongs(uri: Uri, context: Context): RestoreResult
}