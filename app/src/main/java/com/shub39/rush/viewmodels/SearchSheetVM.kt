package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.data.listener.MediaListenerImpl
import com.shub39.rush.domain.Result
import com.shub39.rush.domain.dataclasses.ExtractedColors
import com.shub39.rush.domain.dataclasses.SearchResult
import com.shub39.rush.domain.enums.Sources
import com.shub39.rush.domain.interfaces.SongRepository
import com.shub39.rush.presentation.errorStringRes
import com.shub39.rush.presentation.getMainTitle
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.SearchState
import com.shub39.rush.presentation.lyrics.toSongUi
import com.shub39.rush.presentation.searchsheet.SearchSheetAction
import com.shub39.rush.presentation.searchsheet.SearchSheetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchSheetVM(
    private val stateLayer: StateLayer,
    private val repo: SongRepository
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
            MediaListenerImpl.songInfoFlow.collect { songInfo ->
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
        when (action) {
            is SearchSheetAction.OnCardClicked -> viewModelScope.launch {
                _state.update {
                    it.copy(visible = !it.visible)
                }

                fetchLyrics(action.id)

                _state.update {
                    it.copy(searchQuery = "", error = null)
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
                        visible = !it.visible,
                        searchQuery = "",
                        error = null
                    )
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
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private suspend fun searchSong(
        query: String,
        fetch: Boolean = stateLayer.lyricsState.value.autoChange,
    ) {
        if (query.isEmpty() || query == _lastSearched.value || _state.value.isSearching) return

        _state.update {
            it.copy(
                isSearching = true,
                error = null
            )
        }

        stateLayer.lyricsState.update {
            it.copy(
                searchState = SearchState.Searching(query)
            )
        }

        try {
            when (val result = repo.searchGenius(query)) {
                is Result.Error -> {
                    _state.update {
                        it.copy(error = errorStringRes(result.error))
                    }
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(
                            searchResults = result.data,
                            error = null
                        )
                    }
                }
            }
        } finally {
            _state.update { it.copy(isSearching = false) }
            stateLayer.lyricsState.update { it.copy(searchState = SearchState.Idle) }
            _lastSearched.update { query }
        }

        if (
            fetch &&
            _state.value.searchResults.isNotEmpty() &&
            query.contains(_state.value.searchResults.first().title.trim(), ignoreCase = true)
        ) {
            fetchLyrics(_state.value.searchResults.first().id)
        } else {
            stateLayer.lyricsState.update {
                it.copy(
                    searchState = SearchState.UserPrompt,
                    sync = false
                )
            }
        }
    }

    private suspend fun fetchLyrics(songId: Long) {
        if (stateLayer.lyricsState.value.lyricsState is LyricsState.Fetching) return

        val song = _state.value.searchResults.find { it.id == songId } ?: return

        stateLayer.lyricsState.update {
            it.copy(
                lyricsState = LyricsState.Fetching("${song.title} - ${song.artist}"),
                extractedColors = ExtractedColors(),
                searchState = SearchState.Idle,
                sync = false
            )
        }

        if (songId in stateLayer.savedPageState.value.songsAsc.map { it.id }) {
            val result = repo.getSong(songId).toSongUi()

            stateLayer.lyricsState.update {
                it.copy(
                    lyricsState = LyricsState.Loaded(song = result),
                    source = if (result.lyrics.isNotEmpty()) Sources.LRCLIB else Sources.GENIUS,
                    syncedAvailable = result.syncedLyrics != null,
                    sync = result.syncedLyrics != null && (getMainTitle(it.playingSong.title).trim()
                        .equals(getMainTitle(result.title).trim(), ignoreCase = true)),
                    selectedLines = emptyMap(),
                )
            }

            stateLayer.savedPageState.update {
                it.copy(currentSong = result)
            }
        } else {
            when (val result = repo.fetchSong(song)) {
                is Result.Error -> {
                    stateLayer.lyricsState.update {
                        it.copy(
                            lyricsState = LyricsState.LyricsError(
                                errorCode = errorStringRes(result.error),
                                debugMessage = result.message
                            )
                        )
                    }
                }

                is Result.Success -> {
                    val retrievedSong = result.data.toSongUi()

                    stateLayer.lyricsState.update {
                        it.copy(
                            lyricsState = LyricsState.Loaded(song = retrievedSong),
                            source = if (retrievedSong.lyrics.isNotEmpty()) Sources.LRCLIB else Sources.GENIUS,
                            syncedAvailable = retrievedSong.syncedLyrics != null,
                            sync = retrievedSong.syncedLyrics != null && (getMainTitle(it.playingSong.title).trim()
                                .equals(
                                    getMainTitle(retrievedSong.title).trim(),
                                    ignoreCase = true
                                )),
                            selectedLines = emptyMap(),
                        )
                    }

                    stateLayer.savedPageState.update {
                        it.copy(currentSong = retrievedSong)
                    }
                }
            }
        }
    }

    private suspend fun localSearch(query: String): List<SearchResult> {
        if (query.isEmpty()) return emptyList()

        val songs = repo.getSong(query)
        val searchResults = mutableListOf<SearchResult>()

        for (song in songs) {
            searchResults.add(
                SearchResult(
                    title = song.title,
                    artist = song.artists,
                    album = song.album,
                    artUrl = song.artUrl ?: "",
                    url = song.sourceUrl,
                    id = song.id
                )
            )
        }

        return searchResults
    }
}