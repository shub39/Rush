package com.shub39.rush.lyrics.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.lyrics.presentation.setting.component.AudioFile
import com.shub39.rush.lyrics.domain.SearchResult
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.sortMapByKeys
import com.shub39.rush.lyrics.data.listener.MediaListener
import com.shub39.rush.core.presentation.errorStringRes
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheetAction
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheetState
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.lyrics.toSongUi
import com.shub39.rush.lyrics.presentation.saved.SavedPageAction
import com.shub39.rush.lyrics.presentation.saved.SavedPageState
import com.shub39.rush.lyrics.presentation.setting.BatchDownload
import com.shub39.rush.lyrics.presentation.setting.SettingsPageAction
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState
import com.shub39.rush.share.ExtractedColors
import com.shub39.rush.share.SharePageAction
import com.shub39.rush.share.SharePageState
import com.shub39.rush.share.SongDetails
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RushViewModel(
    private val repo: SongRepo,
) : ViewModel() {

    private var savedJob: Job? = null

    private val _lastSearched = MutableStateFlow("")

    private val _lyricsState = MutableStateFlow(LyricsPageState())
    private val _savedState = MutableStateFlow(SavedPageState())
    private val _shareState = MutableStateFlow(SharePageState())
    private val _settingsState = MutableStateFlow(SettingsPageState())
    private val _searchState = MutableStateFlow(SearchSheetState())

    val lyricsState = _lyricsState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LyricsPageState()
        )
    val savedState = _savedState.asStateFlow()
        .onStart {
            observeSongs()
            observePlayingMedia()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SavedPageState()
        )
    val shareState = _shareState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SharePageState()
        )
    val settingsState = _settingsState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SettingsPageState()
        )
    val searchState = _searchState.asStateFlow()
        .onStart {
            observeSearchSheet()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SearchSheetState()
        )

    private fun observePlayingMedia() {
        viewModelScope.launch {
            MediaListener.songInfoFlow.collect { songInfo ->
                _lyricsState.update {
                    it.copy(
                        playingSong = it.playingSong.copy(
                            title = songInfo.first,
                            artist = songInfo.second
                        )
                    )
                }

                Log.d("Rush", "Song Info: $songInfo")

                if (_lyricsState.value.autoChange) {
                    searchSong("${songInfo.first} ${songInfo.second}".trim())
                }
            }
        }

        viewModelScope.launch {
            combine(
                MediaListener.songPositionFlow,
                MediaListener.playbackSpeedFlow,
                ::Pair
            ).collectLatest { (position, speed) ->
                val start = System.currentTimeMillis()

                while (isActive) {
                    val elapsed = (speed * (System.currentTimeMillis() - start)).toLong()
                    _lyricsState.update {
                        it.copy(
                            playingSong = it.playingSong.copy(
                                position = position + elapsed
                            )
                        )
                    }

                    Log.d("Rush", "Song Position: $position")
                    delay(500)
                }

            }
        }
    }

    private fun observeSongs() {
        savedJob?.cancel()
        savedJob = repo
            .getSongs()
            .onEach { songs ->
                _savedState.update { state ->
                    state.copy(
                        songsByTime = songs.sortedByDescending { it.dateAdded },
                        songsAsc = songs.sortedBy { it.title },
                        songsDesc = songs.sortedByDescending { it.title },
                        groupedAlbum = songs.groupBy { it.album ?: "???" }.entries.toList(),
                        groupedArtist = songs.groupBy { it.artists }.entries.toList()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchSheet() {
        searchState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _searchState.update {
                            it.copy(
                                error = null
                            )
                        }
                    }

                    query.length >= 3 -> {
                        localSearch(query)
                        searchSong(query, false)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onLyricsPageAction(action: LyricsPageAction) {
        viewModelScope.launch {
            when (action) {
                is LyricsPageAction.OnLrcSearch -> {
                    lrcSearch(action.track, action.artist)
                }

                is LyricsPageAction.OnToggleAutoChange -> {
                    toggleAutoChange()
                }

                is LyricsPageAction.OnToggleSearchSheet -> {
                    toggleSearchSheet()
                }

                is LyricsPageAction.OnUpdateShareLines -> {
                    updateShareLines(action.songDetails, action.shareLines)
                }

                is LyricsPageAction.OnUpdateSongLyrics -> {
                    updateLrcLyrics(action.id, action.plainLyrics, action.syncedLyrics)
                }
            }
        }
    }

    fun onSavedPageAction(action: SavedPageAction) {
        viewModelScope.launch {
            when (action) {
                is SavedPageAction.ChangeCurrentSong -> {
                    fetchLyrics(action.id)
                }

                is SavedPageAction.OnDeleteSong -> {
                    deleteSong(action.song.id)
                }

                SavedPageAction.OnToggleAutoChange -> {
                    toggleAutoChange()
                }

                SavedPageAction.OnToggleSearchSheet -> {
                    toggleSearchSheet()
                }
            }
        }
    }

    fun onSettingsPageAction(action: SettingsPageAction) {
        viewModelScope.launch {
            when (action) {
                is SettingsPageAction.OnBatchDownload -> {
                    batchDownload(action.files)
                }

                SettingsPageAction.OnClearIndexes -> {
                    onClearIndexes()
                }

                SettingsPageAction.OnDeleteSongs -> {
                    deleteSongs()
                }
            }
        }
    }

    fun onSharePageAction(action: SharePageAction) {
        viewModelScope.launch {
            when (action) {
                is SharePageAction.UpdateExtractedColors -> {
                    updateExtractedColors(action.colors)
                }
            }
        }
    }

    fun onSearchSheetAction(action: SearchSheetAction) {
        viewModelScope.launch {
            when (action) {
                is SearchSheetAction.OnCardClicked -> {
                    toggleSearchSheet()
                    fetchLyrics(action.id)
                }

                is SearchSheetAction.OnQueryChange -> {
                    _searchState.update {
                        it.copy(
                            searchQuery = action.query
                        )
                    }
                }

                SearchSheetAction.OnToggleSearchSheet -> {
                    toggleSearchSheet()
                }
            }
        }
    }

    private fun updateShareLines(
        songDetails: SongDetails,
        shareLines: Map<Int, String>
    ) {
        _shareState.update {
            it.copy(
                songDetails = songDetails,
                selectedLines = sortMapByKeys(shareLines)
            )
        }
    }

    private fun updateExtractedColors(
        colors: ExtractedColors
    ) {
        _shareState.update {
            it.copy(
                extractedColors = it.extractedColors.copy(
                    cardContentMuted = colors.cardContentMuted,
                    cardContentDominant = colors.cardContentDominant,
                    cardBackgroundMuted = colors.cardBackgroundMuted,
                    cardBackgroundDominant = colors.cardBackgroundDominant
                )
            )
        }
    }

    private suspend fun deleteSong(songId: Long) {
        repo.deleteSong(songId)
    }

    private suspend fun updateLrcLyrics(
        id: Long,
        plainLyrics: String,
        syncedLyrics: String?
    ) {
        repo.updateLrcLyrics(id, plainLyrics, syncedLyrics)

        _lyricsState.update {
            it.copy(
                song = repo.getSong(id).toSongUi()
            )
        }

    }

    private suspend fun lrcSearch(
        track: String,
        artist: String = ""
    ) {
        _lyricsState.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = true)) }

        when (val result = repo.searchLrcLib(track, artist)) {
            is Result.Error -> {
                _lyricsState.update {
                    it.copy(
                        lrcCorrect = it.lrcCorrect.copy(
                            error = errorStringRes(result.error)
                        )
                    )
                }
            }

            is Result.Success -> {
                _lyricsState.update {
                    it.copy(
                        lrcCorrect = it.lrcCorrect.copy(
                            searchResults = result.data
                        )
                    )
                }
            }
        }

        _lyricsState.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = false)) }
    }

    private fun searchSong(
        query: String,
        fetch: Boolean = _lyricsState.value.autoChange,
    ) {
        if (query.isEmpty() || _lyricsState.value.searching.first || query == _lastSearched.value) return

        viewModelScope.launch {
            _lyricsState.update {
                it.copy(
                    searching = Pair(true, query)
                )
            }

            _searchState.update {
                it.copy(
                    isSearching = true
                )
            }

            try {
                when (val result = repo.searchGenius(query)) {
                    is Result.Error -> {
                        _lyricsState.update {
                            it.copy(
                                error = errorStringRes(result.error)
                            )
                        }
                    }

                    is Result.Success -> {
                        _searchState.update {
                            it.copy(
                                searchResults = result.data,
                            )
                        }

                        if (fetch) _lastSearched.value = query

                        _lyricsState.update {
                            it.copy(
                                error = null
                            )
                        }
                    }
                }

            } finally {
                _lyricsState.update {
                    it.copy(
                        searching = Pair(false, "")
                    )
                }

                _searchState.update {
                    it.copy(
                        isSearching = false
                    )
                }
            }

            if (fetch && _lyricsState.value.error == null && _searchState.value.searchResults.isNotEmpty()) {

                fetchLyrics(_searchState.value.searchResults.first().id)

            } else {

                toggleAutoChange(false)

            }
        }
    }

    private suspend fun fetchLyrics(songId: Long) {
        if (_lyricsState.value.searching.first) return

        val song = _searchState.value.searchResults.find { it.id == songId }

        _lyricsState.update {
            it.copy(
                fetching = Pair(true, "${song?.title} - ${song?.artist}")
            )
        }

        try {
            if (songId in _savedState.value.songsAsc.map { it.id }) {
                val result = repo.getSong(songId)

                _lyricsState.update {
                    it.copy(
                        song = result.toSongUi(),
                        error = null
                    )
                }
            } else {
                when (val result = repo.fetchSong(songId)) {
                    is Result.Error -> {
                        _lyricsState.update {
                            it.copy(
                                error = errorStringRes(result.error)
                            )
                        }
                    }

                    is Result.Success -> {
                        _lyricsState.update {
                            it.copy(
                                song = result.data.toSongUi(),
                                error = null
                            )
                        }
                    }
                }

            }
        } finally {
            _lyricsState.update {
                it.copy(
                    fetching = Pair(false, ""),
                )
            }
        }
    }

    private suspend fun localSearch(query: String) {
        if (query.isEmpty()) {
            _searchState.update {
                it.copy(
                    localSearchResults = emptyList()
                )
            }
            return
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

        _searchState.update {
            it.copy(
                localSearchResults = searchResults,
            )
        }
    }

    private suspend fun batchDownload(
        list: List<AudioFile>,
    ) {
        _settingsState.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = true
                )
            )
        }

        val savedSongs = _savedState.value.songsAsc.map { it.id }

        list.forEachIndexed { index, audioFile ->
            when (val result = repo.searchGenius(audioFile.title)) {
                is Result.Error -> {
                    _settingsState.update {
                        it.copy(
                            batchDownload = it.batchDownload.copy(
                                indexes = it.batchDownload.indexes.plus(index to false)
                            )
                        )
                    }
                }

                is Result.Success -> {
                    val id = result.data.first().id

                    if (id in savedSongs) {

                        _settingsState.update {
                            it.copy(
                                batchDownload = it.batchDownload.copy(
                                    indexes = it.batchDownload.indexes.plus(index to true),
                                )
                            )
                        }

                    } else {
                        when (repo.fetchSong(id)) {
                            is Result.Error -> {
                                _settingsState.update {
                                    it.copy(
                                        batchDownload = it.batchDownload.copy(
                                            indexes = it.batchDownload.indexes + (index to false)
                                        )
                                    )
                                }
                            }

                            is Result.Success -> {
                                _settingsState.update {
                                    it.copy(
                                        batchDownload = it.batchDownload.copy(
                                            indexes = it.batchDownload.indexes + (index to true),
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        _settingsState.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = false
                )
            )
        }
    }



    private fun toggleAutoChange() {
        _lyricsState.update { it.copy(autoChange = !it.autoChange) }
        _savedState.update { it.copy(autoChange = !it.autoChange) }

        if (_lyricsState.value.autoChange && _lyricsState.value.playingSong.title.isNotEmpty()) {
            searchSong("${_lyricsState.value.playingSong.title} ${_lyricsState.value.playingSong.artist}")
        }
    }

    private fun toggleAutoChange(boolean: Boolean) {
        _lyricsState.update { it.copy(autoChange = boolean) }
        _savedState.update { it.copy(autoChange = boolean) }
    }

    private fun toggleSearchSheet() {
        _searchState.update {
            it.copy(
                visible = !it.visible
            )
        }
    }

    private fun onClearIndexes() {
        _settingsState.update {
            it.copy(
                batchDownload = BatchDownload(
                    indexes = emptyMap()
                )
            )
        }
    }

    private suspend fun deleteSongs() {
        repo.deleteAllSongs()
    }

}