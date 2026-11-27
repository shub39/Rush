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
package com.shub39.rush.shared.logic.backup

import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.core.backup.EXPORT_SCHEMA_VERSION
import com.shub39.rush.shared.core.backup.ExportSchema
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.interfaces.CorrectionSearchResult
import com.shub39.rush.shared.core.interfaces.SongRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class ExportImplTest {
    /**
     * The envelope is written by hand so songs can be encoded one at a time, so this is the test
     * that keeps it honest: whatever [ExportImpl] streams has to decode back into [ExportSchema].
     */
    @Test
    fun writesAJsonDocumentThatRestoreCanRead() = runTest {
        val songs = List(SONG_COUNT) { song(it.toLong()) }
        val chunks = mutableListOf<String>()

        val exported = ExportImpl(FakeSongRepository(songs)).exportToJson { chunks += it }

        assertTrue(exported)

        val decoded = json.decodeFromString<ExportSchema>(chunks.joinToString(separator = ""))

        assertEquals(EXPORT_SCHEMA_VERSION, decoded.schemaVersion)
        assertEquals(songs.map { it.id }, decoded.songs.map { it.id })
        assertEquals(songs.map { it.lyrics }, decoded.songs.map { it.lyrics })
    }

    @Test
    fun streamsOneSongAtATimeInsteadOfTheWholeDocument() = runTest {
        val songs = List(SONG_COUNT) { song(it.toLong()) }
        val chunks = mutableListOf<String>()

        ExportImpl(FakeSongRepository(songs)).exportToJson { chunks += it }

        // The point of streaming is that no single chunk is anywhere near the size of the whole
        // document, which is what used to be allocated in one go and blow up the heap.
        val largestChunk = chunks.maxOf { it.length }
        val documentSize = chunks.sumOf { it.length }

        assertTrue(
            largestChunk * 10 < documentSize,
            "Largest chunk ($largestChunk chars) is too big for a $documentSize char export",
        )
    }

    @Test
    fun pagesThroughTheSongsInsteadOfLoadingThemAll() = runTest {
        val songs = List(SONG_COUNT) { song(it.toLong()) }
        val repository = FakeSongRepository(songs)

        ExportImpl(repository).exportToJson {}

        assertTrue(repository.pageRequests > 1, "Expected the export to page through the songs")
    }

    @Test
    fun reportsFailureWhenTheDestinationFails() = runTest {
        val exported =
            ExportImpl(FakeSongRepository(List(2) { song(it.toLong()) }))
                .exportToJson { throw IllegalStateException("Disk full") }

        assertFalse(exported)
    }

    @Test
    fun writesAnEmptyLibraryAsAnEmptyDocument() = runTest {
        val chunks = mutableListOf<String>()

        val exported = ExportImpl(FakeSongRepository(emptyList())).exportToJson { chunks += it }

        assertTrue(exported)

        val decoded = json.decodeFromString<ExportSchema>(chunks.joinToString(separator = ""))

        assertEquals(EXPORT_SCHEMA_VERSION, decoded.schemaVersion)
        assertTrue(decoded.songs.isEmpty())
    }

    private fun song(id: Long) =
        Song(
            id = id,
            title = "Title $id",
            artists = "Artist $id",
            lyrics = """Line one of $id
                |Line two of $id
                |Line three of $id"""
                .trimMargin(),
            album = "Album $id",
            sourceUrl = "https://genius.com/song/$id",
            artUrl = null,
            geniusLyrics = "Genius lyrics of $id",
            syncedLyrics = "[00:01.00] Line one of $id",
            ttmlLyrics = null,
            dateAdded = id,
        )

    private companion object {
        /** Enough songs to span several pages, so paging is actually exercised. */
        const val SONG_COUNT = 75

        /** The same lenient format [com.shub39.rush.shared.logic.backup.RestoreImpl] reads with. */
        val json = Json { ignoreUnknownKeys = true }
    }

    private class FakeSongRepository(private val songs: List<Song>) : SongRepository {
        var pageRequests: Int = 0
            private set

        override suspend fun getSongsPage(limit: Int, offset: Int): List<Song> {
            pageRequests += 1
            return songs.drop(offset).take(limit)
        }

        override suspend fun getAllSongs(): List<Song> = songs

        override suspend fun getSongs(): Flow<List<Song>> = flowOf(songs)

        override suspend fun getSong(id: Long): Song = songs.first { it.id == id }

        override suspend fun getSong(query: String): List<Song> =
            songs.filter { it.title.contains(query, ignoreCase = true) }

        override suspend fun fetchSong(result: SearchResult): Result<Song, SourceError> =
            error("Not used by the export")

        override suspend fun searchGenius(query: String): Result<List<SearchResult>, SourceError> =
            error("Not used by the export")

        override suspend fun updateGenius(id: Long): Result<String, SourceError> =
            error("Not used by the export")

        override suspend fun searchCorrections(
            track: String,
            artist: String,
        ): Result<List<CorrectionSearchResult>, SourceError> = error("Not used by the export")

        override suspend fun insertSong(song: Song) = Unit

        override suspend fun deleteSong(id: Long) = Unit

        override suspend fun correctLyrics(id: Long, searchResult: CorrectionSearchResult) = Unit

        override suspend fun deleteAllSongs() = Unit
    }
}
