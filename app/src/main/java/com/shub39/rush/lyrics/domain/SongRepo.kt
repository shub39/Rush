package com.shub39.rush.lyrics.domain

import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SourceError
import kotlinx.coroutines.flow.Flow

interface SongRepo {
    suspend fun fetchSong(id: Long): Result<Song, SourceError>
    suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError>
    suspend fun searchLrcLib(track: String, artist: String): Result<List<LrcLibSong>, SourceError>

    fun getSongs(): Flow<List<Song>>
    suspend fun getAllSongs(): List<Song>
    suspend fun getSong(id: Long): Song
    suspend fun getSong(query: String): List<Song>
    suspend fun deleteSong(id: Long)
    suspend fun updateLrcLyrics(id: Long, plainLyrics: String, syncedLyrics: String?)
    suspend fun deleteAllSongs()
}