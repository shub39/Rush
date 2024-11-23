package com.shub39.rush.lyrics.presentation.component.searchsheet

import androidx.compose.runtime.Immutable
import com.shub39.rush.lyrics.domain.SearchResult

@Immutable
data class SearchSheetState (
    val searchResults: List<SearchResult> = emptyList(),
    val localSearchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val visible: Boolean = false,
)