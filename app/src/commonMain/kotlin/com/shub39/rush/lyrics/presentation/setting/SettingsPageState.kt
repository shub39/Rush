package com.shub39.rush.lyrics.presentation.setting

import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.lyrics.domain.AudioFile
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState

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