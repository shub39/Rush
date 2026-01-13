package com.shub39.rush.domain.interfaces

import com.shub39.rush.domain.Result
import com.shub39.rush.domain.SourceError
import com.shub39.rush.domain.dataclasses.LrcLibSong
import com.shub39.rush.domain.dataclasses.SearchResult
import com.shub39.rush.domain.dataclasses.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    // network
    suspend fun fetchSong(result: SearchResult): Result<Song, SourceError>
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