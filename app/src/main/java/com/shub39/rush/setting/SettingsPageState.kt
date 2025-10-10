package com.shub39.rush.setting

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.core.domain.backup.ExportState
import com.shub39.rush.core.domain.backup.RestoreState
import com.shub39.rush.core.domain.data_classes.Theme

@Stable
@Immutable
data class SettingsPageState(
    val theme: Theme = Theme(),
    val deleteButtonEnabled: Boolean = true,
    val exportState: ExportState = ExportState.Exporting,
    val restoreState: RestoreState = RestoreState.Idle,
    val isProUser: Boolean = false,
    val onBoardingDone: Boolean = true,
    val showPaywall: Boolean = false
)