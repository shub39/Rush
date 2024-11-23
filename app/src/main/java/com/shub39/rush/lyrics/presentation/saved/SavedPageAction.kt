package com.shub39.rush.lyrics.presentation.saved

import com.shub39.rush.lyrics.data.database.SongEntity

sealed interface SavedPageAction {
    object OnToggleAutoChange : SavedPageAction
    object OnToggleSearchSheet: SavedPageAction
    data class OnDeleteSong(val songEntity: SongEntity) : SavedPageAction
    data class ChangeCurrentSong(val id: Long): SavedPageAction
}