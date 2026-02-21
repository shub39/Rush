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
import com.shub39.rush.domain.interfaces.SharePagePreferences
import com.shub39.rush.presentation.share.SharePageAction
import com.shub39.rush.presentation.share.SharePageState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ShareVM(stateLayer: SharedStates, private val datastore: SharePagePreferences) : ViewModel() {

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
                    .getCardFitFlow()
                    .onEach { fit -> _state.update { it.copy(cardFit = fit) } }
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
                    .getCardFontFlow()
                    .onEach { font -> _state.update { it.copy(cardFont = font) } }
                    .launchIn(this)

                datastore
                    .showRushBranding()
                    .onEach { pref -> _state.update { it.copy(rushBranding = pref) } }
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
                is SharePageAction.OnUpdateCardFit -> datastore.updateCardFit(action.fit)
                is SharePageAction.OnUpdateCardRoundness ->
                    datastore.updateCardRoundness(action.roundness)
                is SharePageAction.OnUpdateCardTheme -> datastore.updateCardTheme(action.theme)
                is SharePageAction.OnUpdateCardFont -> datastore.updateCardFont(action.font)
                is SharePageAction.OnUpdateAlbumArtShape ->
                    datastore.updateAlbumArtShape(action.shape)
                is SharePageAction.OnToggleRushBranding -> datastore.updateRushBranding(action.pref)
            }
        }
    }
}
