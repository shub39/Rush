package com.shub39.rush.ui.page.lyrics

sealed interface LyricsPageAction {
    object OnToggleAutoChange: LyricsPageAction
    data class OnUpdateShareLines(val shareLines: Map<Int, String>) : LyricsPageAction
    object OnToggleSearchSheet: LyricsPageAction
    data class OnLrcSearch(val track: String, val artist: String) : LyricsPageAction
    data class OnUpdateSongLyrics(val id: Long, val plainLyrics: String, val syncedLyrics: String?) : LyricsPageAction
}