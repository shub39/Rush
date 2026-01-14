package com.shub39.rush.presentation.searchsheet

sealed interface SearchSheetAction {
    data object OnToggleSearchSheet: SearchSheetAction
    data class OnQueryChange(val query: String): SearchSheetAction
    data class OnCardClicked(val id: Long): SearchSheetAction
}