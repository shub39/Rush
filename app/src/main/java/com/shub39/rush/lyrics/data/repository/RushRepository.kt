package com.shub39.rush.lyrics.data.repository

import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SourceError
import com.shub39.rush.lyrics.data.database.SongDao
import com.shub39.rush.lyrics.data.mappers.toSong
import com.shub39.rush.lyrics.data.mappers.toSongEntity
import com.shub39.rush.lyrics.data.network.SongProvider
import com.shub39.rush.lyrics.domain.LrcLibSong
import com.shub39.rush.lyrics.domain.SearchResult
import com.shub39.rush.lyrics.domain.Song
import com.shub39.rush.lyrics.domain.SongRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RushRepository(
    private val localDao: SongDao
) : SongRepo {
    override suspend fun fetchSong(id: Long): Result<Song, SourceError> {
        return SongProvider.fetchLyrics(id).also {
            if (it is Result.Success) {
                localDao.insertSong(it.data.toSongEntity())
            }
        }
    }

    override suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError> {
        return SongProvider.geniusSearch(query)
    }

    override suspend fun searchLrcLib(
        track: String,
        artist: String
    ): Result<List<LrcLibSong>, SourceError> {
        return SongProvider.lrcLibSearch(track, artist)
    }

    override fun getSongs(): Flow<List<Song>> {
        return localDao
            .getAllSongs().map { entities ->
                entities.map { it.toSong() }
            }
    }

    override suspend fun getSong(id: Long): Song {
        return localDao.getSongById(id).toSong()
    }

    override suspend fun getSong(query: String): List<Song> {
        return localDao.searchSong(query).map { it.toSong() }
    }

    override suspend fun deleteSong(id: Long) {
        localDao.deleteSong(id)
    }

    override suspend fun updateLrcLyrics(id: Long, plainLyrics: String, syncedLyrics: String?) {
        localDao.updateLrcLyricsById(id, plainLyrics, syncedLyrics)
    }

    override suspend fun deleteAllSongs() {
        localDao.deleteAllSongs()
    }
}