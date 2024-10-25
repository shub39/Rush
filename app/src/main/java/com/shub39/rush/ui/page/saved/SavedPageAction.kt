package com.shub39.rush.ui.page.saved

import com.shub39.rush.database.Song

sealed interface SavedPageAction {
    object OnToggleAutoChange : SavedPageAction
    object OnToggleSearchSheet: SavedPageAction
    data class OnDeleteSong(val song: Song) : SavedPageAction
    data class ChangeCurrentSong(val id: Long): SavedPageAction
}