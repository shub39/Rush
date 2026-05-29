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
