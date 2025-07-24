package com.shub39.rush.search_sheet

import androidx.compose.runtime.Immutable
import com.shub39.rush.core.domain.data_classes.SearchResult

@Immutable
data class SearchSheetState (
    val visible: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val localSearchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val error: Int? = null
)