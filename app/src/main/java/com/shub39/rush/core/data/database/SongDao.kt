package com.shub39.rush.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(songEntity: SongEntity)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Long)

    @Update
    suspend fun updateSong(songEntity: SongEntity)

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%'")
    suspend fun searchSong(query: String): List<SongEntity>

    @Query("UPDATE songs SET lyrics = :lrcAsync, syncedLyrics = :lrcSync WHERE id = :id")
    suspend fun updateLrcLyricsById(id: Long, lrcAsync: String, lrcSync: String?)

    @Query("UPDATE songs SET geniusLyrics = :lyrics WHERE id = :id")
    suspend fun updateGeniusLyrics(id: Long, lyrics: String)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}