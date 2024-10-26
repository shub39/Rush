package com.shub39.rush.ui.page.setting

import com.shub39.rush.ui.page.setting.component.AudioFile

sealed interface SettingsPageAction {
    object OnClearIndexes: SettingsPageAction
    data class OnBatchDownload(val files: List<AudioFile>): SettingsPageAction
    object OnDeleteSongs: SettingsPageAction
}