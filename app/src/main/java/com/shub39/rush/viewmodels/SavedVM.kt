package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.data.listener.MediaListenerImpl
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.domain.SongRepo
import com.shub39.rush.core.domain.enums.Sources
import com.shub39.rush.core.presentation.getMainTitle
import com.shub39.rush.lyrics.toSongUi
import com.shub39.rush.saved.SavedPageAction
import com.shub39.rush.saved.SavedPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedVM(
    private val stateLayer: StateLayer,
    private val repo: SongRepo,
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

    private fun observeData() = viewModelScope.launch(Dispatchers.Default) {
        savedJob?.cancel()
        savedJob = launch {
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
        if (stateLayer.lyricsState.value.fetching.first) return

        val result = repo.getSong(id).toSongUi()

        stateLayer.lyricsState.update {
            it.copy(
                song = result,
                source = if (result.lyrics.isNotEmpty()) Sources.LrcLib else Sources.Genius,
                syncedAvailable = result.syncedLyrics != null,
                sync = result.syncedLyrics != null && (getMainTitle(it.playingSong.title).trim()
                    .lowercase() == getMainTitle(result.title).trim().lowercase()),
                selectedLines = emptyMap(),
                error = null
            )
        }

        _state.update {
            it.copy(currentSong = result)
        }
    }
}