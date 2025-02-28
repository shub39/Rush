package com.shub39.rush.lyrics.presentation.lyrics

import android.content.Context
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.Sources

sealed interface LyricsPageAction {
    data class OnChangeSelectedLines(val lines: Map<Int, String>): LyricsPageAction
    data class OnLyricsCorrect(val show: Boolean): LyricsPageAction
    data class OnSyncAvailable(val sync: Boolean): LyricsPageAction
    data class OnToggleColorPref(val pref: Boolean): LyricsPageAction
    data class OnUpdatemBackground(val color: Int): LyricsPageAction
    data class OnUpdatemContent(val color: Int): LyricsPageAction
    data class OnHypnoticToggle(val pref: Boolean): LyricsPageAction
    data class OnVibrantToggle(val pref: Boolean): LyricsPageAction
    data class OnMeshSpeedChange(val speed: Float): LyricsPageAction
    data class OnSync(val sync: Boolean) : LyricsPageAction
    data class OnSourceChange(val source: Sources): LyricsPageAction
    data object OnToggleAutoChange: LyricsPageAction
    data class OnUpdateShareLines(val songDetails: SongDetails) :
        LyricsPageAction
    data object OnToggleSearchSheet: LyricsPageAction
    data class UpdateExtractedColors(val context: Context) : LyricsPageAction
    data class OnLrcSearch(val track: String, val artist: String) : LyricsPageAction
    data class OnUpdateSongLyrics(val id: Long, val plainLyrics: String, val syncedLyrics: String?) :
        LyricsPageAction
}