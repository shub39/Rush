package com.shub39.rush.presentation.setting

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.domain.backup.ExportState
import com.shub39.rush.domain.backup.RestoreState
import com.shub39.rush.domain.dataclasses.Theme

@Stable
@Immutable
data class SettingsPageState(
    val theme: Theme = Theme(),
    val deleteButtonEnabled: Boolean = true,
    val exportState: ExportState = ExportState.Exporting,
    val restoreState: RestoreState = RestoreState.Idle,
)