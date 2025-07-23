package com.shub39.rush.lyrics.presentation.lyrics

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.lyrics.domain.Lyric
import com.shub39.rush.lyrics.domain.Song
import com.shub39.rush.lyrics.domain.SongUi

fun breakLyrics(lyrics: String): List<Map.Entry<Int, String>> {
    if (lyrics.isEmpty()) return emptyList()
    val lines = lyrics.lines()
    val map = mutableMapOf<Int, String>()
    for (i in lines.indices) {
        map[i] = lines[i]
    }
    return map.entries.toList()
}

fun parseLyrics(lyricsString: String): List<Lyric> {
    val seenTimes = mutableSetOf<Long>()

    return lyricsString.lines().mapNotNull { line ->
        val parts = line.split("] ")
        if (parts.size == 2) {
            val time = parts[0].removePrefix("[").split(":").let { (minutes, seconds) ->
                minutes.toLong() * 60 * 1000 + (seconds.toDouble() * 1000).toLong()
            }
            if (time in seenTimes) {
                null
            } else {
                seenTimes.add(time)
                val text = parts[1]
                Lyric(time, text)
            }
        } else {
            null
        }
    }
}

fun updateSelectedLines(
    selectedLines: Map<Int, String>,
    key: Int,
    value: String,
    maxSelections: Int = 6
): Map<Int, String> {
    return if (!selectedLines.contains(key) && selectedLines.size < maxSelections) {
        selectedLines.plus(key to value)
    } else {
        selectedLines.minus(key)
    }
}

fun getCurrentLyricIndex(playbackPosition: Long, lyrics: List<Lyric>): Int {
    return if (lyrics.indexOfLast { it.time <= playbackPosition } < 0) {
        0
    } else {
        lyrics.indexOfLast { it.time <= playbackPosition }
    }
}

@Composable
fun getHypnoticColors(state: LyricsPageState): Pair<Color, Color> {
    val hypnoticColor1 by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.lighten(2f)
                else -> state.extractedColors.cardBackgroundDominant.lighten(2f)
            }
        } else {
            Color(state.mCardBackground).lighten(2f)
        },
        label = "hypnotic color 1"
    )
    val hypnoticColor2 by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.darken(2f)
                else -> state.extractedColors.cardBackgroundDominant.darken(2f)
            }
        } else {
            Color(state.mCardBackground).darken(2f)
        },
        label = "hypnotic color 2"
    )
    return Pair(hypnoticColor1, hypnoticColor2)
}

@Composable
fun getCardColors(
    state: LyricsPageState
): Pair<Color, Color> {
    val cardBackground by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted
                else -> state.extractedColors.cardBackgroundDominant
            }
        } else {
            Color(state.mCardBackground)
        },
        label = "cardBackground"
    )
    val cardContent by animateColorAsState(
        targetValue = if (state.useExtractedColors) {
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardContentMuted
                else -> state.extractedColors.cardContentDominant
            }
        } else {
            Color(state.mCardContent)
        },
        label = "cardContent"
    )
    return Pair(cardBackground, cardContent)
}

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