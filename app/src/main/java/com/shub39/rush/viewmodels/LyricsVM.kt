package com.shub39.rush.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.core.data.PaletteGenerator
import com.shub39.rush.core.data.listener.MediaListenerImpl
import com.shub39.rush.core.domain.LyricsPagePreferences
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SongRepo
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.presentation.errorStringRes
import com.shub39.rush.core.presentation.sortMapByKeys
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.lyrics.breakLyrics
import com.shub39.rush.lyrics.toSongUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LyricsVM(
    private val stateLayer: StateLayer,
    private val repo: SongRepo,
    private val lyricsPrefs: LyricsPagePreferences,
    private val paletteGenerator: PaletteGenerator,
) : ViewModel() {

    private var observeJob: Job? = null

    private val _state = stateLayer.lyricsState

    val state = _state.asStateFlow()
        .onStart {
            observePlayback()
            observeDatastore()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            LyricsPageState()
        )

    fun onAction(action: LyricsPageAction) {
        viewModelScope.launch {
            when (action) {
                is LyricsPageAction.OnLrcSearch -> {
                    _state.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = true)) }

                    when (val result = repo.searchLrcLib(action.track, action.artist)) {
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    lrcCorrect = it.lrcCorrect.copy(
                                        error = errorStringRes(result.error)
                                    )
                                )
                            }
                        }

                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    lrcCorrect = it.lrcCorrect.copy(
                                        searchResults = result.data
                                    )
                                )
                            }
                        }
                    }

                    _state.update { it.copy(lrcCorrect = it.lrcCorrect.copy(searching = false)) }
                }

                is LyricsPageAction.OnToggleAutoChange -> {
                    val newPref = !_state.value.autoChange

                    _state.update {
                        it.copy(
                            autoChange = newPref
                        )
                    }

                    stateLayer.savedPageState.update {
                        it.copy(
                            autoChange = newPref
                        )
                    }

                    if (newPref) MediaListenerImpl.onSeekEagerly()
                }

                is LyricsPageAction.OnToggleSearchSheet -> {
                    stateLayer.searchSheetState.update {
                        it.copy(
                            visible = !it.visible
                        )
                    }
                }

                is LyricsPageAction.OnUpdateShareLines -> {
                    stateLayer.sharePageState.update {
                        it.copy(
                            songDetails = action.songDetails,
                            selectedLines = sortMapByKeys(_state.value.selectedLines)
                        )
                    }
                }

                is LyricsPageAction.OnUpdateSongLyrics -> {
                    repo.updateLrcLyrics(action.id, action.plainLyrics, action.syncedLyrics)

                    val song = repo.getSong(action.id).toSongUi()

                    _state.update {
                        it.copy(
                            song = song,
                            syncedAvailable = song.syncedLyrics != null
                        )
                    }
                }

                is LyricsPageAction.UpdateExtractedColors -> launch(Dispatchers.Default) {
                    val colors = paletteGenerator.generatePaletteFromUrl(action.url)

                    _state.update {
                        it.copy(extractedColors = colors)
                    }

                    stateLayer.sharePageState.update {
                        it.copy(extractedColors = colors)
                    }

                    stateLayer.savedPageState.update {
                        it.copy(extractedColors = colors)
                    }
                }

                is LyricsPageAction.OnSourceChange -> {
                    _state.update {
                        it.copy(
                            source = action.source,
                            selectedLines = emptyMap()
                        )
                    }
                }

                is LyricsPageAction.OnSync -> {
                    _state.update {
                        it.copy(
                            sync = action.sync
                        )
                    }
                }

                is LyricsPageAction.OnSyncAvailable -> {
                    _state.update {
                        it.copy(
                            syncedAvailable = action.sync
                        )
                    }
                }

                is LyricsPageAction.OnLyricsCorrect -> {
                    _state.update {
                        it.copy(
                            lyricsCorrect = action.show
                        )
                    }
                }

                is LyricsPageAction.OnChangeSelectedLines -> {
                    _state.update {
                        it.copy(
                            selectedLines = action.lines
                        )
                    }
                }

                is LyricsPageAction.OnChangeLyricsBackground -> {
                    if (action.background == LyricsBackground.WAVE && !action.audioPermissionGranted) {
                        action.requestAudioPermission()
                    } else {
                        lyricsPrefs.updateLyricsBackround(action.background)
                    }
                }

                is LyricsPageAction.OnUpdateColorType -> lyricsPrefs.updateLyricsColor(action.color)

                is LyricsPageAction.OnToggleColorPref -> lyricsPrefs.updateUseExtractedFlow(action.pref)

                is LyricsPageAction.OnUpdatemBackground -> lyricsPrefs.updateCardBackground(action.color)

                is LyricsPageAction.OnUpdatemContent -> lyricsPrefs.updateCardContent(action.color)

                is LyricsPageAction.OnScrapeGeniusLyrics -> {
                    _state.update { it.copy(scraping = Pair(true, null)) }

                    when (val result = repo.scrapeGeniusLyrics(action.id, action.url)) {
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    scraping = Pair(false, result.error)
                                )
                            }
                        }

                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    scraping = Pair(false, null),
                                    song = it.song?.copy(
                                        geniusLyrics = breakLyrics(result.data)
                                    )
                                )
                            }
                        }
                    }
                }

                is LyricsPageAction.OnAlignmentChange -> lyricsPrefs.updateLyricAlignment(action.alignment)

                is LyricsPageAction.OnFontSizeChange -> lyricsPrefs.updateFontSize(action.size)

                is LyricsPageAction.OnLineHeightChange -> lyricsPrefs.updateLineHeight(action.height)

                is LyricsPageAction.OnLetterSpacingChange -> lyricsPrefs.updateLetterSpacing(action.spacing)

                LyricsPageAction.OnCustomisationReset -> lyricsPrefs.reset()

                is LyricsPageAction.OnFullscreenChange -> lyricsPrefs.setFullScreen(action.pref)

                is LyricsPageAction.OnMaxLinesChange -> lyricsPrefs.updateMaxLines(action.lines)

                LyricsPageAction.OnPauseOrResume -> MediaListenerImpl.pauseOrResume(_state.value.playingSong.speed == 0f)

                is LyricsPageAction.OnSeek -> MediaListenerImpl.seek(action.position)

                is LyricsPageAction.OnBlurSyncedChange -> lyricsPrefs.updateBlurSynced(action.pref)
            }
        }
    }

    private fun observeDatastore() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            lyricsPrefs.getBlurSynced()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            blurSyncedLyrics = pref
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getLyricAlignmentFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            textPrefs = it.textPrefs.copy(textAlign = pref)
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getFontSizeFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            textPrefs = it.textPrefs.copy(fontSize = pref)
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getLineHeightFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            textPrefs = it.textPrefs.copy(lineHeight = pref)
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getLetterSpacingFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            textPrefs = it.textPrefs.copy(letterSpacing = pref)
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getCardBackgroundFlow()
                .onEach { color ->
                    _state.update {
                        it.copy(
                            mCardBackground = color
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getCardContentFlow()
                .onEach { color ->
                    _state.update {
                        it.copy(
                            mCardContent = color
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getLyricsColorFlow()
                .onEach { color ->
                    _state.update {
                        it.copy(
                            cardColors = color
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getMaxLinesFlow()
                .onEach { lines ->
                    _state.update {
                        it.copy(
                            maxLines = lines
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getFullScreenFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            fullscreen = pref
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getLyricsBackgroundFlow()
                .onEach { hyp ->
                    _state.update {
                        it.copy(
                            lyricsBackground = hyp
                        )
                    }
                }
                .launchIn(this)
        }
    }

    // Observes playback position and speed
    private fun observePlayback() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(
                MediaListenerImpl.songPositionFlow,
                MediaListenerImpl.playbackSpeedFlow,
                ::Pair
            ).collectLatest { (position, speed) ->
                val start = System.currentTimeMillis()

                while (isActive) {
                    val elapsed = (speed * (System.currentTimeMillis() - start)).toLong()

                    _state.update { lyricsPageState ->
                        lyricsPageState.copy(
                            playingSong = lyricsPageState.playingSong.copy(
                                position = position + elapsed,
                                speed = speed
                            )
                        )
                    }

                    delay(500)
                }
            }
        }
    }
}