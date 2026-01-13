package com.shub39.rush.presentation.saved

import com.shub39.rush.domain.dataclasses.Song
import com.shub39.rush.domain.enums.SortOrder

sealed interface SavedPageAction {
    data object OnToggleAutoChange : SavedPageAction
    data object OnToggleSearchSheet: SavedPageAction
    data class OnDeleteSong(val song: Song) : SavedPageAction
    data class ChangeCurrentSong(val id: Long): SavedPageAction
    data class UpdateSortOrder(val sortOrder: SortOrder): SavedPageAction
}