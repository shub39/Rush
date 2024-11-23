package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsPageState(
    val batchDownload: BatchDownload = BatchDownload(),
)

data class BatchDownload(
    val indexes: Map<Int, Boolean> = emptyMap(),
    val isDownloading: Boolean = false,
    val error: Int? = null
)