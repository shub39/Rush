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
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.ui.errorStringRes
import com.shub39.rush.shared.ui.searchsheet.SearchSheetAction
import com.shub39.rush.shared.ui.searchsheet.SearchSheetState
import com.shub39.rush.shared.ui.searchsheet.toSearchResult
import com.shub39.rush.shared.ui.session.SessionController
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

/**
 * The search sheet: query in, results out. It no longer knows anything about rush mode, playback or
 * loading lyrics — picking a result delegates to [SessionController].
 */
@KoinViewModel
class SearchVM(
    private val sessionController: SessionController,
    @Provided private val repo: SongRepository,
    @Provided private val analytics: AnalyticsWrapper,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchSheetState())
    val state = _state

    /** The source of truth for what to search for; never read back from the UI state. */
    private val query = MutableStateFlow("")

    init {
        analytics.trackEvent(AnalyticsEvent.SEARCH_OPENED, emptyMap())
        observeQuery()
    }

    fun onAction(action: SearchSheetAction) {
        when (action) {
            is SearchSheetAction.OnQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                query.value = action.query
            }

            is SearchSheetAction.OnSavedCardClicked ->
                viewModelScope.launch { sessionController.loadSaved(action.id) }

            is SearchSheetAction.OnResultClicked ->
                viewModelScope.launch { sessionController.loadRemote(action.result) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            query.debounce(SEARCH_DEBOUNCE).collectLatest { searchQuery -> search(searchQuery) }
        }
    }

    private suspend fun search(searchQuery: String) {
        // Blank and too-short queries look the same to the user: nothing to show, nothing pending
        if (searchQuery.isBlank() || searchQuery.length < MIN_QUERY_LENGTH) {
            _state.update {
                it.copy(
                    savedResults = emptyList(),
                    remoteResults = emptyList(),
                    isSearching = false,
                    error = null,
                )
            }

            return
        }

        _state.update { it.copy(isSearching = true, error = null) }

        _state.update { it.copy(savedResults = localResults(searchQuery)) }

        when (val result = repo.searchGenius(searchQuery)) {
            is Result.Error ->
                _state.update { it.copy(isSearching = false, error = errorStringRes(result.error)) }

            is Result.Success ->
                _state.update {
                    it.copy(isSearching = false, remoteResults = result.data, error = null)
                }
        }
    }

    private suspend fun localResults(searchQuery: String) =
        repo.getSong(searchQuery).map { it.toSearchResult() }

    private companion object {
        val SEARCH_DEBOUNCE = 500.milliseconds
        const val MIN_QUERY_LENGTH = 3
    }
}
