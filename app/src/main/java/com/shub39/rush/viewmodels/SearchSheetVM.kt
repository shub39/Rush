package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.domain.MediaInterface
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SongRepo
import com.shub39.rush.core.domain.data_classes.ExtractedColors
import com.shub39.rush.core.domain.data_classes.SearchResult
import com.shub39.rush.core.domain.enums.Sources
import com.shub39.rush.core.presentation.errorStringRes
import com.shub39.rush.core.presentation.getMainTitle
import com.shub39.rush.lyrics.toSongUi
import com.shub39.rush.search_sheet.SearchSheetAction
import com.shub39.rush.search_sheet.SearchSheetState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchSheetVM(
    private val stateLayer: StateLayer,
    private val repo: SongRepo,
    private val mediaListener: MediaInterface
) : ViewModel() {

    private val _state = stateLayer.searchSheetState
    private val _lastSearched = MutableStateFlow("")

    val state = _state.asStateFlow()
        .onStart {
            observeSearchSheet()
            observeSongInfo()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SearchSheetState()
        )

    private fun observeSongInfo() {
        viewModelScope.launch {
            mediaListener.songInfoFlow.collect { songInfo ->
                stateLayer.lyricsState.update {
                    it.copy(
                        playingSong = it.playingSong.copy(
                            title = songInfo.first,
                            artist = songInfo.second
                        )
                    )
                }

                if (stateLayer.lyricsState.value.autoChange) {
                    searchSong("${songInfo.first} ${songInfo.second}".trim())
                }
            }
        }
    }

    fun onAction(action: SearchSheetAction) {
        viewModelScope.launch {
            when (action) {
                is SearchSheetAction.OnCardClicked -> {
                    _state.update {
                        it.copy(
                            visible = !it.visible
                        )
                    }

                    fetchLyrics(action.id)

                    _state.update {
                        it.copy(
                            searchQuery = ""
                        )
                    }
                }

                is SearchSheetAction.OnQueryChange -> {
                    _state.update {
                        it.copy(
                            searchQuery = action.query
                        )
                    }
                }

                SearchSheetAction.OnToggleSearchSheet -> {
                    _state.update {
                        it.copy(
                            visible = !it.visible
                        )
                    }

                    _state.update {
                        it.copy(
                            searchQuery = ""
                        )
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchSheet() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update {
                            it.copy(
                                error = null
                            )
                        }
                    }

                    query.length >= 3 -> {
                        _state.update {
                            it.copy(
                                localSearchResults = localSearch(query)
                            )
                        }

                        searchSong(query, false)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchSong(
        query: String,
        fetch: Boolean = stateLayer.lyricsState.value.autoChange,
    ) {
        if (query.isEmpty() || stateLayer.lyricsState.value.searching.first || query == _lastSearched.value) return

        viewModelScope.launch {
            stateLayer.lyricsState.update {
                it.copy(
                    searching = Pair(true, query),
                    sync = false
                )
            }

            _state.update {
                it.copy(
                    isSearching = true
                )
            }

            try {
                when (val result = repo.searchGenius(query)) {
                    is Result.Error -> {
                        stateLayer.lyricsState.update {
                            it.copy(
                                error = errorStringRes(result.error)
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                searchResults = result.data,
                            )
                        }

                        if (fetch) _lastSearched.value = query

                        stateLayer.lyricsState.update {
                            it.copy(
                                error = null
                            )
                        }
                    }
                }

            } finally {
                stateLayer.lyricsState.update {
                    it.copy(
                        searching = Pair(false, "")
                    )
                }

                _state.update {
                    it.copy(
                        isSearching = false
                    )
                }
            }

            if (fetch && stateLayer.lyricsState.value.error == null && _state.value.searchResults.isNotEmpty()) {

                fetchLyrics(_state.value.searchResults.first().id)

            } else {
                stateLayer.lyricsState.update {
                    it.copy(
                        autoChange = false
                    )
                }
            }
        }
    }

    private suspend fun fetchLyrics(songId: Long) {
        if (stateLayer.lyricsState.value.fetching.first) return

        val song = _state.value.searchResults.find { it.id == songId }

        stateLayer.lyricsState.update {
            it.copy(
                fetching = Pair(true, "${song?.title} - ${song?.artist}"),
                extractedColors = ExtractedColors(),
                sync = false
            )
        }

        try {
            if (songId in stateLayer.savedPageState.value.songsAsc.map { it.id }) {
                val result = repo.getSong(songId).toSongUi()

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

                stateLayer.savedPageState.update {
                    it.copy(currentSong = result)
                }
            } else {
                when (val result = repo.fetchSong(songId)) {
                    is Result.Error -> {
                        stateLayer.lyricsState.update {
                            it.copy(
                                error = errorStringRes(result.error)
                            )
                        }
                    }

                    is Result.Success -> {
                        val retrievedSong = result.data.toSongUi()

                        stateLayer.lyricsState.update {
                            it.copy(
                                song = retrievedSong,
                                source = if (retrievedSong.lyrics.isNotEmpty()) Sources.LrcLib else Sources.Genius,
                                syncedAvailable = retrievedSong.syncedLyrics != null,
                                sync = retrievedSong.syncedLyrics != null && (getMainTitle(it.playingSong.title).trim()
                                    .lowercase() == getMainTitle(retrievedSong.title).trim()
                                    .lowercase()),
                                selectedLines = emptyMap(),
                                error = null
                            )
                        }

                        stateLayer.savedPageState.update {
                            it.copy(currentSong = retrievedSong)
                        }
                    }
                }

            }
        } finally {
            stateLayer.lyricsState.update {
                it.copy(
                    fetching = Pair(false, "")
                )
            }
        }
    }

    private suspend fun localSearch(query: String): List<SearchResult> {
        if (query.isEmpty()) {
            return emptyList()
        }

        val songs = repo.getSong(query)
        val searchResults = mutableListOf<SearchResult>()

        for (song in songs) {
            searchResults.add(
                SearchResult(
                    title = song.title,
                    artist = song.artists,
                    album = song.album,
                    artUrl = song.artUrl!!,
                    url = song.sourceUrl,
                    id = song.id
                )
            )
        }

        return searchResults
    }
}