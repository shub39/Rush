package com.shub39.rush.lyrics.presentation.search_sheet

import androidx.compose.runtime.Immutable
import com.shub39.rush.lyrics.domain.SearchResult
import org.jetbrains.compose.resources.StringResource

@Immutable
data class SearchSheetState (
    val visible: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val localSearchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val error: StringResource? = null
)