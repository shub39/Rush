package com.shub39.rush.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.ui.page.setting.component.AudioFile
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.SongDatabase
import com.shub39.rush.error.Result
import com.shub39.rush.listener.MediaListener
import com.shub39.rush.logic.UILogic.sortMapByKeys
import com.shub39.rush.logic.errorStringRes
import com.shub39.rush.network.SongProvider
import com.shub39.rush.ui.page.component.searchsheet.SearchSheetAction
import com.shub39.rush.ui.page.component.searchsheet.SearchSheetState
import com.shub39.rush.ui.page.lyrics.LrcCorrect
import com.shub39.rush.ui.page.lyrics.LyricsPageAction
import com.shub39.rush.ui.page.lyrics.LyricsPageState
import com.shub39.rush.ui.page.lyrics.toSongUi
import com.shub39.rush.ui.page.saved.SavedPageAction
import com.shub39.rush.ui.page.saved.SavedPageState
import com.shub39.rush.ui.page.setting.BatchDownload
import com.shub39.rush.ui.page.setting.SettingsPageAction
import com.shub39.rush.ui.page.setting.SettingsPageState
import com.shub39.rush.ui.page.share.SharePageState
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
    database: SongDatabase
) : ViewModel() {

    private val songDao = database.songDao()

    private val _lastSearched = MutableStateFlow("")

    private val _lyricsPageState = MutableStateFlow(LyricsPageState())
    private val _savedPageState = MutableStateFlow(SavedPageState())
    private val _sharePageState = MutableStateFlow(SharePageState())
    private val _settingsPageState = MutableStateFlow(SettingsPageState())
    private val _searchSheetState = MutableStateFlow(SearchSheetState())

    val lyricsPageState = _lyricsPageState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LyricsPageState()
        )
    val savedPageState = _savedPageState.asStateFlow()
        .onStart { updateSavedState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SavedPageState()
        )
    val sharePageState = _sharePageState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SharePageState()
        )
    val settingsPageState = _settingsPageState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SettingsPageState()
        )
    val searchSheetState = _searchSheetState.asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SearchSheetState()
        )

    init {
        viewModelScope.launch {
            MediaListener.songInfoFlow.collect { songInfo ->
                _lyricsPageState.update {
                    it.copy(
                        playingSong = it.playingSong.copy(
                            title = songInfo.first,
                            artist = songInfo.second
                        )
                    )
                }

                Log.d("Rush", "Song Info: $songInfo")

                if (_lyricsPageState.value.autoChange) {
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
                    _lyricsPageState.update {
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
                    _lyricsPageState.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = true)) }

                    val result = withContext(Dispatchers.IO) {
                        SongProvider.lrcLibSearch(action.track, action.artist)
                    }

                    when (result) {
                        is Result.Error -> {
                            _lyricsPageState.update {
                                it.copy(
                                    lrcCorrect = LrcCorrect(
                                        error = errorStringRes(result.error)
                                    )
                                )
                            }
                        }

                        is Result.Success -> {
                            _lyricsPageState.update {
                                it.copy(
                                    lrcCorrect = LrcCorrect(
                                        searchResults = result.data,
                                    )
                                )
                            }
                        }
                    }

                }

                is LyricsPageAction.OnToggleAutoChange -> {
                    toggleAutoChange()
                }

                is LyricsPageAction.OnToggleSearchSheet -> {
                    toggleSearchSheet()
                }

                is LyricsPageAction.OnUpdateShareLines -> {
                    _sharePageState.update {
                        it.copy(
                            songDetails = action.songDetails,
                            selectedLines = sortMapByKeys(action.shareLines)
                        )
                    }
                }

                is LyricsPageAction.OnUpdateSongLyrics -> {
                    songDao.updateLrcLyricsById(action.id, action.plainLyrics, action.syncedLyrics)
                    _lyricsPageState.update {
                        it.copy(
                            song = songDao.getSongById(action.id).toSongUi()
                        )
                    }
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
                    songDao.deleteSong(action.song)
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
                    _settingsPageState.update {
                        it.copy(
                            batchDownload = BatchDownload(
                                indexes = emptyMap()
                            )
                        )
                    }
                }

                SettingsPageAction.OnDeleteSongs -> {
                    songDao.deleteAllSongs()
                    updateSavedState()
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

    private fun searchSong(
        query: String,
        fetch: Boolean = _lyricsPageState.value.autoChange,
    ) {
        if (query.isEmpty() || query == _lastSearched.value) return

        viewModelScope.launch {
            _lyricsPageState.update {
                it.copy(
                    searching = Pair(true, query)
                )
            }

            _searchSheetState.update {
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
                        _lyricsPageState.update {
                            it.copy(
                                error = errorStringRes(result.error)
                            )
                        }
                    }

                    is Result.Success -> {
                        _searchSheetState.update {
                            it.copy(
                                searchResults = result.data,
                            )
                        }

                        if (fetch) _lastSearched.value = query

                        _lyricsPageState.update {
                            it.copy(
                                error = null
                            )
                        }
                    }
                }

            } finally {
                _lyricsPageState.update {
                    it.copy(
                        searching = Pair(false, "")
                    )
                }

                _searchSheetState.update {
                    it.copy(
                        isSearching = false
                    )
                }
            }

            if (fetch && _lyricsPageState.value.error == null && _searchSheetState.value.searchResults.isNotEmpty()) {

                fetchLyrics(_searchSheetState.value.searchResults.first().id)

            } else {

                toggleAutoChange(false)

            }
        }
    }

    private fun fetchLyrics(songId: Long) {
        viewModelScope.launch {
            val song = _searchSheetState.value.searchResults.find { it.id == songId }
            _lyricsPageState.update {
                it.copy(
                    fetching = Pair(true, "${song?.title} - ${song?.artist}")
                )
            }

            try {
                if (songId in _savedPageState.value.songsAsc.map { it.id }) {
                    val result = songDao.getSongById(songId)
                    _lyricsPageState.update {
                        it.copy(
                            song = result.toSongUi()
                        )
                    }
                } else {
                    val result = withContext(Dispatchers.IO) {
                        SongProvider.fetchLyrics(songId)
                    }

                    when (result) {
                        is Result.Error -> {
                            _lyricsPageState.update {
                                it.copy(
                                    error = errorStringRes(result.error)
                                )
                            }
                        }

                        is Result.Success -> {
                            songDao.insertSong(result.data)

                            _lyricsPageState.update {
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
                _lyricsPageState.update {
                    it.copy(
                        fetching = Pair(false, ""),
                        error = null
                    )
                }
            }
        }
    }

    private fun localSearch(query: String) {
        if (query.isEmpty()) {
            _searchSheetState.update {
                it.copy(
                    localSearchResults = emptyList()
                )
            }
            return
        }

        viewModelScope.launch {
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

            _searchSheetState.update {
                it.copy(
                    localSearchResults = searchResults,
                )
            }
        }
    }

    private fun batchDownload(
        list: List<AudioFile>,
    ) {
        viewModelScope.launch {
            _settingsPageState.update {
                it.copy(
                    batchDownload = it.batchDownload.copy(
                        isDownloading = true
                    )
                )
            }
            val savedSongs = _savedPageState.value.songsAsc.map { it.id }

            list.forEachIndexed { index, audioFile ->
                val result = withContext(Dispatchers.IO) {
                    SongProvider.geniusSearch("${audioFile.title} ${audioFile.artist}")
                }

                when (result) {
                    is Result.Error -> {
                        _settingsPageState.update {
                            it.copy(
                                batchDownload = it.batchDownload.copy(
                                    error = errorStringRes(result.error)
                                )
                            )
                        }
                    }

                    is Result.Success -> {
                        val id = result.data.first().id

                        if (id in savedSongs) {

                            _settingsPageState.update {
                                it.copy(
                                    batchDownload = it.batchDownload.copy(
                                        indexes = it.batchDownload.indexes + (index to true),
                                        error = null
                                    )
                                )
                            }

                        } else {
                            val song = withContext(Dispatchers.IO) {
                                SongProvider.fetchLyrics(id)
                            }

                            when (song) {
                                is Result.Error -> {
                                    _settingsPageState.update {
                                        it.copy(
                                            batchDownload = it.batchDownload.copy(
                                                indexes = it.batchDownload.indexes + (index to false)
                                            )
                                        )
                                    }
                                }

                                is Result.Success -> {
                                    songDao.insertSong(song.data)

                                    _settingsPageState.update {
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
            _settingsPageState.update {
                it.copy(
                    batchDownload = it.batchDownload.copy(
                        isDownloading = false,
                        error = null,
                    )
                )
            }
        }
    }

    private suspend fun updateSavedState() {
        val songs = songDao.getAllSongs()

        _savedPageState.update {
            it.copy(
                songsAsc = songs.sortedBy { it.title },
                songsDesc = songs.sortedByDescending { it.title },
                groupedArtist = songs.groupBy { it.artists }.entries.toList(),
                groupedAlbum = songs.groupBy { it.album ?: "???" }.entries.toList()
            )
        }
    }

    private fun toggleAutoChange() {
        _lyricsPageState.update { it.copy(autoChange = !it.autoChange) }
        _savedPageState.update { it.copy(autoChange = !it.autoChange) }

        if (_lyricsPageState.value.autoChange && _lyricsPageState.value.playingSong.title.isNotEmpty()) {
            val info =
                "${_lyricsPageState.value.playingSong.title} ${_lyricsPageState.value.playingSong.artist}"
            searchSong(info)
        }
    }

    private fun toggleAutoChange(boolean: Boolean) {
        _lyricsPageState.update { it.copy(autoChange = boolean) }
        _savedPageState.update { it.copy(autoChange = boolean) }
    }

    private fun toggleSearchSheet() {
        _searchSheetState.update {
            it.copy(
                visible = !it.visible
            )
        }
    }

}