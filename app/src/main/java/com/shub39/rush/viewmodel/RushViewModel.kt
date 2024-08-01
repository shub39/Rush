package com.shub39.rush.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.Song
import com.shub39.rush.database.SongDatabase
import com.shub39.rush.listener.MediaListener
import com.shub39.rush.lyrics.SongProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RushViewModel(
    application: Application,
) : ViewModel() {

    private val database = SongDatabase.getDatabase(application)
    private val songDao = database.songDao()
    private val _songs = MutableStateFlow(listOf<Song>())
    private val _searchResults = MutableStateFlow(listOf<SearchResult>())
    private val _currentSongId = MutableStateFlow<Long?>(null)
    private val _currentSong = MutableStateFlow<Song?>(null)
    private val _currentPlayingSongInfo = MutableStateFlow<Pair<String, String>?>(null)
    private val _isSearchingLyrics = MutableStateFlow(false)
    private val _isFetchingLyrics = MutableStateFlow(false)
    private val _currentSongPosition = MutableStateFlow(0L)

    val songs: StateFlow<List<Song>> get() = _songs
    val searchResults: StateFlow<List<SearchResult>> get() = _searchResults
    val currentSong: MutableStateFlow<Song?> get() = _currentSong
    val currentPlayingSongInfo: StateFlow<Pair<String, String>?> get() = _currentPlayingSongInfo
    val isSearchingLyrics: StateFlow<Boolean> get() = _isSearchingLyrics
    val isFetchingLyrics: StateFlow<Boolean> get() = _isFetchingLyrics
    val currentSongPosition: StateFlow<Long> get() = _currentSongPosition

    init {
        viewModelScope.launch {
            _songs.value = songDao.getAllSongs()
            MediaListener.songInfoFlow.collect { songInfo ->
                _currentPlayingSongInfo.value = songInfo
            }
        }
        viewModelScope.launch {
            MediaListener.songPositionFlow.collect { position ->
                _currentSongPosition.value = position
                Log.d("RushViewModel", "Position: $position")
            }
        }
    }

    fun changeCurrentSong(songId: Long) {
        _currentSongId.value = songId
        fetchLyrics(songId)
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            songDao.deleteSong(song)
            _songs.value = songDao.getAllSongs()
        }
    }

    fun searchSong(query: String) {
        if (query.isEmpty()) return

        viewModelScope.launch {
            _isSearchingLyrics.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    SongProvider.search(query)
                }
                if (result.isSuccess) {
                    _searchResults.value = result.getOrNull() ?: emptyList()
                } else {
                    Log.e("ViewModel", result.exceptionOrNull()?.message, result.exceptionOrNull())
                    _searchResults.value = emptyList()
                }
            } finally {
                _isSearchingLyrics.value = false
            }
        }
    }

    private fun fetchLyrics(songId: Long = _currentSongId.value!!) {
        viewModelScope.launch {
            _isFetchingLyrics.value = true
            try {
                if (songId in songs.value.map { it.id }) {
                    val result = songDao.getSongById(songId)
                    _currentSong.value = result
                } else {
                    val result = withContext(Dispatchers.IO) {
                        SongProvider.fetchLyrics(songId)
                    }
                    if (result.isSuccess) {
                        _currentSong.value = result.getOrNull()
                        songDao.insertSong(_currentSong.value!!)
                        _songs.value = songDao.getAllSongs()
                    } else {
                        Log.e(
                            "ViewModel",
                            result.exceptionOrNull()?.message,
                            result.exceptionOrNull()
                        )
                    }
                }
            } finally {
                _isFetchingLyrics.value = false
            }
        }
    }

}