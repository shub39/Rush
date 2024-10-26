package com.shub39.rush.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Update
    suspend fun updateSong(song: Song)

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): Song

    @Query("SELECT * FROM songs WHERE title LIKE :query || '%'")
    suspend fun searchSong(query: String): List<Song>

    @Query("UPDATE songs SET lyrics = :lrcAsync, syncedLyrics = :lrcSync WHERE id = :id")
    suspend fun updateLrcLyricsById(id: Long, lrcAsync: String, lrcSync: String?)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}