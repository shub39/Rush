package com.shub39.rush.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.database.AudioFile
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.Song
import com.shub39.rush.database.SongDatabase
import com.shub39.rush.error.Result
import com.shub39.rush.listener.MediaListener
import com.shub39.rush.logic.errorStringRes
import com.shub39.rush.network.SongProvider
import com.shub39.rush.ui.page.lyrics.LrcCorrect
import com.shub39.rush.ui.page.lyrics.LyricsPageAction
import com.shub39.rush.ui.page.lyrics.LyricsPageState
import com.shub39.rush.ui.page.lyrics.toSongUi
import com.shub39.rush.ui.page.saved.SavedPageAction
import com.shub39.rush.ui.page.saved.SavedPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    private val _songs = MutableStateFlow(listOf<Song>())
    private val _searchResults = MutableStateFlow(listOf<SearchResult>())
    private val _localSearchResults = MutableStateFlow(listOf<SearchResult>())
    private val _currentSongId = MutableStateFlow<Long?>(null)
    private val _currentSong = MutableStateFlow<Song?>(null)
    private val _isSearchingLyrics = MutableStateFlow(false)
    private val _shareLines = MutableStateFlow(mapOf<Int, String>())
    private val _lastSearched = MutableStateFlow("")
    private val _error = MutableStateFlow(false)
    private val _batchDownloading = MutableStateFlow(false)
    private val _downloadIndexes = MutableStateFlow(mapOf<Int, Boolean>())

    private val _lyricsPageState = MutableStateFlow(LyricsPageState())
    private val _savedPageState = MutableStateFlow(SavedPageState())

    private val _searchSheet = MutableStateFlow(false)

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

    val songs: StateFlow<List<Song>> get() = _songs
    val searchResults: StateFlow<List<SearchResult>> get() = _searchResults
    val localSearchResults: StateFlow<List<SearchResult>> get() = _localSearchResults
    val currentSong: MutableStateFlow<Song?> get() = _currentSong
    val isSearchingLyrics: StateFlow<Boolean> get() = _isSearchingLyrics
    val error: StateFlow<Boolean> get() = _error
    val shareLines: StateFlow<Map<Int, String>> get() = _shareLines
    val batchDownloading: StateFlow<Boolean> get() = _batchDownloading
    val downloadIndexes: StateFlow<Map<Int, Boolean>> get() = _downloadIndexes

    val searchSheet: StateFlow<Boolean> get() = _searchSheet

    init {
        viewModelScope.launch {
            _songs.value = songDao.getAllSongs()
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
                    TODO()
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

    fun searchSong(
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
                        _searchResults.value = result.data
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
            }

            if (fetch && _lyricsPageState.value.error == null && _searchResults.value.isNotEmpty()) {

                fetchLyrics(_searchResults.value.first().id)

            } else {
                _lyricsPageState.update {
                    it.copy(
                        autoChange = false
                    )
                }
            }
        }
    }

    private fun fetchLyrics(songId: Long = _currentSongId.value!!) {
        viewModelScope.launch {
            val song = _searchResults.value.find { it.id == songId }
            _lyricsPageState.update {
                it.copy(
                    fetching = Pair(true, "${song?.title} - ${song?.artist}")
                )
            }

            try {
                if (songId in songs.value.map { it.id }) {
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
                            _songs.value = songDao.getAllSongs()
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

    fun localSearch(query: String) {
        if (query.isEmpty()) {
            _localSearchResults.value = emptyList()
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

            _localSearchResults.value = searchResults
        }
    }

    fun batchDownload(
        list: List<AudioFile>,
    ) {
        viewModelScope.launch {
            _batchDownloading.value = true
            val savedSongs = songs.value.map { it.id }

            list.forEachIndexed { index, audioFile ->
                val result = withContext(Dispatchers.IO) {
                    SongProvider.geniusSearch("${audioFile.title} ${audioFile.artist}")
                }

                when (result) {
                    is Result.Error -> TODO()
                    is Result.Success -> {
                        val id = result.data.first().id

                        if (id in savedSongs) {
                            _downloadIndexes.value += index to true
                        } else {
                            val song = withContext(Dispatchers.IO) {
                                SongProvider.fetchLyrics(id)
                            }

                            when (song) {
                                is Result.Error -> {
                                    _downloadIndexes.value += index to false
                                }

                                is Result.Success -> {
                                    songDao.insertSong(song.data)
                                    _downloadIndexes.value += index to true
                                }
                            }
                        }
                    }
                }

            }

            _songs.value = songDao.getAllSongs()
            _batchDownloading.value = false
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            songDao.deleteSong(song)
            _songs.value = songDao.getAllSongs()
        }
    }

    fun clearIndexes() {
        _downloadIndexes.value = emptyMap()
    }

    fun changeCurrentSong(songId: Long) {
        _currentSongId.value = songId
        fetchLyrics(songId)
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

        if (_lyricsPageState.value.autoChange) {
            val info =
                "${_lyricsPageState.value.playingSong.title} ${_lyricsPageState.value.playingSong.artist}"
            searchSong(info)
        }
    }

    fun toggleSearchSheet() {
        _searchSheet.update {
            it.not()
        }
    }

}