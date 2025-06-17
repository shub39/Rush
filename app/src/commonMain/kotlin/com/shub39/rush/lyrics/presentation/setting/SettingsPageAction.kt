package com.shub39.rush.lyrics.presentation.setting

import com.materialkolor.PaletteStyle
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.Fonts

sealed interface SettingsPageAction {
    data class OnSeedColorChange(val color: Int): SettingsPageAction
    data class OnThemeSwitch(val appTheme: AppTheme): SettingsPageAction
    data class OnAmoledSwitch(val amoled: Boolean): SettingsPageAction
    data class OnPaletteChange(val style: PaletteStyle): SettingsPageAction
    data class OnMaterialThemeToggle(val pref: Boolean): SettingsPageAction
    data class OnFontChange(val fonts: Fonts): SettingsPageAction
    data object OnClearIndexes: SettingsPageAction
    data object OnDeleteSongs: SettingsPageAction
    data object ResetBackup: SettingsPageAction
    data class OnRestoreSongs(val path: String): SettingsPageAction
    data object OnExportSongs: SettingsPageAction
}