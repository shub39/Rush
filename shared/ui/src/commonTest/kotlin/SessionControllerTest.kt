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
package com.shub39.rush.shared.ui.session

import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.SourceError
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.CorrectionSearchResult
import com.shub39.rush.shared.core.interfaces.PaletteGenerator
import com.shub39.rush.shared.core.interfaces.SongRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class SessionControllerTest {
    private fun song(id: Long = 1L, lyrics: String = "line one\nline two", synced: String? = null) =
        Song(
            id = id,
            title = "Title",
            artists = "Artist",
            lyrics = lyrics,
            album = "Album",
            sourceUrl = "",
            artUrl = null,
            geniusLyrics = null,
            syncedLyrics = synced,
            dateAdded = 0,
            ttmlLyrics = null,
        )

    private fun controller(repo: SongRepository, scope: kotlinx.coroutines.CoroutineScope) =
        SessionController(
            repo = repo,
            paletteGenerator =
                object : PaletteGenerator {
                    override suspend fun generatePaletteFromUrl(url: String) = ExtractedColors()
                },
            analytics =
                object : AnalyticsWrapper {
                    override fun trackEvent(event: AnalyticsEvent, properties: Map<String, Any>) =
                        Unit
                },
            scope = scope,
        )

    @Test
    fun `loadSaved exposes the song as the single source of truth`() = runTest {
        val controller = controller(FakeRepo(songs = listOf(song())), this)

        val loaded = controller.loadSaved(1L)

        assertTrue(loaded)
        assertEquals(1L, controller.state.value.song?.id)
        assertFalse(controller.state.value.isLoading)
        assertNull(controller.state.value.error)
        assertEquals(Sources.LRCLIB, controller.state.value.source)
    }

    @Test
    fun `loadSaved reports an error instead of crashing for a missing song`() = runTest {
        val controller = controller(FakeRepo(songs = emptyList()), this)

        val loaded = controller.loadSaved(42L)

        assertFalse(loaded)
        assertNull(controller.state.value.song)
        assertFalse(controller.state.value.isLoading)
        assertNotNull(controller.state.value.error)
    }

    @Test
    fun `loadRemote surfaces fetch errors`() = runTest {
        val controller =
            controller(
                FakeRepo(fetchResult = Result.Error(SourceError.Network.NO_INTERNET, "no net")),
                this,
            )

        val loaded = controller.loadRemote(searchResult())

        assertFalse(loaded)
        assertFalse(controller.state.value.isLoading)
        assertNotNull(controller.state.value.error)
    }

    @Test
    fun `loadRemote only auto syncs when synced lyrics exist`() = runTest {
        val withSynced =
            controller(
                FakeRepo(fetchResult = Result.Success(song(synced = "[00:01.00]line"))),
                this,
            )

        withSynced.loadRemote(searchResult(), autoSync = true)
        assertTrue(withSynced.state.value.sync)
        assertTrue(withSynced.state.value.syncedAvailable)

        val withoutSynced = controller(FakeRepo(fetchResult = Result.Success(song())), this)

        withoutSynced.loadRemote(searchResult(), autoSync = true)
        assertFalse(withoutSynced.state.value.sync)
        assertFalse(withoutSynced.state.value.syncedAvailable)
    }

    private fun searchResult() =
        SearchResult(
            id = 9L,
            title = "Title",
            artist = "Artist",
            album = null,
            artUrl = "",
            url = "",
        )

    private class FakeRepo(
        private val songs: List<Song> = emptyList(),
        private val fetchResult: Result<Song, SourceError> =
            Result.Success(
                Song(
                    id = 9L,
                    title = "Title",
                    artists = "Artist",
                    lyrics = "",
                    album = null,
                    sourceUrl = "",
                    artUrl = null,
                    geniusLyrics = null,
                    syncedLyrics = null,
                    dateAdded = 0,
                    ttmlLyrics = null,
                )
            ),
    ) : SongRepository {
        override suspend fun fetchSong(result: SearchResult) = fetchResult

        override suspend fun searchGenius(query: String) =
            Result.Success<List<SearchResult>, SourceError>(emptyList())

        override suspend fun updateGenius(id: Long): Result<String, SourceError> =
            Result.Error<String, SourceError>(SourceError.Network.REQUEST_FAILED)

        override suspend fun searchCorrections(track: String, artist: String) =
            Result.Success<List<CorrectionSearchResult>, SourceError>(emptyList())

        override suspend fun insertSong(song: Song) = Unit

        override suspend fun getSongs(): Flow<List<Song>> = flowOf(songs)

        override suspend fun getAllSongs(): List<Song> = songs

        override suspend fun getSong(id: Long): Song =
            songs.firstOrNull { it.id == id } ?: error("Song $id not found")

        override suspend fun getSong(query: String): List<Song> =
            songs.filter { it.title.contains(query, ignoreCase = true) }

        override suspend fun deleteSong(id: Long) = Unit

        override suspend fun correctLyrics(id: Long, searchResult: CorrectionSearchResult) = Unit

        override suspend fun deleteAllSongs() = Unit
    }
}
