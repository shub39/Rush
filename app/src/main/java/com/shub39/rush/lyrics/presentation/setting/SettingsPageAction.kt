package com.shub39.rush.lyrics.presentation.setting

import android.net.Uri
import com.materialkolor.PaletteStyle
import com.shub39.rush.core.domain.Fonts
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile

sealed interface SettingsPageAction {
    data class OnSeedColorChange(val color: Int): SettingsPageAction
    data class OnThemeSwitch(val useDarkTheme: Boolean?): SettingsPageAction
    data class OnAmoledSwitch(val amoled: Boolean): SettingsPageAction
    data class OnPaletteChange(val style: PaletteStyle): SettingsPageAction
    data class OnHypnoticToggle(val toggle: Boolean): SettingsPageAction
    data class OnMaterialThemeToggle(val pref: Boolean): SettingsPageAction
    data class OnFontChange(val fonts: Fonts): SettingsPageAction

    data object OnClearIndexes: SettingsPageAction
    data class OnBatchDownload(val files: List<AudioFile>): SettingsPageAction
    data class OnUpdateMaxLines(val lines: Int): SettingsPageAction
    data class OnUpdateLyricsColor(val vibrant: Boolean): SettingsPageAction
    data object OnDeleteSongs: SettingsPageAction
    data object ResetBackup: SettingsPageAction
    data class OnRestoreSongs(val uri: Uri): SettingsPageAction
    data object OnExportSongs: SettingsPageAction
}