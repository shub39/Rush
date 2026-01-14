package com.shub39.rush.presentation.searchsheet

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.domain.dataclasses.SearchResult

@Stable
@Immutable
data class SearchSheetState (
    val visible: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val localSearchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val error: Int? = null
)