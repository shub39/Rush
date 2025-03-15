package com.shub39.rush.lyrics.presentation.lyrics

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shub39.rush.core.data.ExtractedColors
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.Error
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.lyrics.domain.LrcLibSong
import com.shub39.rush.lyrics.domain.Lyric
import com.shub39.rush.lyrics.domain.Song

@Immutable
data class LyricsPageState(
    val song: SongUi? = null,
    val fetching: Pair<Boolean, String> = Pair(false, ""),
    val searching: Pair<Boolean, String> = Pair(false, ""),
    val scraping: Pair<Boolean, Error?> = Pair(false, null),
    @StringRes val error: Int? = null,
    val autoChange: Boolean = false,
    val playingSong: PlayingSong = PlayingSong(),
    val lrcCorrect: LrcCorrect = LrcCorrect(),
    val extractedColors: ExtractedColors = ExtractedColors(),
    val syncedAvailable: Boolean = false,
    val sync: Boolean = false,
    val lyricsCorrect: Boolean = false,
    val source: Sources = Sources.LrcLib,
    val selectedLines: Map<Int, String> = emptyMap(),
    val cardColors: CardColors = CardColors.MUTED,
    val hypnoticCanvas: Boolean = false,
    val maxLines: Int = 6,
    val meshSpeed: Float = 1f,
    val useExtractedColors: Boolean = true,
    val mCardBackground: Int = Color.DarkGray.toArgb(),
    val mCardContent: Int = Color.White.toArgb()
)

@Immutable
data class SongUi(
    val id: Long = 0,
    val title: String = "",
    val artists: String = "",
    val album: String? = null,
    val sourceUrl: String = "",
    val artUrl: String? = null,
    val lyrics:  List<Map.Entry<Int, String>> = emptyList(),
    val syncedLyrics: List<Lyric>? = null,
    val geniusLyrics:  List<Map.Entry<Int, String>>? = null
)

data class PlayingSong(
    val title: String = "",
    val artist: String? = null,
    val position: Long = 0,
    val speed: Float = 0f
)

@Immutable
data class LrcCorrect(
    val searchResults: List<LrcLibSong> = emptyList(),
    val searching: Boolean = false,
    @StringRes val error: Int? = null
)

fun Song.toSongUi(): SongUi {
    return SongUi(
        id = id,
        title = title,
        artists = artists,
        album = album,
        sourceUrl = sourceUrl,
        artUrl = artUrl,
        lyrics = breakLyrics(lyrics),
        syncedLyrics = if (syncedLyrics == null) null else parseLyrics(syncedLyrics),
        geniusLyrics = if (geniusLyrics == null) null else breakLyrics(geniusLyrics)
    )
}