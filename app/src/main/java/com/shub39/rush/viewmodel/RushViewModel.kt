package com.shub39.rush.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.ui.page.setting.component.AudioFile
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.Song
import com.shub39.rush.database.SongDatabase
import com.shub39.rush.error.Result
import com.shub39.rush.listener.MediaListener
import com.shub39.rush.logic.UILogic.sortMapByKeys
import com.shub39.rush.logic.errorStringRes
import com.shub39.rush.network.SongProvider
import com.shub39.rush.ui.page.component.searchsheet.SearchSheetAction
import com.shub39.rush.ui.page.component.searchsheet.SearchSheetState
import com.shub39.rush.ui.page.lyrics.LyricsPageAction
import com.shub39.rush.ui.page.lyrics.LyricsPageState
import com.shub39.rush.ui.page.lyrics.toSongUi
import com.shub39.rush.ui.page.saved.SavedPageAction
import com.shub39.rush.ui.page.saved.SavedPageState
import com.shub39.rush.ui.page.setting.BatchDownload
import com.shub39.rush.ui.page.setting.SettingsPageAction
import com.shub39.rush.ui.page.setting.SettingsPageState
import com.shub39.rush.ui.page.share.ExtractedColors
import com.shub39.rush.ui.page.share.SharePageAction
import com.shub39.rush.ui.page.share.SharePageState
import com.shub39.rush.ui.page.share.SongDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RushViewModel(
    database: SongDatabase,
) : ViewModel() {

    private val songDao = database.songDao()

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
        .onStart { updateSavedState() }
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
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SearchSheetState()
        )

    init {
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
                    updateSavedState()
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
                    deleteSong(action.song)
                    updateSavedState()
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
                    updateSavedState()
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

                is SearchSheetAction.OnSearch -> {
                    localSearch(action.query)
                    searchSong(action.query, false)
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

    private suspend fun deleteSong(song: Song) {
        songDao.deleteSong(song)
    }

    private suspend fun updateLrcLyrics(
        id: Long,
        plainLyrics: String,
        syncedLyrics: String?
    ) {
        songDao.updateLrcLyricsById(id, plainLyrics, syncedLyrics)

        _lyricsState.update {
            it.copy(
                song = songDao.getSongById(id).toSongUi()
            )
        }

    }

    private suspend fun lrcSearch(
        track: String,
        artist: String = ""
    ) {
        _lyricsState.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = true)) }

        val result = withContext(Dispatchers.IO) {
            SongProvider.lrcLibSearch(track, artist)
        }

        when (result) {
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
                val result = withContext(Dispatchers.IO) {
                    SongProvider.geniusSearch(query)
                }

                when (result) {
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
                val result = songDao.getSongById(songId)

                _lyricsState.update {
                    it.copy(
                        song = result.toSongUi(),
                        error = null
                    )
                }
            } else {
                val result = withContext(Dispatchers.IO) {
                    SongProvider.fetchLyrics(songId)
                }

                when (result) {
                    is Result.Error -> {
                        _lyricsState.update {
                            it.copy(
                                error = errorStringRes(result.error)
                            )
                        }
                    }

                    is Result.Success -> {
                        songDao.insertSong(result.data)

                        _lyricsState.update {
                            it.copy(
                                song = result.data.toSongUi(),
                                error = null
                            )
                        }

                        updateSavedState()
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

        val songs = songDao.searchSong(query)
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
            val result = withContext(Dispatchers.IO) {
                SongProvider.geniusSearch("${audioFile.title} ${audioFile.artist}")
            }

            when (result) {
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
                        val song = withContext(Dispatchers.IO) {
                            SongProvider.fetchLyrics(id)
                        }

                        when (song) {
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
                                songDao.insertSong(song.data)

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

        updateSavedState()

        _settingsState.update {
            it.copy(
                batchDownload = it.batchDownload.copy(
                    isDownloading = false
                )
            )
        }
    }

    private suspend fun updateSavedState() {
        val songs = songDao.getAllSongs()

        _savedState.update {
            it.copy(
                songsAsc = songs.sortedBy { it.title },
                songsDesc = songs.sortedByDescending { it.title },
                groupedArtist = songs.groupBy { it.artists }.entries.toList(),
                groupedAlbum = songs.groupBy { it.album ?: "???" }.entries.toList()
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
        songDao.deleteAllSongs()
    }

}