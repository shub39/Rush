package com.shub39.rush.core.domain

import com.shub39.rush.core.domain.data_classes.LrcLibSong
import com.shub39.rush.core.domain.data_classes.SearchResult
import com.shub39.rush.core.domain.data_classes.Song
import kotlinx.coroutines.flow.Flow

interface SongRepo {
    // network
    suspend fun fetchSong(id: Long): Result<Song, SourceError>
    suspend fun scrapeGeniusLyrics(id: Long, url: String): Result<String, SourceError>
    suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError>
    suspend fun searchLrcLib(track: String, artist: String): Result<List<LrcLibSong>, SourceError>

    // database input/ output
    suspend fun insertSong(song: Song)
    suspend fun getSongs(): Flow<List<Song>>
    suspend fun getAllSongs(): List<Song>
    suspend fun getSong(id: Long): Song
    suspend fun getSong(query: String): List<Song>
    suspend fun deleteSong(id: Long)
    suspend fun updateLrcLyrics(id: Long, plainLyrics: String, syncedLyrics: String?)
    suspend fun deleteAllSongs()
}