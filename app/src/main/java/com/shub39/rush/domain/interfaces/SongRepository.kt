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
