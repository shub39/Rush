package com.shub39.rush.lyrics.presentation.setting

import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.lyrics.domain.backup.ExportState
import com.shub39.rush.lyrics.domain.backup.RestoreState

data class SettingsPageState(
    val theme: Theme = Theme(),
    val deleteButtonEnabled: Boolean = true,
    val exportState: ExportState = ExportState.IDLE,
    val restoreState: RestoreState = RestoreState.IDLE
)