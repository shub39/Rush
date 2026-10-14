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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.LyricsPagePreferences
import com.shub39.rush.shared.core.interfaces.LyricsSourcePreferences
import com.shub39.rush.shared.core.interfaces.PaletteGenerator
import com.shub39.rush.shared.core.interfaces.RomanizationProvider
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.core.interfaces.SyncOffsetManager
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.core.util.TrackMatcher
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.OffsetTarget
import com.shub39.rush.shared.ui.lyrics.PlaybackInfo
import com.shub39.rush.shared.ui.lyrics.SourcePicker
import com.shub39.rush.shared.ui.lyrics.SourceResult
import com.shub39.rush.shared.ui.lyrics.breakLyrics
import com.shub39.rush.shared.ui.lyrics.toSongUi
import com.shub39.rush.shared.ui.sortMapByKeys
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class LyricsVM(
    private val stateLayer: SharedStates,
    @Provided private val repo: SongRepository,
    @Provided private val lyricsPrefs: LyricsPagePreferences,
    @Provided private val paletteGenerator: PaletteGenerator,
    @Provided private val romanization: RomanizationProvider,
    @Provided private val analytics: AnalyticsWrapper,
    @Provided private val lyricsSourcePreferences: LyricsSourcePreferences,
    @Provided private val syncOffsetManager: SyncOffsetManager,
) : ViewModel() {

    private var observeJob: Job? = null
    private val sourceJobs = mutableMapOf<LyricsProviderId, Job>()
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

                    when (
                        val result =
                            repo.searchLyrics(
                                TrackQuery(title = action.track, artist = action.artist)
                            )
                    ) {
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

                    analytics.trackEvent(
                        AnalyticsEvent.RUSH_MODE_TOGGLED,
                        mapOf("enabled" to newPref),
                    )

                    _state.update { it.copy(autoChange = newPref) }

                    stateLayer.savedPageState.update { it.copy(autoChange = newPref) }

                    if (newPref) MediaListener.onSeekEagerly()
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
                    repo.applyLyrics(action.id, action.candidate)

                    val song = repo.getSong(action.id).toSongUi()

                    analytics.trackEvent(
                        AnalyticsEvent.LYRICS_CORRECTED,
                        mapOf("title" to song.title, "artists" to song.artists),
                    )

                    val syncedAvailable = song.syncedLyrics != null || song.ttmlLyrics != null
                    _state.update {
                        it.copy(
                            syncedAvailable = syncedAvailable,
                            sync = it.sync && syncedAvailable,
                            source = Sources.LRCLIB,
                            lyricsState = LyricsState.Loaded(song = song),
                            sourcePicker = it.sourcePicker.copy(visible = false),
                        )
                    }

                    generateRomanizedLyrics()
                }

                is LyricsPageAction.OnShowSourcePicker -> {
                    if (action.show) openSourcePicker()
                    else
                        _state.update {
                            it.copy(sourcePicker = it.sourcePicker.copy(visible = false))
                        }
                }

                is LyricsPageAction.OnRetrySource -> loadSource(action.provider)

                is LyricsPageAction.OnAdjustOffset ->
                    syncOffsetManager.adjustSession(action.deltaMs)

                LyricsPageAction.OnResetOffsetAdjustment -> syncOffsetManager.resetSession()

                LyricsPageAction.OnBluetoothPermissionChanged -> syncOffsetManager.refresh()

                is LyricsPageAction.OnShowOffsetSheet -> {
                    _state.update { it.copy(offsetSheet = action.show) }
                }

                is LyricsPageAction.OnSaveOffset -> {
                    when (action.target) {
                        OffsetTarget.DEVICE -> syncOffsetManager.saveForDevice()
                        OffsetTarget.TYPE -> syncOffsetManager.saveForType()
                        OffsetTarget.SONG -> {
                            val song =
                                (_state.value.lyricsState as? LyricsState.Loaded)?.song
                                    ?: return@launch
                            updateSongOffset(
                                song.syncOffsetMs + _state.value.syncOffset.sessionAdjustMs
                            )
                            syncOffsetManager.resetSession()
                        }
                    }
                }

                LyricsPageAction.OnResetSongOffset -> updateSongOffset(0)

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

                is LyricsPageAction.OnChangeLyricsBackground -> {
                    lyricsPrefs.updateLyricsBackground(action.background)
                }

                is LyricsPageAction.OnUpdateColorType -> {
                    lyricsPrefs.updateLyricsColor(action.color)
                }

                is LyricsPageAction.OnUpdatemBackground -> {
                    lyricsPrefs.updateCardBackground(action.color)
                }

                is LyricsPageAction.OnExpressiveLyricsChange -> {
                    lyricsPrefs.updateExpressiveSyllablesPref(action.pref)
                }

                is LyricsPageAction.OnUpdatemContent -> {
                    lyricsPrefs.updateCardContent(action.color)
                }

                is LyricsPageAction.OnFetchGeniusLyrics -> {
                    _state.update { it.copy(fetchingGenius = Pair(true, null)) }

                    when (val result = repo.updateGenius(id = action.id)) {
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    fetchingGenius =
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
                                    fetchingGenius = Pair(false, null),
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

                is LyricsPageAction.OnAlignmentChange -> {
                    lyricsPrefs.updateLyricAlignment(action.alignment)
                }

                is LyricsPageAction.OnFontSizeChange -> lyricsPrefs.updateFontSize(action.size)

                is LyricsPageAction.OnLineHeightChange ->
                    lyricsPrefs.updateLineHeight(action.height)

                is LyricsPageAction.OnLetterSpacingChange ->
                    lyricsPrefs.updateLetterSpacing(action.spacing)

                LyricsPageAction.OnCustomisationReset -> {
                    lyricsPrefs.reset()
                }

                is LyricsPageAction.OnFullscreenChange -> {
                    lyricsPrefs.setFullScreen(action.pref)
                }

                is LyricsPageAction.OnMaxLinesChange -> lyricsPrefs.updateMaxLines(action.lines)

                LyricsPageAction.OnPauseOrResume ->
                    MediaListener.pauseOrResume(_playbackInfo.value.speed == 0f)

                is LyricsPageAction.OnSeek -> {
                    // lyric times are shifted by the sync offset, the player needs real times
                    MediaListener.seek((action.position + currentOffset()).coerceAtLeast(0))
                    _playbackInfo.update { it.copy(position = action.position) }
                }

                is LyricsPageAction.OnSetPosition -> {
                    _playbackInfo.update { it.copy(position = action.position) }
                }

                is LyricsPageAction.OnHideUIToggle -> {
                    lyricsPrefs.updateHideUI(action.enabled)
                }

                is LyricsPageAction.OnBlurSyncedChange -> {
                    lyricsPrefs.updateBlurSynced(action.pref)
                }

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

    /** Sync offset applied to the current lyrics: audio device + adjustment + song fine tuning. */
    private fun currentOffset(): Long {
        val state = _state.value
        val songOffset = (state.lyricsState as? LyricsState.Loaded)?.song?.syncOffsetMs ?: 0
        return state.syncOffset.totalOffset(songOffset)
    }

    private suspend fun updateSongOffset(offsetMs: Long) {
        val song = (_state.value.lyricsState as? LyricsState.Loaded)?.song ?: return
        repo.setSongOffset(song.id, offsetMs)

        _state.update { state ->
            val loaded = state.lyricsState as? LyricsState.Loaded
            if (loaded == null || loaded.song.id != song.id) state
            else
                state.copy(
                    lyricsState = loaded.copy(song = loaded.song.copy(syncOffsetMs = offsetMs))
                )
        }
    }

    private suspend fun openSourcePicker() {
        val song = (_state.value.lyricsState as? LyricsState.Loaded)?.song ?: return
        val providers =
            lyricsSourcePreferences
                .getSourcesFlow()
                .first()
                .filter { it.enabled }
                .map { it.provider }
        val sameSong = _state.value.sourcePicker.songId == song.id

        _state.update {
            it.copy(
                sourcePicker =
                    if (sameSong) it.sourcePicker.copy(visible = true, providers = providers)
                    else SourcePicker(visible = true, songId = song.id, providers = providers)
            )
        }

        providers
            .filter { !sameSong || _state.value.sourcePicker.results[it] !is SourceResult.Found }
            .forEach { loadSource(it) }
    }

    private fun loadSource(provider: LyricsProviderId) {
        val song = (_state.value.lyricsState as? LyricsState.Loaded)?.song ?: return
        val playing = _state.value.playingSong
        val query =
            TrackQuery(
                title = song.title,
                artist = song.artists,
                album = song.album,
                durationMs =
                    playing?.duration?.takeIf {
                        TrackMatcher.similarity(playing.title, song.title) >= 0.8
                    },
            )

        sourceJobs[provider]?.cancel()
        sourceJobs[provider] =
            viewModelScope.launch {
                updateSourceResult(song.id, provider, SourceResult.Loading)

                val result =
                    when (val response = repo.fetchLyricsFrom(provider, query)) {
                        is Result.Success -> SourceResult.Found(response.data)
                        is Result.Error -> SourceResult.Failed(errorStringRes(response.error))
                    }

                updateSourceResult(song.id, provider, result)
            }
    }

    private fun updateSourceResult(songId: Long, provider: LyricsProviderId, result: SourceResult) {
        _state.update {
            if (it.sourcePicker.songId != songId) it
            else
                it.copy(
                    sourcePicker =
                        it.sourcePicker.copy(
                            results = it.sourcePicker.results + (provider to result)
                        )
                )
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
                    .getHideUIFlow()
                    .onEach { pref -> _state.update { it.copy(hideUI = pref) } }
                    .launchIn(this)

                // Collecting the offset also keeps the audio output detection running
                syncOffsetManager.state
                    .onEach { offset -> _state.update { it.copy(syncOffset = offset) } }
                    .launchIn(this)

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
                    .onEach { songId ->
                        if (_state.value.romanizationEnabled) {
                            generateRomanizedLyrics()
                        }
                        // results of the source picker belong to the previous song
                        if (_state.value.sourcePicker.songId != songId) {
                            _state.update {
                                it.copy(sourcePicker = it.sourcePicker.copy(visible = false))
                            }
                        }
                    }
                    .launchIn(this)
            }
    }

    // Observes playback position and speed, shifted by the sync offset
    private fun observePlayback() {
        observePlaybackJob?.cancel()
        observePlaybackJob =
            viewModelScope.launch(Dispatchers.Default) {
                combine(MediaListener.songPositionFlow, MediaListener.playbackSpeedFlow, ::Pair)
                    .collectLatest { (position, speed) ->
                        val start = TimeSource.Monotonic.markNow()

                        while (this@launch.isActive) {
                            val elapsed = (speed * start.elapsedNow().inWholeMilliseconds).toLong()
                            val target = (position + elapsed - currentOffset()).coerceAtLeast(0)

                            _playbackInfo.update {
                                // Position updates lag a bit: ignore small backward jumps but
                                // follow seeks made in the player
                                val backwards = it.position - target
                                it.copy(
                                    position =
                                        if (backwards in 1 until SEEK_TOLERANCE_MS) it.position
                                        else target,
                                    speed = speed,
                                )
                            }

                            delay(100.milliseconds)
                        }
                    }
            }
    }

    private companion object {
        const val SEEK_TOLERANCE_MS = 1500L
    }
}
