package com.shub39.rush.search_sheet

sealed interface SearchSheetAction {
    data object OnToggleSearchSheet: SearchSheetAction
    data class OnQueryChange(val query: String): SearchSheetAction
    data class OnCardClicked(val id: Long): SearchSheetAction
}