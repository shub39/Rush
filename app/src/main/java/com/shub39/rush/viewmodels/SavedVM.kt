package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.data.listener.MediaListenerImpl
import com.shub39.rush.domain.enums.Sources
import com.shub39.rush.domain.interfaces.OtherPreferences
import com.shub39.rush.domain.interfaces.SongRepository
import com.shub39.rush.presentation.getMainTitle
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

class SavedVM(
    private val stateLayer: StateLayer,
    private val repo: SongRepository,
    private val datastore: OtherPreferences
) : ViewModel() {

    private var savedJob: Job? = null

    private val _state = stateLayer.savedPageState

    val state = _state.asStateFlow()
        .onStart {
            observeData()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SavedPageState()
        )

    fun onAction(action: SavedPageAction) {
        viewModelScope.launch {
            when (action) {
                is SavedPageAction.ChangeCurrentSong -> fetchLyrics(action.id)

                is SavedPageAction.OnDeleteSong -> repo.deleteSong(action.song.id)

                SavedPageAction.OnToggleAutoChange -> {
                    val newPref = !_state.value.autoChange

                    stateLayer.lyricsState.update {
                        it.copy(
                            autoChange = newPref
                        )
                    }

                    _state.update {
                        it.copy(autoChange = newPref)
                    }

                    if (newPref) MediaListenerImpl.onSeekEagerly()
                }

                SavedPageAction.OnToggleSearchSheet -> {
                    stateLayer.searchSheetState.update {
                        it.copy(
                            visible = !it.visible
                        )
                    }
                }

                is SavedPageAction.UpdateSortOrder -> datastore.updateSortOrder(action.sortOrder)
            }
        }
    }

    private fun observeData() {
        savedJob?.cancel()
        savedJob = viewModelScope.launch {
            repo.getSongs()
                .onEach { songs ->
                    if (songs.isEmpty()) {
                        _state.update {
                            it.copy(
                                songsByTime = emptyList(),
                                songsAsc = emptyList(),
                                songsDesc = emptyList(),
                            )
                        }
                        stateLayer.settingsState.update { it.copy(deleteButtonEnabled = false) }

                        return@onEach
                    }

                    _state.update { state ->
                        state.copy(
                            songsByTime = songs.sortedByDescending { it.dateAdded },
                            songsAsc = songs.sortedBy { it.title },
                            songsDesc = songs.sortedByDescending { it.title },
                        )
                    }

                    stateLayer.settingsState.update {
                        it.copy(deleteButtonEnabled = true)
                    }
                }
                .flowOn(Dispatchers.Default)
                .launchIn(this)

            datastore.getSortOrderFlow()
                .onEach { sortOrder ->
                    _state.update {
                        it.copy(
                            sortOrder = sortOrder
                        )
                    }
                }
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
                sync = result.syncedLyrics != null && (getMainTitle(it.playingSong.title).trim()
                    .equals(getMainTitle(result.title).trim(), ignoreCase = true)),
                selectedLines = emptyMap(),
            )
        }

        _state.update {
            it.copy(currentSong = result)
        }
    }
}