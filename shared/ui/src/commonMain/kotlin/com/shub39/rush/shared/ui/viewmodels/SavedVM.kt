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
package com.shub39.rush.shared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.OtherPreferences
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.ui.rushmode.RushAction
import com.shub39.rush.shared.ui.rushmode.RushController
import com.shub39.rush.shared.ui.saved.SavedPageAction
import com.shub39.rush.shared.ui.saved.SavedPageState
import com.shub39.rush.shared.ui.session.SessionController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SavedVM(
    private val sessionController: SessionController,
    private val rushController: RushController,
    @Provided private val repo: SongRepository,
    @Provided private val datastore: OtherPreferences,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    /** Only the saved library and its sort order belong to this screen. */
    private val library = MutableStateFlow(SavedPageState())

    val state =
        combine(library, sessionController.state, rushController.state) { saved, session, rush ->
                saved.copy(
                    nowPlaying = session.song,
                    rush = rush,
                    extractedColors = session.extractedColors,
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedPageState())

    init {
        observeData()
    }

    fun onAction(action: SavedPageAction) {
        when (action) {
            is SavedPageAction.ChangeCurrentSong ->
                viewModelScope.launch { sessionController.loadSaved(action.id) }

            is SavedPageAction.OnDeleteSong -> {
                analytics.trackEvent(
                    AnalyticsEvent.SONG_DELETED,
                    mapOf("title" to action.song.title, "artists" to action.song.artists),
                )
                viewModelScope.launch { repo.deleteSong(action.song.id) }
            }

            SavedPageAction.OnToggleRushMode -> rushController.onAction(RushAction.Toggle)

            SavedPageAction.OnRequestMediaAccess ->
                rushController.onAction(RushAction.OnRequestMediaAccess)

            is SavedPageAction.UpdateSortOrder ->
                viewModelScope.launch { datastore.updateSortOrder(action.sortOrder) }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            repo
                .getSongs()
                .onEach { songs ->
                    library.update { current ->
                        current.copy(
                            songsByTime = songs.sortedByDescending { it.dateAdded },
                            songsAsc = songs.sortedBy { it.title },
                            songsDesc = songs.sortedByDescending { it.title },
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .launchIn(this)
        }

        datastore
            .getSortOrderFlow()
            .onEach { sortOrder -> library.update { it.copy(sortOrder = sortOrder) } }
            .launchIn(viewModelScope)
    }
}
