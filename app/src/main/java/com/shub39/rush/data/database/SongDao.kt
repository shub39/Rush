/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs") fun getAllSongs(): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSong(songEntity: SongEntity)

    @Query("DELETE FROM songs WHERE id = :id") suspend fun deleteSong(id: Long)

    @Update suspend fun updateSong(songEntity: SongEntity)

    @Query("SELECT * FROM songs WHERE id = :id") suspend fun getSongById(id: Long): SongEntity

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%'")
    suspend fun searchSong(query: String): List<SongEntity>

    @Query("UPDATE songs SET lyrics = :lrcAsync, syncedLyrics = :lrcSync WHERE id = :id")
    suspend fun updateLrcLyricsById(id: Long, lrcAsync: String, lrcSync: String?)

    @Query("UPDATE songs SET geniusLyrics = :lyrics WHERE id = :id")
    suspend fun updateGeniusLyrics(id: Long, lyrics: String)

    @Query("DELETE FROM songs") suspend fun deleteAllSongs()
}
