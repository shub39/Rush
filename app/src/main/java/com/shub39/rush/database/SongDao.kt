package com.shub39.rush.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): Song
}