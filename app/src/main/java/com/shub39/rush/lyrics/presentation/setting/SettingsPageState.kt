package com.shub39.rush.lyrics.presentation.setting

import androidx.compose.runtime.Immutable
import com.shub39.rush.core.data.Theme
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile

@Immutable
data class SettingsPageState(
    val theme: Theme = Theme(),
    val maxLines: Int = 6,
    val fullscreen: Boolean = true,
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