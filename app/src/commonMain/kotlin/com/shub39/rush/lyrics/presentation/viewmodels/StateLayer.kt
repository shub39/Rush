package com.shub39.rush.lyrics.presentation.viewmodels

import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.saved.SavedPageState
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheetState
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState
import com.shub39.rush.lyrics.presentation.share.SharePageState
import kotlinx.coroutines.flow.MutableStateFlow

// all the states in a single place so that they can be updated from different viewmodels
class StateLayer {
    val lyricsState = MutableStateFlow(LyricsPageState())
    val searchSheetState = MutableStateFlow(SearchSheetState())
    val savedPageState = MutableStateFlow(SavedPageState())
    val sharePageState = MutableStateFlow(SharePageState())
    val settingsState = MutableStateFlow(SettingsPageState())
}