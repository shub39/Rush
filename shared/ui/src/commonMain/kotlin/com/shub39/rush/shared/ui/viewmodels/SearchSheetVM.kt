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
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.SongMeta
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.SearchState
import com.shub39.rush.shared.ui.lyrics.toSongUi
import com.shub39.rush.shared.ui.searchsheet.SearchSheetAction
import com.shub39.rush.shared.ui.searchsheet.SearchSheetState
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SearchSheetVM(
    private val stateLayer: SharedStates,
    @Provided private val repo: SongRepository,
) : ViewModel() {
    private var lyricsSearchStateJob: Job? = null
    private var searchJob: Job? = null
    private var fetchJob: Job? = null
    private var observeSongInfoJob: Job? = null

    private val _state = stateLayer.searchSheetState
    private val _lastSearched = MutableStateFlow("")

    val state =
        _state
            .asStateFlow()
            .onStart {
                observeSearchSheet()
                observeAutoChange()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchSheetState())

    private fun observeAutoChange() {
        stateLayer.lyricsState
            .map { it.autoChange }
            .distinctUntilChanged()
            .onEach { state -> if (state) observeSongInfo() else observeSongInfoJob?.cancel() }
            .launchIn(viewModelScope)
    }

    private fun observeSongInfo() {
        observeSongInfoJob?.cancel()
        observeSongInfoJob =
            viewModelScope.launch {
                MediaListener.songInfoFlow
                    .distinctUntilChanged()
                    .onEach { songInfo ->
                        stateLayer.lyricsState.update { it.copy(playingSong = songInfo) }

                        if (stateLayer.lyricsState.value.autoChange) {
                            searchSong("${songInfo.title} ${songInfo.artist}".trim())
                        }
                    }
                    .launchIn(this)
            }
    }

    fun onAction(action: SearchSheetAction) {
        when (action) {
            is SearchSheetAction.OnCardClicked ->
                viewModelScope.launch {
                    _state.update { it.copy(visible = !it.visible) }

                    fetchLyrics(action.id)

                    _state.update { it.copy(searchQuery = "", error = null) }
                }

            is SearchSheetAction.OnQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }

            SearchSheetAction.OnToggleSearchSheet -> {
                _state.update { it.copy(visible = !it.visible, searchQuery = "", error = null) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchSheet() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500.milliseconds)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update { it.copy(error = null) }
                    }

                    query.length >= 3 -> {
                        _state.update { it.copy(localSearchResults = localSearch(query)) }

                        searchSong(query, false)
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun searchSong(
        query: String,
        fetch: Boolean = stateLayer.lyricsState.value.autoChange,
    ) {
        searchJob?.cancel()

        searchJob =
            viewModelScope.launch {
                if (query.isEmpty() || query == _lastSearched.value) return@launch

                _state.update { it.copy(isSearching = true, error = null) }

                stateLayer.lyricsState.update {
                    it.copy(searchState = SearchState.Searching(query))
                }

                try {
                    when (val result = repo.searchGenius(query)) {
                        is Result.Error -> {
                            _state.update { it.copy(error = errorStringRes(result.error)) }
                        }

                        is Result.Success -> {
                            _state.update { it.copy(searchResults = result.data, error = null) }
                            _lastSearched.update { query }
                        }
                    }
                } finally {
                    _state.update { it.copy(isSearching = false) }
                    stateLayer.lyricsState.update { it.copy(searchState = SearchState.Idle) }
                }

                if (
                    fetch &&
                        stateLayer.lyricsState.value.playingSong != null &&
                        _state.value.searchResults.isNotEmpty()
                ) {
                    val resultScores =
                        _state.value.searchResults.associateWith {
                            getResultScore(
                                songMeta = stateLayer.lyricsState.value.playingSong!!,
                                searchResult = it,
                            )
                        }

                    if (resultScores.isNotEmpty()) {
                        if (resultScores.maxBy { it.value }.value > 0.0) {
                            fetchLyrics(resultScores.maxBy { it.value }.key.id)
                        } else searchFailedPrompt()
                    } else searchFailedPrompt()
                } else searchFailedPrompt()
            }
    }

    private fun searchFailedPrompt() {
        stateLayer.lyricsState.update {
            it.copy(searchState = SearchState.UserPrompt, sync = false)
        }

        lyricsSearchStateJob?.cancel()
        lyricsSearchStateJob =
            viewModelScope.launch {
                delay(5000.milliseconds)

                stateLayer.lyricsState.update {
                    if (it.searchState == SearchState.UserPrompt)
                        it.copy(searchState = SearchState.Idle)
                    else it
                }
            }
    }

    private fun fetchLyrics(songId: Long) {
        fetchJob?.cancel()

        fetchJob =
            viewModelScope.launch {
                val song =
                    _state.value.searchResults.find { it.id == songId }
                        ?: _state.value.localSearchResults.find { it.id == songId }
                        ?: return@launch

                stateLayer.lyricsState.update {
                    it.copy(
                        lyricsState = LyricsState.Fetching("${song.title} - ${song.artist}"),
                        extractedColors = ExtractedColors(),
                        searchState = SearchState.Idle,
                        sync = false,
                    )
                }

                if (songId in stateLayer.savedPageState.value.songsAsc.map { it.id }) {
                    val result = repo.getSong(songId).toSongUi()

                    stateLayer.lyricsState.update {
                        it.copy(
                            lyricsState = LyricsState.Loaded(song = result),
                            source =
                                if (result.lyrics.isNotEmpty()) Sources.LRCLIB else Sources.GENIUS,
                            syncedAvailable =
                                result.syncedLyrics != null || result.ttmlLyrics != null,
                            sync = (result.syncedLyrics != null || result.ttmlLyrics != null),
                            selectedLines = emptyMap(),
                        )
                    }

                    stateLayer.savedPageState.update { it.copy(currentSong = result) }
                } else {
                    when (val result = repo.fetchSong(song)) {
                        is Result.Error -> {
                            stateLayer.lyricsState.update {
                                it.copy(
                                    lyricsState =
                                        LyricsState.LyricsError(
                                            errorCode = errorStringRes(result.error),
                                            debugMessage = result.message,
                                        )
                                )
                            }
                        }

                        is Result.Success -> {
                            val retrievedSong = result.data.toSongUi()

                            stateLayer.lyricsState.update {
                                it.copy(
                                    lyricsState = LyricsState.Loaded(song = retrievedSong),
                                    source =
                                        if (retrievedSong.lyrics.isNotEmpty()) Sources.LRCLIB
                                        else Sources.GENIUS,
                                    syncedAvailable =
                                        retrievedSong.syncedLyrics != null ||
                                            retrievedSong.ttmlLyrics != null,
                                    sync =
                                        (retrievedSong.syncedLyrics != null ||
                                            retrievedSong.ttmlLyrics != null),
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
                    id = song.id,
                )
            )
        }

        return searchResults
    }

    private fun getResultScore(songMeta: SongMeta, searchResult: SearchResult): Double {
        var score = 0.0

        if (songMeta.title == searchResult.title) {
            score += 0.5
        } else if (songMeta.title.startsWith(searchResult.title)) {
            score += 0.4
        } else if (searchResult.title.startsWith(songMeta.title)) {
            score += 0.3
        }

        if (songMeta.artist == null) {
            return score
        } else if (songMeta.artist == searchResult.artist) {
            score += 0.5
        } else if (songMeta.artist!!.startsWith(searchResult.artist)) {
            score += 0.4
        } else if (searchResult.artist.startsWith(songMeta.artist!!)) {
            score += 0.3
        }

        if (songMeta.album == searchResult.album) score += 0.5

        return score
    }
}
