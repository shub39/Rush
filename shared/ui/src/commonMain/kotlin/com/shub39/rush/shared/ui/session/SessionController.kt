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
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.CorrectionSearchResult
import com.shub39.rush.shared.core.interfaces.PaletteGenerator
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.lyrics.breakLyrics
import com.shub39.rush.shared.ui.lyrics.toSongUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

/**
 * The single owner of the song the app is currently showing.
 *
 * Every screen (lyrics, saved, share) reads [state] instead of keeping its own copy, so there is
 * exactly one place that decides what "the loaded song" means. Runs on an application scoped
 * coroutine scope so a load survives navigation.
 */
@Single
class SessionController(
    private val repo: SongRepository,
    private val paletteGenerator: PaletteGenerator,
    private val analytics: AnalyticsWrapper,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    private val loadMutex = Mutex()
    private var requestId = 0L

    /**
     * Loads a song that already lives in the local database, without touching the network.
     *
     * @return `true` when the song was loaded and is now in [state].
     */
    suspend fun loadSaved(id: Long, origin: String = "saved", autoSync: Boolean = false): Boolean =
        loadMutex.withLock {
            val request = beginLoad(loadingLabel = "", autoSync = autoSync)

            val song =
                try {
                    repo.getSong(id).toSongUi()
                } catch (e: Exception) {
                    failLoad(request, SourceError.Data.UNKNOWN, e.message)
                    return@withLock false
                }

            trackOpened(song, origin)
            succeedLoad(request, song, autoSync)
            true
        }

    /**
     * Fetches a search result from the network and stores it locally.
     *
     * @return `true` when the song was loaded and is now in [state].
     */
    suspend fun loadRemote(
        result: SearchResult,
        origin: String = "online_search",
        autoSync: Boolean = false,
    ): Boolean =
        loadMutex.withLock {
            val request = beginLoad("${result.title} - ${result.artist}", autoSync)

            when (val fetched = repo.fetchSong(result)) {
                is Result.Error -> {
                    failLoad(request, fetched.error, fetched.message)
                    return@withLock false
                }

                is Result.Success -> {
                    val song = fetched.data.toSongUi()

                    trackOpened(song, origin)
                    trackFetched(song)
                    succeedLoad(request, song, autoSync)

                    return@withLock true
                }
            }
        }

    /** Re-downloads the Genius lyrics of the loaded song. */
    fun refreshGenius(id: Long) {
        scope.launch {
            _state.update { it.copy(geniusLoading = true, geniusError = null) }

            when (val result = repo.updateGenius(id)) {
                is Result.Error ->
                    _state.update {
                        it.copy(
                            geniusLoading = false,
                            geniusError = SessionError(errorStringRes(result.error), result.message),
                        )
                    }

                is Result.Success ->
                    _state.update { current ->
                        current.copy(
                            geniusLoading = false,
                            geniusError = null,
                            song = current.song?.copy(geniusLyrics = breakLyrics(result.data)),
                        )
                    }
            }
        }
    }

    /** Replaces the plain/synced lyrics of a saved song with a user picked correction. */
    fun correctLyrics(id: Long, searchResult: CorrectionSearchResult) {
        scope.launch {
            repo.correctLyrics(id, searchResult)

            val song = repo.getSong(id).toSongUi()

            analytics.trackEvent(
                AnalyticsEvent.LYRICS_CORRECTED,
                mapOf("title" to song.title, "artists" to song.artists),
            )

            _state.update {
                it.copy(
                    song = song,
                    syncedAvailable = song.syncedLyrics != null || song.ttmlLyrics != null,
                )
            }
        }
    }

    fun selectSource(source: Sources) {
        _state.update { it.copy(source = source, selectedLines = emptyMap()) }
    }

    fun setSync(sync: Boolean) {
        _state.update { it.copy(sync = sync) }
    }

    fun setSelectedLines(lines: Map<Int, String>) {
        _state.update { it.copy(selectedLines = lines) }
    }

    fun updateExtractedColors(url: String) {
        scope.launch(Dispatchers.Default) {
            val colors = paletteGenerator.generatePaletteFromUrl(url)

            _state.update { it.copy(extractedColors = colors) }
        }
    }

    private fun beginLoad(loadingLabel: String, autoSync: Boolean): Long {
        this.requestId += 1

        _state.update {
            it.copy(
                isLoading = true,
                loadingLabel = loadingLabel,
                error = null,
                selectedLines = emptyMap(),
            )
        }

        return this.requestId
    }

    private fun succeedLoad(request: Long, song: SongUi, autoSync: Boolean) {
        if (request != requestId) return

        _state.update {
            it.copy(
                song = song,
                isLoading = false,
                loadingLabel = "",
                error = null,
                source = if (song.lyrics.isNotEmpty()) Sources.LRCLIB else Sources.GENIUS,
                syncedAvailable = song.syncedLyrics != null || song.ttmlLyrics != null,
                sync = autoSync && (song.syncedLyrics != null || song.ttmlLyrics != null),
                selectedLines = emptyMap(),
            )
        }
    }

    private fun failLoad(request: Long, error: SourceError, debug: String?) {
        if (request != requestId) return

        _state.update {
            it.copy(
                isLoading = false,
                loadingLabel = "",
                error = SessionError(errorStringRes(error), debug),
            )
        }
    }

    private fun trackOpened(song: SongUi, origin: String) {
        analytics.trackEvent(
            AnalyticsEvent.LYRICS_OPENED,
            mapOf("title" to song.title, "artists" to song.artists, "source" to origin),
        )
    }

    private fun trackFetched(song: SongUi) {
        analytics.trackEvent(
            AnalyticsEvent.SONG_FETCHED,
            mapOf("title" to song.title, "artists" to song.artists),
        )
    }
}
