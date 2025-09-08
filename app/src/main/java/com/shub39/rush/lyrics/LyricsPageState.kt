package com.shub39.rush.lyrics

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import com.shub39.rush.core.domain.Error
import com.shub39.rush.core.domain.data_classes.ExtractedColors
import com.shub39.rush.core.domain.data_classes.LrcLibSong
import com.shub39.rush.core.domain.data_classes.SongUi
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.domain.enums.Sources

data class LyricsPageState(
    // non-datastore
    val song: SongUi? = null,
    val fetching: Pair<Boolean, String> = Pair(false, ""),
    val searching: Pair<Boolean, String> = Pair(false, ""),
    val scraping: Pair<Boolean, Error?> = Pair(false, null),
    val error: Int? = null,
    val autoChange: Boolean = false,
    val playingSong: PlayingSong = PlayingSong(),
    val lrcCorrect: LrcCorrect = LrcCorrect(),
    val extractedColors: ExtractedColors = ExtractedColors(),
    val syncedAvailable: Boolean = false,
    val sync: Boolean = false,
    val lyricsCorrect: Boolean = false,
    val source: Sources = Sources.LrcLib,
    val selectedLines: Map<Int, String> = emptyMap(),

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

data class TextPrefs(
    val fontSize: Float = 28f,
    val lineHeight: Float = 32f,
    val letterSpacing: Float = 0f,
    val textAlign: TextAlign = TextAlign.Center
)

data class PlayingSong(
    val title: String = "",
    val artist: String? = null,
    val position: Long = 0,
    val speed: Float = 0f
)

data class LrcCorrect(
    val searchResults: List<LrcLibSong> = emptyList(),
    val searching: Boolean = false,
    val error: Int? = null
)