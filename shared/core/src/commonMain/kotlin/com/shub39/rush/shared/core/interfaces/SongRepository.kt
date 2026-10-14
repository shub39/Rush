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
package com.shub39.rush.shared.core.interfaces

import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    // network
    /** Fetches lyrics for [result] from the lyrics sources cascade and saves the song. */
    suspend fun fetchSong(result: SearchResult, durationMs: Long? = null): Result<Song, SourceError>

    suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError>

    suspend fun updateGenius(id: Long): Result<String, SourceError>

    /** Lyrics found by every enabled source, for manual correction. */
    suspend fun searchLyrics(query: TrackQuery): Result<List<LyricsCandidate>, SourceError>

    /** Best lyrics found by a single source. */
    suspend fun fetchLyricsFrom(
        provider: LyricsProviderId,
        query: TrackQuery,
    ): Result<LyricsCandidate, SourceError>

    // database input/ output
    suspend fun insertSong(song: Song)

    suspend fun getSongs(): Flow<List<Song>>

    suspend fun getAllSongs(): List<Song>

    suspend fun getSong(id: Long): Song

    suspend fun getSong(query: String): List<Song>

    suspend fun deleteSong(id: Long)

    /** Replaces the lyrics of song [id] with [candidate]. */
    suspend fun applyLyrics(id: Long, candidate: LyricsCandidate)

    suspend fun deleteAllSongs()
}
