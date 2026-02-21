/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
