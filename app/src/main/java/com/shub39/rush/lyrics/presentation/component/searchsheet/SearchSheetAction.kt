package com.shub39.rush.lyrics.presentation.component.searchsheet

sealed interface SearchSheetAction {
    object OnToggleSearchSheet: SearchSheetAction
    data class OnSearch(val query: String): SearchSheetAction
    data class OnCardClicked(val id: Long): SearchSheetAction
}