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

import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.domain.enums.Fonts
import com.shub39.rush.domain.enums.PaletteStyle

sealed interface SettingsPageAction {
    data class OnSeedColorChange(val color: Int) : SettingsPageAction

    data class OnThemeSwitch(val appTheme: AppTheme) : SettingsPageAction

    data class OnAmoledSwitch(val amoled: Boolean) : SettingsPageAction

    data class OnPaletteChange(val style: PaletteStyle) : SettingsPageAction

    data class OnMaterialThemeToggle(val pref: Boolean) : SettingsPageAction

    data class OnFontChange(val fonts: Fonts) : SettingsPageAction

    data object OnDeleteSongs : SettingsPageAction

    data object ResetBackup : SettingsPageAction

    data class OnRestoreSongs(val path: String) : SettingsPageAction

    data object OnExportSongs : SettingsPageAction
}
