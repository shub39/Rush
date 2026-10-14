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
package com.shub39.rush.shared.ui.setting

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.shared.core.backup.ExportState
import com.shub39.rush.shared.core.backup.RestoreState
import com.shub39.rush.shared.core.dataclasses.Changelog
import com.shub39.rush.shared.core.dataclasses.LyricsSourceSetting
import com.shub39.rush.shared.core.dataclasses.OverlaySettings
import com.shub39.rush.shared.core.dataclasses.SyncOffsetState
import com.shub39.rush.shared.core.dataclasses.Theme
import com.shub39.rush.shared.core.enums.BackgroundLyricsMode

@Stable
@Immutable
data class SettingsPageState(
    val theme: Theme = Theme(),
    val deleteButtonEnabled: Boolean = true,
    val exportState: ExportState = ExportState.Exporting,
    val restoreState: RestoreState = RestoreState.Idle,
    val changelog: Changelog = emptyList(),
    val lyricsSources: List<LyricsSourceSetting> = emptyList(),
    val syncOffset: SyncOffsetState = SyncOffsetState(),
    val background: BackgroundLyricsSettings = BackgroundLyricsSettings(),
)

/** Background lyrics (floating window or notification) settings and platform status. */
data class BackgroundLyricsSettings(
    val supported: Boolean = false,
    val mode: BackgroundLyricsMode = BackgroundLyricsMode.OFF,
    val running: Boolean = false,
    val overlayPermission: Boolean = false,
    val overlay: OverlaySettings = OverlaySettings(),
    /** Whether Android battery optimization is off for the app, so it can keep running. */
    val ignoringBatteryOptimizations: Boolean = false,
    val manufacturer: String = "",
)
