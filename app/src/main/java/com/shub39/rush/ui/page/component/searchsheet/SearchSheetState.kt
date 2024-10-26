package com.shub39.rush.ui.page.component.searchsheet

import androidx.compose.runtime.Immutable
import com.shub39.rush.database.SearchResult

@Immutable
data class SearchSheetState (
    val searchResults: List<SearchResult> = emptyList(),
    val localSearchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val visible: Boolean = false,
)