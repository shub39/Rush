package com.shub39.rush.lyrics

import androidx.compose.ui.text.style.TextAlign
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.domain.enums.Sources

sealed interface LyricsPageAction {
    data class OnBlurSyncedChange(val pref: Boolean): LyricsPageAction
    data class OnChangeLyricsBackground(val background: LyricsBackground): LyricsPageAction
    data class OnMaxLinesChange(val lines: Int): LyricsPageAction
    data class OnFullscreenChange(val pref: Boolean): LyricsPageAction
    data object OnCustomisationReset: LyricsPageAction
    data class OnLetterSpacingChange(val spacing: Float): LyricsPageAction
    data class OnLineHeightChange(val height: Float): LyricsPageAction
    data class OnFontSizeChange(val size: Float): LyricsPageAction
    data class OnAlignmentChange(val alignment: TextAlign): LyricsPageAction
    data class OnChangeSelectedLines(val lines: Map<Int, String>): LyricsPageAction
    data class OnScrapeGeniusLyrics(val id: Long, val url: String): LyricsPageAction
    data class OnLyricsCorrect(val show: Boolean): LyricsPageAction
    data class OnSyncAvailable(val sync: Boolean): LyricsPageAction
    data class OnToggleColorPref(val pref: Boolean): LyricsPageAction
    data class OnUpdatemBackground(val color: Int): LyricsPageAction
    data class OnUpdatemContent(val color: Int): LyricsPageAction
    data class OnUpdateColorType(val color: CardColors): LyricsPageAction
    data class OnMeshSpeedChange(val speed: Float): LyricsPageAction
    data class OnSync(val sync: Boolean) : LyricsPageAction
    data class OnSourceChange(val source: Sources): LyricsPageAction
    data object OnToggleAutoChange: LyricsPageAction
    data object OnPauseOrResume: LyricsPageAction
    data class OnSeek(val position: Long): LyricsPageAction
    data class OnUpdateShareLines(val songDetails: SongDetails) :
        LyricsPageAction
    data object OnToggleSearchSheet: LyricsPageAction
    data class UpdateExtractedColors(val url: String) : LyricsPageAction
    data class OnLrcSearch(val track: String, val artist: String) : LyricsPageAction
    data class OnUpdateSongLyrics(val id: Long, val plainLyrics: String, val syncedLyrics: String?) :
        LyricsPageAction
}