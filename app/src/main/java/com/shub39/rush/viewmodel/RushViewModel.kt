package com.shub39.rush.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.database.AudioFile
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.Song
import com.shub39.rush.database.SongDatabase
import com.shub39.rush.listener.MediaListener
import com.shub39.rush.lyrics.SongProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RushViewModel(
    application: Application,
) : ViewModel() {

    private val database = SongDatabase.getDatabase(application)
    private val songDao = database.songDao()

    private val _songs = MutableStateFlow(listOf<Song>())
    private val _searchResults = MutableStateFlow(listOf<SearchResult>())
    private val _localSearchResults = MutableStateFlow(listOf<SearchResult>())
    private val _currentSongId = MutableStateFlow<Long?>(null)
    private val _currentSong = MutableStateFlow<Song?>(null)
    private val _currentPlayingSongInfo = MutableStateFlow<Pair<String, String>?>(null)
    private val _isSearchingLyrics = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")
    private val _isFetchingLyrics = MutableStateFlow(false)
    private val _fetchQuery = MutableStateFlow("")
    private val _currentSongPosition = MutableStateFlow(0L)
    private val _shareLines = MutableStateFlow(mapOf<Int, String>())
    private val _lastSearched = MutableStateFlow("")
    private val _errorQuery = MutableStateFlow("")
    private val _autoChange = MutableStateFlow(false)
    private val _error = MutableStateFlow(false)
    private val _batchDownloading = MutableStateFlow(false)
    private val _downloadIndexes = MutableStateFlow(mapOf<Int, Boolean>())

    val songs: StateFlow<List<Song>> get() = _songs
    val songsSortedAsc: Flow<List<Song>> get() = _songs.map { it -> it.sortedBy { it.title } }
    val songsSortedDesc: Flow<List<Song>> get() = _songs.map { it -> it.sortedByDescending { it.title } }
    val searchResults: StateFlow<List<SearchResult>> get() = _searchResults
    val localSearchResults: StateFlow<List<SearchResult>> get() = _localSearchResults
    val currentSong: MutableStateFlow<Song?> get() = _currentSong
    val currentPlayingSongInfo: StateFlow<Pair<String, String>?> get() = _currentPlayingSongInfo
    val isSearchingLyrics: StateFlow<Boolean> get() = _isSearchingLyrics
    val searchQuery: StateFlow<String> get() = _searchQuery
    val autoChange: StateFlow<Boolean> get() = _autoChange
    val isFetchingLyrics: StateFlow<Boolean> get() = _isFetchingLyrics
    val fetchQuery: StateFlow<String> get() = _fetchQuery
    val currentSongPosition: StateFlow<Long> get() = _currentSongPosition
    val error: StateFlow<Boolean> get() = _error
    val shareLines: StateFlow<Map<Int, String>> get() = _shareLines
    val batchDownloading: StateFlow<Boolean> get() = _batchDownloading
    val downloadIndexes: StateFlow<Map<Int, Boolean>> get() = _downloadIndexes

    val songsGroupedArtists: Flow<List<Map.Entry<String, List<Song>>>>
        get() =
            _songs.map { songsList ->
                songsList.groupBy { it.artists }.entries.toList()
            }
    val songsGroupedAlbums: Flow<List<Map.Entry<String, List<Song>>>>
        get() =
            _songs.map { songsList ->
                songsList.groupBy { it.album ?: "???" }.entries.toList()
            }

    init {
        viewModelScope.launch {
            _songs.value = songDao.getAllSongs()
            MediaListener.songInfoFlow.collect { songInfo ->
                _currentPlayingSongInfo.value = songInfo
                if (autoChange.value) {
                    searchSong("${songInfo.first} ${songInfo.second}".trim())
                }
                Log.d("RushViewModel", "SongInfo: $songInfo")
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
                    _currentSongPosition.value = position + elapsed
                    Log.d("RushViewModel", "Position: $position, elapsed: $elapsed")
                    delay(500)
                }
            }
        }
    }

    fun changeCurrentSong(songId: Long) {
        _currentSongId.value = songId
        fetchLyrics(songId)
    }

    fun updateShareLines(lines: Map<Int, String>) {
        _shareLines.value = lines
    }

    fun toggleAutoChange() {
        _autoChange.value = !_autoChange.value
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            songDao.deleteSong(song)
            _songs.value = songDao.getAllSongs()
        }
    }

    fun searchSong(
        query: String,
        fetch: Boolean = _autoChange.value,
    ) {
        if (query.isEmpty() || query == _lastSearched.value || _isSearchingLyrics.value) return

        viewModelScope.launch {
            _searchQuery.value = query
            _isSearchingLyrics.value = true

            try {
                val result = withContext(Dispatchers.IO) {
                    SongProvider.search(query)
                }

                if (result.isSuccess) {
                    _searchResults.value = result.getOrNull() ?: emptyList()
                    if (fetch) _lastSearched.value = query
                    _error.value = false
                } else {
                    Log.e("ViewModel", result.exceptionOrNull()?.message, result.exceptionOrNull())
                    _errorQuery.value = query
                    _error.value = true
                }
            } finally {
                _isSearchingLyrics.value = false
            }

            if (fetch && !error.value && _searchResults.value.isNotEmpty()) {
                fetchLyrics(_searchResults.value.first().id)
            } else {
                _autoChange.value = false
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

    fun retry() {
        if (_errorQuery.value.toLongOrNull() != null) {
            fetchLyrics(songId = _errorQuery.value.toLong())
        } else {
            searchSong(_errorQuery.value)
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
                    SongProvider.search("${audioFile.title} ${audioFile.artist}")
                }

                if (result.isSuccess) {
                    val id = result.getOrNull()?.first()?.id ?: return@forEachIndexed

                    if (id in savedSongs) {

                        _downloadIndexes.value += index to true

                    } else {
                        val song = withContext(Dispatchers.IO) {
                            SongProvider.fetchLyrics(id)
                        }

                        if (song.isSuccess) {
                            songDao.insertSong(song.getOrNull()!!)
                            _downloadIndexes.value += index to true
                            Log.d("ViewModel", "Song downloaded: ${song.getOrNull()!!.title}")
                        } else {
                            _downloadIndexes.value += index to false
                        }
                    }
                } else {

                    _downloadIndexes.value += index to false

                }
            }

            _songs.value = songDao.getAllSongs()
            _batchDownloading.value = false
        }
    }

    fun clearIndexes() {
        _downloadIndexes.value = emptyMap()
    }

    private fun fetchLyrics(songId: Long = _currentSongId.value!!) {
        viewModelScope.launch {
            val song = _searchResults.value.find { it.id == songId }
            _fetchQuery.value = "${song?.title} - ${song?.artist}"
            _isFetchingLyrics.value = true

            try {
                if (songId in songs.value.map { it.id }) {
                    val result = songDao.getSongById(songId)
                    _currentSong.value = result
                    _error.value = false
                } else {
                    val result = withContext(Dispatchers.IO) {
                        SongProvider.fetchLyrics(songId)
                    }

                    if (result.isSuccess) {
                        _currentSong.value = result.getOrNull()
                        songDao.insertSong(_currentSong.value!!)
                        _songs.value = songDao.getAllSongs()
                        _error.value = false
                    } else {
                        Log.e(
                            "ViewModel",
                            result.exceptionOrNull()?.message,
                            result.exceptionOrNull()
                        )
                        _errorQuery.value = songId.toString()
                        _error.value = true
                    }
                }
            } finally {
                _isFetchingLyrics.value = false
            }
        }
    }
}