package com.shub39.rush.lyrics.presentation.viewmodels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shub39.rush.core.data.ExtractedColors
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.LyricsPagePreferences
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.presentation.errorStringRes
import com.shub39.rush.core.presentation.sortMapByKeys
import com.shub39.rush.lyrics.data.listener.MediaListener
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.lyrics.breakLyrics
import com.shub39.rush.lyrics.presentation.lyrics.toSongUi
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
    private val otherPrefs: OtherPreferences,
    private val imageLoader: ImageLoader
): ViewModel() {

    private var observeJob: Job? = null

    private val _state = stateLayer.lyricsState

    val state = _state.asStateFlow()
        .onStart {
            observePlayback()
            observeDatastore()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
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
                    _state.update {
                        it.copy(
                            autoChange = !it.autoChange
                        )
                    }
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

                is LyricsPageAction.UpdateExtractedColors -> {
                    updateExtractedColors(action.context)
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

                is LyricsPageAction.OnHypnoticToggle -> {
                    lyricsPrefs.updateHypnoticCanvas(action.pref)
                }

                is LyricsPageAction.OnMeshSpeedChange -> {
                    _state.update {
                        it.copy(
                            meshSpeed = action.speed
                        )
                    }
                }

                is LyricsPageAction.OnVibrantToggle -> {
                    lyricsPrefs.updateLyricsColor(
                        if (action.pref) CardColors.VIBRANT else CardColors.MUTED
                    )
                }

                is LyricsPageAction.OnToggleColorPref -> {
                    lyricsPrefs.updateUseExtractedFlow(action.pref)
                }

                is LyricsPageAction.OnUpdatemBackground -> {
                    lyricsPrefs.updateCardBackground(action.color)
                }

                is LyricsPageAction.OnUpdatemContent -> {
                    lyricsPrefs.updateCardContent(action.color)
                }

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
            }
        }
    }

    private fun observeDatastore() = viewModelScope.launch {
        observeJob?.cancel()
        observeJob = launch {
            lyricsPrefs.getUseExtractedFlow()
                .onEach { pref ->
                    _state.update {
                        it.copy(
                            useExtractedColors = pref
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

            otherPrefs.getMaxLinesFlow()
                .onEach { lines ->
                    _state.update {
                        it.copy(
                            maxLines = lines
                        )
                    }
                }
                .launchIn(this)

            lyricsPrefs.getHypnoticCanvasFlow()
                .onEach { hyp ->
                    _state.update {
                        it.copy(
                            hypnoticCanvas = hyp
                        )
                    }
                }
                .launchIn(this)
        }
    }

    // Observes playback position and speed
    private fun observePlayback() {
        viewModelScope.launch {
            combine(
                MediaListener.songPositionFlow,
                MediaListener.playbackSpeedFlow,
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

    // extract colors using palette api
    private suspend fun updateExtractedColors(context: Context) {
        val request = ImageRequest.Builder(context)
            .data(_state.value.song?.artUrl)
            .allowHardware(false)
            .build()
        val result = (imageLoader.execute(request) as? SuccessResult)?.drawable

        result.let { drawable ->
            if (drawable != null) {
                Palette.from(drawable.toBitmap()).generate { palette ->
                    palette?.let { colors ->
                        val extractedColors = ExtractedColors(
                            cardBackgroundDominant =
                            Color(
                                colors.vibrantSwatch?.rgb ?: colors.lightVibrantSwatch?.rgb
                                ?: colors.darkVibrantSwatch?.rgb ?: colors.dominantSwatch?.rgb
                                ?: Color.DarkGray.toArgb()
                            ),
                            cardContentDominant =
                            Color(
                                colors.vibrantSwatch?.bodyTextColor
                                    ?: colors.lightVibrantSwatch?.bodyTextColor
                                    ?: colors.darkVibrantSwatch?.bodyTextColor
                                    ?: colors.dominantSwatch?.bodyTextColor
                                    ?: Color.White.toArgb()
                            ),
                            cardBackgroundMuted =
                            Color(
                                colors.mutedSwatch?.rgb ?: colors.darkMutedSwatch?.rgb
                                ?: colors.lightMutedSwatch?.rgb ?: Color.DarkGray.toArgb()
                            ),
                            cardContentMuted =
                            Color(
                                colors.mutedSwatch?.bodyTextColor ?: colors.darkMutedSwatch?.bodyTextColor
                                ?: colors.lightMutedSwatch?.bodyTextColor ?: Color.White.toArgb()
                            )
                        )

                        _state.update { lyricsPageState ->
                            lyricsPageState.copy(
                                extractedColors = extractedColors
                            )
                        }

                        stateLayer.sharePageState.update {
                            it.copy(
                                extractedColors = extractedColors
                            )
                        }
                    }
                }
            }
        }
    }
}