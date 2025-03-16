package com.shub39.rush.lyrics.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.core.presentation.getMainTitle
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.presentation.lyrics.toSongUi
import com.shub39.rush.lyrics.presentation.saved.SavedPageAction
import com.shub39.rush.lyrics.presentation.saved.SavedPageState
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
): ViewModel() {

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
                    stateLayer.lyricsState.update {
                        it.copy(
                            autoChange = !it.autoChange
                        )
                    }
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

    private fun observeData() = viewModelScope.launch {
        savedJob?.cancel()
        savedJob = launch {
            repo.getSongs()
                .onEach { songs ->
                    _state.update { state ->
                        state.copy(
                            songsByTime = songs.sortedByDescending { it.dateAdded },
                            songsAsc = songs.sortedBy { it.title },
                            songsDesc = songs.sortedByDescending { it.title },
                            groupedAlbum = songs.groupBy { it.album ?: "???" }.entries.toList(),
                            groupedArtist = songs.groupBy { it.artists }.entries.toList()
                        )
                    }
                }
                .launchIn(this)

            datastore.getOnboardingDoneFlow()
                .onEach { done ->
                    _state.update {
                        it.copy(
                            onboarding = done
                        )
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
                sync = result.syncedLyrics != null && (getMainTitle(it.playingSong.title).trim().lowercase() == getMainTitle(result.title).trim().lowercase()),
                selectedLines = emptyMap(),
                error = null
            )
        }
    }
}