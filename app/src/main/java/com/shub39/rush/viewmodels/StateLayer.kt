package com.shub39.rush.viewmodels

import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.saved.SavedPageState
import com.shub39.rush.search_sheet.SearchSheetState
import com.shub39.rush.setting.SettingsPageState
import com.shub39.rush.share.SharePageState
import kotlinx.coroutines.flow.MutableStateFlow

// all the states in a single place so that they can be updated from different viewmodels
class StateLayer {
    val lyricsState = MutableStateFlow(LyricsPageState())
    val searchSheetState = MutableStateFlow(SearchSheetState())
    val savedPageState = MutableStateFlow(SavedPageState())
    val sharePageState = MutableStateFlow(SharePageState())
    val settingsState = MutableStateFlow(SettingsPageState())
}