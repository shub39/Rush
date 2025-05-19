package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.runtime.Immutable
import com.shub39.rush.core.data.Theme
import com.shub39.rush.lyrics.domain.AudioFile
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState

@Immutable
data class SettingsPageState(
    val theme: Theme = Theme(),
    val batchDownload: BatchDownload = BatchDownload(),
    val exportState: ExportState = ExportState.IDLE,
    val restoreState: RestoreState = RestoreState.IDLE
)

data class BatchDownload(
    val indexes: Map<Int, Boolean> = emptyMap(),
    val audioFiles: List<AudioFile> = emptyList(),
    val isDownloading: Boolean = false,
    val isLoadingFiles: Boolean = false,
    val error: Int? = null
)