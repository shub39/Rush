package com.shub39.rush.lyrics.presentation.setting

import android.content.Context
import android.net.Uri
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile

sealed interface SettingsPageAction {
    data object OnClearIndexes: SettingsPageAction
    data class OnBatchDownload(val files: List<AudioFile>): SettingsPageAction
    data class OnUpdateMaxLines(val lines: Int): SettingsPageAction
    data class OnUpdateTheme(val theme: String): SettingsPageAction
    data class OnUpdateLyricsColor(val color: String): SettingsPageAction
    data object OnDeleteSongs: SettingsPageAction
    data class OnRestoreSongs(val uri: Uri, val context: Context): SettingsPageAction
    data object OnExportSongs: SettingsPageAction
}