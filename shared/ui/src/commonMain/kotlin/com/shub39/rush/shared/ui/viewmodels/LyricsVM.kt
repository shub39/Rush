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
package com.shub39.rush.shared.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.dataclasses.LyricsPrefs
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.interfaces.LyricsPagePreferences
import com.shub39.rush.shared.core.interfaces.RomanizationProvider
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.lyrics.LrcCorrect
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.PlaybackInfo
import com.shub39.rush.shared.ui.lyrics.RomanizedLyrics
import com.shub39.rush.shared.ui.lyrics.withRomanization
import com.shub39.rush.shared.ui.rushmode.RushAction
import com.shub39.rush.shared.ui.rushmode.RushController
import com.shub39.rush.shared.ui.rushmode.RushState
import com.shub39.rush.shared.ui.session.SessionController
import com.shub39.rush.shared.ui.session.SessionState
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/**
 * Projects the app scoped [SessionController] (loaded song), [RushController] (rush mode) and the
 * persisted [LyricsPrefs] into the single state object the lyrics screen renders.
 *
 * The view model owns no cross screen state of its own: every value is either derived from those
 * sources or is genuinely local to this screen, so there is nothing to keep in sync.
 */
@KoinViewModel
class LyricsVM(
    private val sessionController: SessionController,
    private val rushController: RushController,
    @Provided private val repo: SongRepository,
    @Provided private val lyricsPrefs: LyricsPagePreferences,
    @Provided private val romanization: RomanizationProvider,
) : ViewModel() {
    private val localUiState = MutableStateFlow(LocalLyricsUiState())
    private val romanized = MutableStateFlow(RomanizedLyrics())
    private val _playbackInfo = MutableStateFlow(PlaybackInfo())

    val playbackInfo = _playbackInfo.asStateFlow()

    val state =
        combine(
                sessionController.state,
                rushController.state,
                lyricsPrefs.getPreferencesFlow(),
                romanized,
                localUiState,
                ::buildState,
            )
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LyricsPageState())

    init {
        observeRomanization()
        observePlayback()
    }

    fun onAction(action: LyricsPageAction) {
        when (action) {
            is LyricsPageAction.OnCorrectionSearch -> searchCorrections(action.track, action.artist)

            LyricsPageAction.OnToggleRushMode -> rushController.onAction(RushAction.Toggle)

            LyricsPageAction.OnDisableRushMode -> rushController.onAction(RushAction.Disable)

            is LyricsPageAction.OnUpdateSongLyrics ->
                viewModelScope.launch {
                    sessionController.correctLyrics(action.id, action.searchResult)
                }

            is LyricsPageAction.UpdateExtractedColors ->
                sessionController.updateExtractedColors(action.url)

            is LyricsPageAction.OnSourceChange -> sessionController.selectSource(action.source)

            is LyricsPageAction.OnSync -> sessionController.setSync(action.sync)

            is LyricsPageAction.OnChangeSelectedLines ->
                sessionController.setSelectedLines(action.lines)

            is LyricsPageAction.OnFetchGeniusLyrics -> sessionController.refreshGenius(action.id)

            is LyricsPageAction.OnLyricsCorrect ->
                localUiState.update { it.copy(lyricsCorrect = action.show) }

            is LyricsPageAction.OnRomanizationToggle ->
                viewModelScope.launch { lyricsPrefs.updateRomanizationEnabled(action.enabled) }

            is LyricsPageAction.OnChangeLyricsBackground ->
                viewModelScope.launch { lyricsPrefs.updateLyricsBackground(action.background) }

            is LyricsPageAction.OnUpdateColorType ->
                viewModelScope.launch { lyricsPrefs.updateLyricsColor(action.color) }

            is LyricsPageAction.OnUpdatemBackground ->
                viewModelScope.launch { lyricsPrefs.updateCardBackground(action.color) }

            is LyricsPageAction.OnExpressiveLyricsChange ->
                viewModelScope.launch { lyricsPrefs.updateExpressiveSyllablesPref(action.pref) }

            is LyricsPageAction.OnUpdatemContent ->
                viewModelScope.launch { lyricsPrefs.updateCardContent(action.color) }

            is LyricsPageAction.OnAlignmentChange ->
                viewModelScope.launch { lyricsPrefs.updateLyricAlignment(action.alignment) }

            is LyricsPageAction.OnFontSizeChange ->
                viewModelScope.launch { lyricsPrefs.updateFontSize(action.size) }

            is LyricsPageAction.OnLineHeightChange ->
                viewModelScope.launch { lyricsPrefs.updateLineHeight(action.height) }

            is LyricsPageAction.OnLetterSpacingChange ->
                viewModelScope.launch { lyricsPrefs.updateLetterSpacing(action.spacing) }

            LyricsPageAction.OnCustomisationReset -> viewModelScope.launch { lyricsPrefs.reset() }

            is LyricsPageAction.OnFullscreenChange ->
                viewModelScope.launch { lyricsPrefs.setFullScreen(action.pref) }

            is LyricsPageAction.OnMaxLinesChange ->
                viewModelScope.launch { lyricsPrefs.updateMaxLines(action.lines) }

            is LyricsPageAction.OnHideUIToggle ->
                viewModelScope.launch { lyricsPrefs.updateHideUI(action.enabled) }

            is LyricsPageAction.OnBlurSyncedChange ->
                viewModelScope.launch { lyricsPrefs.updateBlurSynced(action.pref) }

            LyricsPageAction.OnPauseOrResume ->
                MediaListener.pauseOrResume(_playbackInfo.value.speed == 0f)

            is LyricsPageAction.OnSeek -> {
                MediaListener.seek(action.position)
                _playbackInfo.update { it.copy(position = action.position) }
            }

            is LyricsPageAction.OnSetPosition ->
                _playbackInfo.update { it.copy(position = action.position) }

            LyricsPageAction.OnPlayNext -> MediaListener.playNext()

            LyricsPageAction.OnPlayPrevious -> MediaListener.playPrevious()
        }
    }

    private fun searchCorrections(track: String, artist: String) {
        viewModelScope.launch {
            // Reset the previous run so stale results/errors never survive a new search
            localUiState.update {
                it.copy(
                    lrcCorrect =
                        it.lrcCorrect.copy(
                            searching = true,
                            error = null,
                            searchResults = emptyList(),
                        )
                )
            }

            when (val result = repo.searchCorrections(track, artist)) {
                is Result.Error ->
                    localUiState.update {
                        it.copy(
                            lrcCorrect = it.lrcCorrect.copy(error = errorStringRes(result.error))
                        )
                    }

                is Result.Success ->
                    localUiState.update {
                        it.copy(lrcCorrect = it.lrcCorrect.copy(searchResults = result.data))
                    }
            }

            localUiState.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = false)) }
        }
    }

    private fun buildState(
        session: SessionState,
        rush: RushState,
        prefs: LyricsPrefs,
        romanized: RomanizedLyrics,
        local: LocalLyricsUiState,
    ): LyricsPageState {
        val error = session.error
        val song = session.song?.withRomanization(romanized)

        return LyricsPageState(
            session = session,
            rush = rush,
            prefs = prefs,
            lyricsState =
                when {
                    session.isLoading -> LyricsState.Fetching(session.loadingLabel)
                    error != null -> LyricsState.LyricsError(error.code, error.debug)
                    song != null -> LyricsState.Loaded(song)
                    else -> LyricsState.Idle
                },
            lrcCorrect = local.lrcCorrect,
            lyricsCorrect = local.lyricsCorrect,
        )
    }

    /**
     * Romanization is derived from the loaded song plus the preference, instead of being written
     * into a copy of the song by hand.
     */
    private fun observeRomanization() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(
                    sessionController.state.map { it.song },
                    lyricsPrefs.getRomanizationEnabledFlow(),
                    ::Pair,
                )
                .distinctUntilChanged()
                .collectLatest { (song, enabled) ->
                    if (!enabled || song == null) {
                        romanized.value = RomanizedLyrics()
                        return@collectLatest
                    }

                    romanized.value = song.toRomanizedLyrics()
                }
        }
    }

    private suspend fun SongUi.toRomanizedLyrics() =
        RomanizedLyrics(
            plain =
                lyrics.associate { entry ->
                    entry.key to (romanization.romanize(entry.value) ?: "")
                },
            genius =
                geniusLyrics?.associate { entry ->
                    entry.key to (romanization.romanize(entry.value) ?: "")
                } ?: emptyMap(),
            synced =
                syncedLyrics?.associate { lyric ->
                    lyric.time to (romanization.romanize(lyric.text) ?: "")
                } ?: emptyMap(),
            ttml =
                ttmlLyrics?.associate { line ->
                    line.startTime to (romanization.romanize(line.text) ?: "")
                } ?: emptyMap(),
        )

    // Observes playback position and speed
    private fun observePlayback() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(MediaListener.songPositionFlow, MediaListener.playbackSpeedFlow, ::Pair)
                .collectLatest { (position, speed) ->
                    val start = TimeSource.Monotonic.markNow()

                    while (this@launch.isActive) {
                        val elapsed = (speed * start.elapsedNow().inWholeMilliseconds).toLong()

                        _playbackInfo.update {
                            it.copy(
                                position = maxOf(it.position, position + elapsed),
                                speed = speed,
                            )
                        }

                        delay(100.milliseconds)
                    }
                }
        }
    }
}

/** Purely local to the lyrics screen; nothing else reads or writes it. */
@Immutable
private data class LocalLyricsUiState(
    val lrcCorrect: LrcCorrect = LrcCorrect(),
    val lyricsCorrect: Boolean = false,
)
