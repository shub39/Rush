package com.shub39.rush.setting

import com.shub39.rush.core.domain.backup.ExportState
import com.shub39.rush.core.domain.backup.RestoreState
import com.shub39.rush.core.domain.data_classes.Theme

data class SettingsPageState(
    val theme: Theme = Theme(),
    val deleteButtonEnabled: Boolean = true,
    val exportState: ExportState = ExportState.IDLE,
    val restoreState: RestoreState = RestoreState.IDLE,
    val onBoardingDone: Boolean = true
)