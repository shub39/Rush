package com.shub39.rush.viewmodels

import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.saved.SavedPageState
import com.shub39.rush.presentation.searchsheet.SearchSheetState
import com.shub39.rush.presentation.share.SharePageState
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single

// all the states in a single place so that they can be updated from different viewmodels
@Single
class SharedStates {
    val lyricsState = MutableStateFlow(LyricsPageState())
    val searchSheetState = MutableStateFlow(SearchSheetState())
    val savedPageState = MutableStateFlow(SavedPageState())
    val sharePageState = MutableStateFlow(SharePageState())
}