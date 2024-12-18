package com.shub39.rush.lyrics.presentation.lyrics

import android.content.Context
import com.shub39.rush.core.data.SongDetails

sealed interface LyricsPageAction {
    data object OnToggleAutoChange: LyricsPageAction
    data class OnUpdateShareLines(val songDetails: SongDetails, val shareLines: Map<Int, String>) :
        LyricsPageAction
    data object OnToggleSearchSheet: LyricsPageAction
    data class UpdateExtractedColors(val context: Context) : LyricsPageAction
    data class OnLrcSearch(val track: String, val artist: String) : LyricsPageAction
    data class OnUpdateSongLyrics(val id: Long, val plainLyrics: String, val syncedLyrics: String?) :
        LyricsPageAction
}