package com.shub39.rush.lyrics.presentation.setting

import com.shub39.rush.lyrics.presentation.setting.component.AudioFile

sealed interface SettingsPageAction {
    data object OnClearIndexes: SettingsPageAction
    data class OnBatchDownload(val files: List<AudioFile>): SettingsPageAction
    data class OnUpdateMaxLines(val lines: Int): SettingsPageAction
    data class OnUpdateTheme(val theme: String): SettingsPageAction
    data class OnUpdateLyricsColor(val color: String): SettingsPageAction
    data object OnDeleteSongs: SettingsPageAction
}