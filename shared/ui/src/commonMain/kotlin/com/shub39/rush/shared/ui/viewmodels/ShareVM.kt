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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shub39.rush.shared.core.AnalyticsEvent
import com.shub39.rush.shared.core.dataclasses.SongDetails
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.AlbumArtShape
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.CardTheme
import com.shub39.rush.shared.core.enums.CornerRadius
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.SharePagePreferences
import com.shub39.rush.shared.ui.session.SessionController
import com.shub39.rush.shared.ui.share.SharePageAction
import com.shub39.rush.shared.ui.share.SharePageState
import com.shub39.rush.shared.ui.sortMapByKeys
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/**
 * Owns the share card customisations only. The song and the selected lines are projected from
 * [SessionController] instead of being copied in when the share sheet opens.
 */
@KoinViewModel
class ShareVM(
    private val sessionController: SessionController,
    @Provided private val datastore: SharePagePreferences,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private val cardPrefs = MutableStateFlow(SharePageState())

    val state =
        combine(cardPrefs, sessionController.state) { prefs, session ->
                prefs.copy(
                    songDetails = session.song?.toSongDetails() ?: SongDetails("", "", null, ""),
                    selectedLines = session.selectedLines.sortMapByKeys(),
                    extractedColors = session.extractedColors,
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SharePageState())

    init {
        analytics.trackEvent(AnalyticsEvent.SHARE_OPENED, emptyMap())
        observeCardPreferences()
    }

    fun onAction(action: SharePageAction) {
        when (action) {
            SharePageAction.OnShare -> {
                val current = state.value

                analytics.trackEvent(
                    AnalyticsEvent.CARD_SHARED,
                    mapOf(
                        "theme" to current.cardTheme.name,
                        "color" to current.cardColors.name,
                        "roundness" to current.cardRoundness.name,
                        "fullScreen" to current.fullScreen.toString(),
                        "albumArtShape" to current.albumArtShape.name,
                        "cardBackground" to current.cardBackground.toString(),
                        "cardContent" to current.cardContent.toString(),
                    ),
                )
            }

            is SharePageAction.OnUpdateCardBackground ->
                viewModelScope.launch { datastore.updateCardBackground(action.color) }

            is SharePageAction.OnUpdateCardColor ->
                viewModelScope.launch { datastore.updateCardColor(action.color) }

            is SharePageAction.OnUpdateCardContent ->
                viewModelScope.launch { datastore.updateCardContent(action.color) }

            is SharePageAction.OnUpdateCardRoundness ->
                viewModelScope.launch { datastore.updateCardRoundness(action.roundness) }

            is SharePageAction.OnUpdateCardTheme ->
                viewModelScope.launch { datastore.updateCardTheme(action.theme) }

            is SharePageAction.OnUpdateAlbumArtShape ->
                viewModelScope.launch { datastore.updateAlbumArtShape(action.shape) }

            is SharePageAction.OnToggleFullScreen ->
                viewModelScope.launch { datastore.updateFullscreenShare(action.fullScreen) }

            SharePageAction.OnRandomize -> randomize()
        }
    }

    private fun randomize() {
        viewModelScope.launch {
            when (val newCardColor = CardColors.entries.random()) {
                CardColors.CUSTOM -> {
                    datastore.updateCardColor(newCardColor)

                    val newBackground =
                        Color(
                            red = Random.nextFloat(),
                            blue = Random.nextFloat(),
                            green = Random.nextFloat(),
                            alpha = 1f,
                        )
                    val newContent =
                        if (newBackground.luminance() >= 0.5) Color.Black else Color.White

                    datastore.updateCardBackground(newBackground.toArgb())
                    datastore.updateCardContent(newContent.toArgb())
                }

                else -> datastore.updateCardColor(newCardColor)
            }

            datastore.updateCardRoundness(CornerRadius.entries.random())
            datastore.updateCardTheme(CardTheme.entries.random())
            datastore.updateAlbumArtShape(AlbumArtShape.entries.random())
            datastore.updateFullscreenShare(Random.nextBoolean())
        }
    }

    private fun observeCardPreferences() {
        datastore
            .getAlbumArtShapeFlow()
            .onEach { shape -> cardPrefs.update { it.copy(albumArtShape = shape) } }
            .launchIn(viewModelScope)

        datastore
            .getCardBackgroundFlow()
            .onEach { color -> cardPrefs.update { it.copy(cardBackground = color) } }
            .launchIn(viewModelScope)

        datastore
            .getCardContentFlow()
            .onEach { color -> cardPrefs.update { it.copy(cardContent = color) } }
            .launchIn(viewModelScope)

        datastore
            .getCardColorFlow()
            .onEach { colors -> cardPrefs.update { it.copy(cardColors = colors) } }
            .launchIn(viewModelScope)

        datastore
            .getCardRoundnessFlow()
            .onEach { roundness -> cardPrefs.update { it.copy(cardRoundness = roundness) } }
            .launchIn(viewModelScope)

        datastore
            .getCardThemeFlow()
            .onEach { theme -> cardPrefs.update { it.copy(cardTheme = theme) } }
            .launchIn(viewModelScope)

        datastore
            .getFullscreenShare()
            .onEach { fullScreen -> cardPrefs.update { it.copy(fullScreen = fullScreen) } }
            .launchIn(viewModelScope)
    }
}

private fun SongUi.toSongDetails() =
    SongDetails(title = title, artist = artists, album = album, artUrl = artUrl ?: "")
