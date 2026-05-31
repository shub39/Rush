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
package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.interfaces.LyricsPagePreferences
import com.shub39.rush.shared.core.interfaces.PaletteGenerator
import com.shub39.rush.shared.core.interfaces.RomanizationProvider
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.PlaybackInfo
import com.shub39.rush.shared.ui.lyrics.breakLyrics
import com.shub39.rush.shared.ui.lyrics.toSongUi
import com.shub39.rush.shared.ui.sortMapByKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LyricsVM(
    private val stateLayer: SharedStates,
    private val repo: SongRepository,
    private val lyricsPrefs: LyricsPagePreferences,
    private val paletteGenerator: PaletteGenerator,
    private val romanization: RomanizationProvider,
) : ViewModel() {

    private var observeJob: Job? = null
    private var observePlaybackJob: Job? = null
    private var romanizationJob: Job? = null

    private val _state = stateLayer.lyricsState
    private val _playbackInfo = MutableStateFlow(PlaybackInfo())

    val state =
        _state
            .asStateFlow()
            .onStart { observeDatastore() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LyricsPageState())
    val playbackInfo =
        _playbackInfo
            .asStateFlow()
            .onStart { observePlayback() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlaybackInfo())

    fun onAction(action: LyricsPageAction) {
        viewModelScope.launch {
            when (action) {
                is LyricsPageAction.OnCorrectionSearch -> {
                    _state.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = true)) }

                    when (val result = repo.searchCorrections(action.track, action.artist)) {
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    lrcCorrect =
                                        it.lrcCorrect.copy(error = errorStringRes(result.error))
                                )
                            }
                        }

                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    lrcCorrect = it.lrcCorrect.copy(searchResults = result.data)
                                )
                            }
                        }
                    }

                    _state.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = false)) }
                }

                is LyricsPageAction.OnToggleAutoChange -> {
                    val newPref = !_state.value.autoChange

                    _state.update { it.copy(autoChange = newPref) }

                    stateLayer.savedPageState.update { it.copy(autoChange = newPref) }

                    if (newPref) MediaListener.onSeekEagerly()
                }

                is LyricsPageAction.OnToggleSearchSheet -> {
                    stateLayer.searchSheetState.update { it.copy(visible = !it.visible) }
                }

                is LyricsPageAction.OnUpdateShareLines -> {
                    stateLayer.sharePageState.update {
                        it.copy(
                            songDetails = action.songDetails,
                            selectedLines = _state.value.selectedLines.sortMapByKeys(),
                        )
                    }
                }

                is LyricsPageAction.OnUpdateSongLyrics -> {
                    repo.correctLyrics(action.id, action.searchResult)

                    val song = repo.getSong(action.id).toSongUi()

                    _state.update {
                        it.copy(
                            syncedAvailable = song.syncedLyrics != null || song.ttmlLyrics != null,
                            lyricsState = LyricsState.Loaded(song = song),
                        )
                    }

                    generateRomanizedLyrics()
                }

                is LyricsPageAction.UpdateExtractedColors ->
                    launch(Dispatchers.Default) {
                        val colors = paletteGenerator.generatePaletteFromUrl(action.url)

                        _state.update { it.copy(extractedColors = colors) }

                        stateLayer.sharePageState.update { it.copy(extractedColors = colors) }

                        stateLayer.savedPageState.update { it.copy(extractedColors = colors) }
                    }

                is LyricsPageAction.OnSourceChange -> {
                    _state.update { it.copy(source = action.source, selectedLines = emptyMap()) }
                }

                is LyricsPageAction.OnSync -> {
                    _state.update { it.copy(sync = action.sync) }
                }

                is LyricsPageAction.OnLyricsCorrect -> {
                    _state.update { it.copy(lyricsCorrect = action.show) }
                }

                is LyricsPageAction.OnChangeSelectedLines -> {
                    _state.update { it.copy(selectedLines = action.lines) }
                }

                is LyricsPageAction.OnChangeLyricsBackground ->
                    lyricsPrefs.updateLyricsBackground(action.background)

                is LyricsPageAction.OnUpdateColorType -> lyricsPrefs.updateLyricsColor(action.color)

                is LyricsPageAction.OnToggleColorPref ->
                    lyricsPrefs.updateUseExtractedFlow(action.pref)

                is LyricsPageAction.OnUpdatemBackground ->
                    lyricsPrefs.updateCardBackground(action.color)

                is LyricsPageAction.OnExpressiveLyricsChange ->
                    lyricsPrefs.updateExpressiveSyllablesPref(action.pref)

                is LyricsPageAction.OnUpdatemContent -> lyricsPrefs.updateCardContent(action.color)

                is LyricsPageAction.OnScrapeGeniusLyrics -> {
                    _state.update { it.copy(scraping = Pair(true, null)) }

                    when (val result = repo.scrapeGeniusLyrics(action.id, action.url)) {
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    scraping =
                                        Pair(
                                            false,
                                            LyricsState.LyricsError(
                                                errorCode = errorStringRes(result.error),
                                                debugMessage = result.message,
                                            ),
                                        )
                                )
                            }
                        }

                        is Result.Success -> {
                            _state.update { lyricsPageState ->
                                lyricsPageState.copy(
                                    scraping = Pair(false, null),
                                    lyricsState =
                                        (lyricsPageState.lyricsState as? LyricsState.Loaded)?.let {
                                            it.copy(
                                                song =
                                                    it.song.copy(
                                                        geniusLyrics = breakLyrics(result.data)
                                                    )
                                            )
                                        } ?: LyricsState.Idle,
                                )
                            }

                            generateRomanizedLyrics()
                        }
                    }
                }

                is LyricsPageAction.OnAlignmentChange ->
                    lyricsPrefs.updateLyricAlignment(action.alignment)

                is LyricsPageAction.OnFontSizeChange -> lyricsPrefs.updateFontSize(action.size)

                is LyricsPageAction.OnLineHeightChange ->
                    lyricsPrefs.updateLineHeight(action.height)

                is LyricsPageAction.OnLetterSpacingChange ->
                    lyricsPrefs.updateLetterSpacing(action.spacing)

                LyricsPageAction.OnCustomisationReset -> lyricsPrefs.reset()

                is LyricsPageAction.OnFullscreenChange -> lyricsPrefs.setFullScreen(action.pref)

                is LyricsPageAction.OnMaxLinesChange -> lyricsPrefs.updateMaxLines(action.lines)

                LyricsPageAction.OnPauseOrResume ->
                    MediaListener.pauseOrResume(_playbackInfo.value.speed == 0f)

                is LyricsPageAction.OnSeek -> {
                    MediaListener.seek(action.position)
                    _playbackInfo.update { it.copy(position = action.position) }
                }

                is LyricsPageAction.OnSetPosition -> {
                    _playbackInfo.update { it.copy(position = action.position) }
                }

                is LyricsPageAction.OnBlurSyncedChange -> lyricsPrefs.updateBlurSynced(action.pref)
                LyricsPageAction.OnPlayNext -> MediaListener.playNext()
                LyricsPageAction.OnPlayPrevious -> MediaListener.playPrevious()

                is LyricsPageAction.OnRomanizationToggle -> {
                    lyricsPrefs.updateRomanizationEnabled(action.enabled)
                    if (action.enabled) {
                        generateRomanizedLyrics()
                    } else {
                        romanizationJob?.cancel()

                        val song =
                            (_state.value.lyricsState as? LyricsState.Loaded)?.song ?: return@launch

                        _state.update {
                            it.copy(
                                lyricsState =
                                    LyricsState.Loaded(
                                        song =
                                            song.copy(
                                                romanizedLyrics = emptyMap(),
                                                romanizedGeniusLyrics = emptyMap(),
                                                romanizedSyncedLyrics = emptyMap(),
                                                romanizedTtmlLyrics = emptyMap(),
                                            )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun generateRomanizedLyrics() {
        romanizationJob?.cancel()
        romanizationJob =
            viewModelScope.launch(Dispatchers.Default) {
                if (!_state.value.romanizationEnabled) return@launch

                val song = (_state.value.lyricsState as? LyricsState.Loaded)?.song ?: return@launch

                // Plain lyrics (LRCLIB)
                val romanizedLyrics =
                    song.lyrics.associate { entry ->
                        romanization.romanize(entry.value)?.let { romanized ->
                            entry.key to romanized
                        } ?: (entry.key to "")
                    }

                // Genius lyrics — offset to avoid collision with LRCLIB keys
                val romanizedGeniusLyrics =
                    song.geniusLyrics?.associate { entry ->
                        romanization.romanize(entry.value)?.let { romanized ->
                            entry.key to romanized
                        } ?: (entry.key to "")
                    } ?: emptyMap()

                // Synced lyrics — time-based keys
                val romanizedSyncedLyrics =
                    song.syncedLyrics?.associate { lyric ->
                        romanization.romanize(lyric.text)?.let { romanized ->
                            lyric.time to romanized
                        } ?: (lyric.time to "")
                    } ?: emptyMap()

                // TTML lyrics — startTime in ms as key
                val romanizedTTMLLyrics =
                    song.ttmlLyrics?.associate { parsedLine ->
                        romanization.romanize(parsedLine.text)?.let { romanized ->
                            parsedLine.startTime to romanized
                        } ?: (parsedLine.startTime to "")
                    } ?: emptyMap()

                _state.update {
                    it.copy(
                        lyricsState =
                            LyricsState.Loaded(
                                song =
                                    song.copy(
                                        romanizedLyrics = romanizedLyrics,
                                        romanizedGeniusLyrics = romanizedGeniusLyrics,
                                        romanizedSyncedLyrics = romanizedSyncedLyrics,
                                        romanizedTtmlLyrics = romanizedTTMLLyrics,
                                    )
                            )
                    )
                }
            }
    }

    private fun observeDatastore() {
        observeJob?.cancel()
        observeJob =
            viewModelScope.launch {
                lyricsPrefs
                    .getBlurSynced()
                    .onEach { pref -> _state.update { it.copy(blurSyncedLyrics = pref) } }
                    .launchIn(this)

                lyricsPrefs
                    .getLyricAlignmentFlow()
                    .onEach { pref ->
                        _state.update {
                            it.copy(textPrefs = it.textPrefs.copy(lyricsAlignment = pref))
                        }
                    }
                    .launchIn(this)

                lyricsPrefs
                    .getFontSizeFlow()
                    .onEach { pref ->
                        _state.update { it.copy(textPrefs = it.textPrefs.copy(fontSize = pref)) }
                    }
                    .launchIn(this)

                lyricsPrefs
                    .getLineHeightFlow()
                    .onEach { pref ->
                        _state.update { it.copy(textPrefs = it.textPrefs.copy(lineHeight = pref)) }
                    }
                    .launchIn(this)

                lyricsPrefs
                    .getLetterSpacingFlow()
                    .onEach { pref ->
                        _state.update {
                            it.copy(textPrefs = it.textPrefs.copy(letterSpacing = pref))
                        }
                    }
                    .launchIn(this)

                lyricsPrefs
                    .getCardBackgroundFlow()
                    .onEach { color -> _state.update { it.copy(mCardBackground = color) } }
                    .launchIn(this)

                lyricsPrefs
                    .getCardContentFlow()
                    .onEach { color -> _state.update { it.copy(mCardContent = color) } }
                    .launchIn(this)

                lyricsPrefs
                    .getLyricsColorFlow()
                    .onEach { color -> _state.update { it.copy(cardColors = color) } }
                    .launchIn(this)

                lyricsPrefs
                    .getMaxLinesFlow()
                    .onEach { lines -> _state.update { it.copy(maxLines = lines) } }
                    .launchIn(this)

                lyricsPrefs
                    .getFullScreenFlow()
                    .onEach { pref -> _state.update { it.copy(fullscreen = pref) } }
                    .launchIn(this)

                lyricsPrefs
                    .getLyricsBackgroundFlow()
                    .onEach { hyp -> _state.update { it.copy(lyricsBackground = hyp) } }
                    .launchIn(this)

                lyricsPrefs
                    .getExpressiveSyllablesPref()
                    .onEach { pref -> _state.update { it.copy(expressiveSyllables = pref) } }
                    .launchIn(this)

                lyricsPrefs
                    .getRomanizationEnabledFlow()
                    .onEach { enabled ->
                        _state.update { it.copy(romanizationEnabled = enabled) }
                        if (enabled) {
                            generateRomanizedLyrics()
                        }
                    }
                    .launchIn(this)

                // Watch for song changes and regenerate romanization if enabled
                _state
                    .map { (it.lyricsState as? LyricsState.Loaded)?.song?.id }
                    .distinctUntilChanged()
                    .onEach {
                        if (_state.value.romanizationEnabled) {
                            generateRomanizedLyrics()
                        }
                    }
                    .launchIn(this)
            }
    }

    // Observes playback position and speed
    private fun observePlayback() {
        observePlaybackJob?.cancel()
        observePlaybackJob =
            viewModelScope.launch(Dispatchers.Default) {
                combine(MediaListener.songPositionFlow, MediaListener.playbackSpeedFlow, ::Pair)
                    .collectLatest { (position, speed) ->
                        val start = System.currentTimeMillis()

                        while (isActive) {
                            val elapsed = (speed * (System.currentTimeMillis() - start)).toLong()

                            _playbackInfo.update {
                                it.copy(
                                    position = maxOf(it.position, position + elapsed),
                                    speed = speed,
                                )
                            }

                            delay(100)
                        }
                    }
            }
    }
}
