package com.shub39.rush.lyrics

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import com.shub39.rush.core.domain.WaveColors
import com.shub39.rush.core.domain.data_classes.Lyric
import com.shub39.rush.core.domain.data_classes.Song
import com.shub39.rush.core.domain.data_classes.SongUi
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.presentation.ArtFromUrl
import com.shub39.rush.core.presentation.HypnoticVisualizer
import com.shub39.rush.core.presentation.generateGradientColors
import com.shub39.rush.lyrics.component.CurveVisualizer
import com.shub39.rush.lyrics.component.GradientVisualizer
import com.shub39.rush.lyrics.component.WaveVisualizer
import io.gitlab.bpavuk.viz.VisualizerData

fun breakLyrics(lyrics: String): List<Map.Entry<Int, String>> {
    if (lyrics.isEmpty()) return emptyList()
    val lines = lyrics.lines()
    val map = mutableMapOf<Int, String>()
    for (i in lines.indices) {
        map[i] = lines[i]
    }
    return map.entries.toList()
}

fun parseLyrics(lyricsString: String): List<Lyric>? {
    val seenTimes = mutableSetOf<Long>()

    return try {
        listOf(Lyric(0, "")).plus(
            lyricsString.lines().mapNotNull { line ->
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
        )
    } catch (e: Exception) {
        Log.wtf("LyricsPage", e)
        null
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

fun getNextLyricTime(index: Int, lyrics: List<Lyric>): Long? {
    return lyrics.getOrNull(index + 1)?.time
}

@Composable
fun getHypnoticColors(state: LyricsPageState): Pair<Color, Color> {
    val hypnoticColor1 by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.lighten(2f)
            CardColors.CUSTOM -> Color(state.mCardBackground).lighten(2f)
            CardColors.VIBRANT -> state.extractedColors.cardBackgroundDominant.lighten(2f)
        },
        label = "hypnotic color 1"
    )
    val hypnoticColor2 by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.darken(2f)
            CardColors.CUSTOM -> Color(state.mCardBackground).darken(2f)
            CardColors.VIBRANT -> state.extractedColors.cardBackgroundDominant.darken(2f)
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
        targetValue =
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardBackgroundMuted
                CardColors.CUSTOM -> Color(state.mCardBackground)
                CardColors.VIBRANT -> state.extractedColors.cardBackgroundDominant
            },
        label = "cardBackground"
    )
    val cardContent by animateColorAsState(
        targetValue =
            when (state.cardColors) {
                CardColors.MUTED -> state.extractedColors.cardContentMuted
                CardColors.CUSTOM -> Color(state.mCardContent)
                CardColors.VIBRANT -> state.extractedColors.cardContentDominant
            },
        label = "cardContent"
    )
    return Pair(cardBackground.copy(alpha = 1f), cardContent.copy(alpha = 1f))
}

@Composable
fun getWaveColors(
    state: LyricsPageState
): WaveColors {
    val cardBackground by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> state.extractedColors.cardBackgroundMuted
            CardColors.VIBRANT -> state.extractedColors.cardBackgroundDominant
            CardColors.CUSTOM -> Color(state.mCardBackground)
        }
    )
    val cardWaveBackground by animateColorAsState(
        targetValue = when (state.cardColors) {
            CardColors.MUTED -> state.extractedColors.cardBackgroundMuted.blend(state.extractedColors.cardBackgroundDominant, 0.25f).darken(1.5f)
            CardColors.VIBRANT -> state.extractedColors.cardBackgroundDominant.blend(state.extractedColors.cardBackgroundMuted, 0.25f).lighten(1.5f)
            CardColors.CUSTOM -> Color(state.mCardBackground).blend(Color(state.mCardContent), 0.25f)
        }
    )

    return WaveColors(
        cardBackground, cardWaveBackground
    )
}

@Composable
fun BoxWithConstraintsScope.ApplyLyricsBackground(
    background: LyricsBackground,
    artUrl: String?,
    cardBackground: Color,
    waveData: VisualizerData?,
    waveColors: WaveColors,
    hypnoticColor1: Color,
    hypnoticColor2: Color
) {
    when (background) {
        LyricsBackground.ALBUM_ART -> {
            ArtFromUrl(
                imageUrl = artUrl,
                modifier = Modifier
                    .blur(80.dp)
                    .matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = cardBackground.copy(alpha = 0.5f)
                    )
            )
        }

        LyricsBackground.WAVE -> {
            WaveVisualizer(
                waveData = waveData,
                colors = waveColors,
                modifier = Modifier
                    .matchParentSize()
            )
        }

        LyricsBackground.GRADIENT -> {
            GradientVisualizer(
                waveData = waveData,
                colors = waveColors,
                modifier = Modifier.matchParentSize()
            )
        }

        LyricsBackground.HYPNOTIC -> {
            HypnoticVisualizer(
                modifier = Modifier.matchParentSize(),
                colors = generateGradientColors(hypnoticColor1, hypnoticColor2)
            )
        }

        LyricsBackground.SOLID_COLOR -> {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(cardBackground)
            )
        }

        LyricsBackground.CURVE -> {
            CurveVisualizer(
                modifier = Modifier.matchParentSize(),
                waveData = waveData,
                colors = waveColors
            )
        }
    }
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