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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.data.listener.MediaListenerImpl
import com.shub39.rush.domain.enums.Sources
import com.shub39.rush.domain.getMainTitle
import com.shub39.rush.domain.interfaces.OtherPreferences
import com.shub39.rush.domain.interfaces.SongRepository
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.SearchState
import com.shub39.rush.presentation.lyrics.toSongUi
import com.shub39.rush.presentation.saved.SavedPageAction
import com.shub39.rush.presentation.saved.SavedPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SavedVM(
    private val stateLayer: SharedStates,
    private val repo: SongRepository,
    private val datastore: OtherPreferences,
) : ViewModel() {

    private var savedJob: Job? = null

    private val _state = stateLayer.savedPageState

    val state =
        _state
            .asStateFlow()
            .onStart { observeData() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SavedPageState())

    fun onAction(action: SavedPageAction) {
        viewModelScope.launch {
            when (action) {
                is SavedPageAction.ChangeCurrentSong -> fetchLyrics(action.id)

                is SavedPageAction.OnDeleteSong -> repo.deleteSong(action.song.id)

                SavedPageAction.OnToggleAutoChange -> {
                    val newPref = !_state.value.autoChange

                    stateLayer.lyricsState.update { it.copy(autoChange = newPref) }

                    _state.update { it.copy(autoChange = newPref) }

                    if (newPref) MediaListenerImpl.onSeekEagerly()
                }

                SavedPageAction.OnToggleSearchSheet -> {
                    stateLayer.searchSheetState.update { it.copy(visible = !it.visible) }
                }

                is SavedPageAction.UpdateSortOrder -> datastore.updateSortOrder(action.sortOrder)
            }
        }
    }

    private fun observeData() {
        savedJob?.cancel()
        savedJob =
            viewModelScope.launch {
                repo
                    .getSongs()
                    .onEach { songs ->
                        if (songs.isEmpty()) {
                            _state.update {
                                it.copy(
                                    songsByTime = emptyList(),
                                    songsAsc = emptyList(),
                                    songsDesc = emptyList(),
                                )
                            }

                            return@onEach
                        }

                        _state.update { state ->
                            state.copy(
                                songsByTime = songs.sortedByDescending { it.dateAdded },
                                songsAsc = songs.sortedBy { it.title },
                                songsDesc = songs.sortedByDescending { it.title },
                            )
                        }
                    }
                    .flowOn(Dispatchers.Default)
                    .launchIn(this)

                datastore
                    .getSortOrderFlow()
                    .onEach { sortOrder -> _state.update { it.copy(sortOrder = sortOrder) } }
                    .launchIn(this)
            }
    }

    private suspend fun fetchLyrics(id: Long) {
        if (stateLayer.lyricsState.value.lyricsState is LyricsState.Fetching) return

        val result = repo.getSong(id).toSongUi()

        stateLayer.lyricsState.update {
            it.copy(
                lyricsState = LyricsState.Loaded(song = result),
                source = if (result.lyrics.isNotEmpty()) Sources.LRCLIB else Sources.GENIUS,
                searchState = SearchState.Idle,
                syncedAvailable = result.syncedLyrics != null,
                sync =
                    result.syncedLyrics != null &&
                        (getMainTitle(it.playingSong.title)
                            .trim()
                            .equals(getMainTitle(result.title).trim(), ignoreCase = true)),
                selectedLines = emptyMap(),
            )
        }

        _state.update { it.copy(currentSong = result) }
    }
}
