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
import com.shub39.rush.shared.core.enums.AlbumArtShape
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.CardTheme
import com.shub39.rush.shared.core.enums.CornerRadius
import com.shub39.rush.shared.core.interfaces.SharePagePreferences
import com.shub39.rush.shared.ui.share.SharePageAction
import com.shub39.rush.shared.ui.share.SharePageState
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ShareVM(stateLayer: SharedStates, @Provided private val datastore: SharePagePreferences) :
    ViewModel() {

    private var observeJob: Job? = null

    private val _state = stateLayer.sharePageState

    val state =
        _state
            .asStateFlow()
            .onStart { observeDatastore() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SharePageState())

    private fun observeDatastore() =
        viewModelScope.launch {
            observeJob?.cancel()
            observeJob = launch {
                datastore
                    .getAlbumArtShapeFlow()
                    .onEach { shape -> _state.update { it.copy(albumArtShape = shape) } }
                    .launchIn(this)

                datastore
                    .getCardBackgroundFlow()
                    .onEach { color -> _state.update { it.copy(cardBackground = color) } }
                    .launchIn(this)

                datastore
                    .getCardContentFlow()
                    .onEach { color -> _state.update { it.copy(cardContent = color) } }
                    .launchIn(this)

                datastore
                    .getCardColorFlow()
                    .onEach { colors -> _state.update { it.copy(cardColors = colors) } }
                    .launchIn(this)

                datastore
                    .getCardRoundnessFlow()
                    .onEach { roundness -> _state.update { it.copy(cardRoundness = roundness) } }
                    .launchIn(this)

                datastore
                    .getCardThemeFlow()
                    .onEach { theme -> _state.update { it.copy(cardTheme = theme) } }
                    .launchIn(this)

                datastore
                    .getFullscreenShare()
                    .onEach { fullScreen -> _state.update { it.copy(fullScreen = fullScreen) } }
                    .launchIn(this)
            }
        }

    fun onAction(action: SharePageAction) {
        viewModelScope.launch {
            when (action) {
                is SharePageAction.OnUpdateCardBackground ->
                    datastore.updateCardBackground(action.color)

                is SharePageAction.OnUpdateCardColor -> datastore.updateCardColor(action.color)
                is SharePageAction.OnUpdateCardContent -> datastore.updateCardContent(action.color)
                is SharePageAction.OnUpdateCardRoundness ->
                    datastore.updateCardRoundness(action.roundness)

                is SharePageAction.OnUpdateCardTheme -> datastore.updateCardTheme(action.theme)
                is SharePageAction.OnUpdateAlbumArtShape ->
                    datastore.updateAlbumArtShape(action.shape)

                is SharePageAction.OnToggleFullScreen ->
                    datastore.updateFullscreenShare(action.fullScreen)

                SharePageAction.OnRandomize -> {
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
                                if (newBackground.luminance() >= 0.5) {
                                    Color.Black
                                } else {
                                    Color.White
                                }
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
        }
    }
}
