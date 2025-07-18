package com.shub39.rush.lyrics.presentation.saved

import com.shub39.rush.core.domain.enums.SortOrder
import com.shub39.rush.lyrics.domain.Song

sealed interface SavedPageAction {
    data class OnUpdateOnBoardingDone(val done: Boolean) : SavedPageAction
    data object OnToggleAutoChange : SavedPageAction
    data object OnToggleSearchSheet: SavedPageAction
    data class OnDeleteSong(val song: Song) : SavedPageAction
    data class ChangeCurrentSong(val id: Long): SavedPageAction
    data class UpdateSortOrder(val sortOrder: SortOrder): SavedPageAction
}