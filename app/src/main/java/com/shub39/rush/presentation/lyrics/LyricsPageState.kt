package com.shub39.rush.presentation.lyrics

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shub39.rush.domain.dataclasses.ExtractedColors
import com.shub39.rush.domain.dataclasses.LrcLibSong
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.domain.enums.Sources

@Stable
@Immutable
data class LyricsPageState(
    // non-datastore
    val scraping: Pair<Boolean, LyricsState.LyricsError?> = Pair(false, null),
    val lyricsState: LyricsState = LyricsState.Idle,
    val autoChange: Boolean = false,
    val playingSong: PlayingSong = PlayingSong(),
    val lrcCorrect: LrcCorrect = LrcCorrect(),
    val extractedColors: ExtractedColors = ExtractedColors(),
    val syncedAvailable: Boolean = false,
    val sync: Boolean = false,
    val lyricsCorrect: Boolean = false,
    val source: Sources = Sources.LRCLIB,
    val selectedLines: Map<Int, String> = emptyMap(),
    val searchState: SearchState = SearchState.Idle,

    // datastore
    val blurSyncedLyrics: Boolean = true,
    val textPrefs: TextPrefs = TextPrefs(),
    val cardColors: CardColors = CardColors.MUTED,
    val lyricsBackground: LyricsBackground = LyricsBackground.SOLID_COLOR,
    val maxLines: Int = 6,
    val mCardBackground: Int = Color.DarkGray.toArgb(),
    val mCardContent: Int = Color.White.toArgb(),
    val fullscreen: Boolean = false
)

sealed interface LyricsState {
    data object Idle : LyricsState
    data class Loaded(val song: SongUi) : LyricsState
    data class Fetching(val name: String) : LyricsState
    data class LyricsError(val errorCode: Int, val debugMessage : String? = null) : LyricsState
}

sealed interface SearchState {
    data object Idle : SearchState
    data class Searching(val query: String) : SearchState
    data object UserPrompt : SearchState
}

@Stable
@Immutable
data class TextPrefs(
    val fontSize: Float = 28f,
    val lineHeight: Float = 32f,
    val letterSpacing: Float = 0f,
    val lyricsAlignment: LyricsAlignment = LyricsAlignment.CENTER
)

@Stable
@Immutable
data class PlayingSong(
    val title: String = "",
    val artist: String? = null,
    val position: Long = 0,
    val speed: Float = 0f
)

@Stable
@Immutable
data class LrcCorrect(
    val searchResults: List<LrcLibSong> = emptyList(),
    val searching: Boolean = false,
    val error: Int? = null
)