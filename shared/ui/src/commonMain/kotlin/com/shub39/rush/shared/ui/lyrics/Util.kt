/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.ui.lyrics

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.blend
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import com.shub39.rush.shared.core.dataclasses.Lyric
import com.shub39.rush.shared.core.dataclasses.Song
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.dataclasses.WaveColors
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.core.util.TTMLParser
import com.shub39.rush.shared.ui.component.ArtFromUrl
import com.shub39.rush.shared.ui.component.CurveVisualizer
import com.shub39.rush.shared.ui.component.GradientVisualizer
import com.shub39.rush.shared.ui.component.HypnoticVisualizer
import com.shub39.rush.shared.ui.component.WaveVisualizer
import com.shub39.rush.shared.ui.generateGradientColors

expect fun calculateGlowMultiplier(waveData: List<Byte>?): Float

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
        listOf(Lyric(0, ""))
            .plus(
                lyricsString.lines().mapNotNull { line ->
                    val parts = line.split("] ")
                    if (parts.size == 2) {
                        val time =
                            parts[0].removePrefix("[").split(":").let { (minutes, seconds) ->
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
    } catch (_: Exception) {
        null
    }
}

fun updateSelectedLines(
    selectedLines: Map<Int, String>,
    key: Int,
    value: String,
    maxSelections: Int = 6,
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

fun calculateLineProgress(currentTime: Long, startTime: Double, nextTime: Double?): Float {
    return nextTime?.let { nt ->
        val currentSecs = currentTime / 1000.0
        val denom = (nt - startTime).toFloat()
        if (denom <= 0f) 1f else ((currentSecs - startTime).toFloat() / denom).coerceIn(0f, 1f)
    } ?: 1f
}

fun getNextLyricTime(index: Int, lyrics: List<Lyric>): Long? {
    return lyrics.getOrNull(index + 1)?.time
}

@Composable
fun getHypnoticColors(state: LyricsPageState): Pair<Color, Color> {
    val hypnoticColor1 by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardBackgroundMuted)
                    CUSTOM -> Color(state.mCardBackground)
                    VIBRANT -> Color(state.extractedColors.cardBackgroundDominant)
                }.lighten(2f),
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "hypnotic color 1",
        )
    val hypnoticColor2 by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardBackgroundMuted)
                    CUSTOM -> Color(state.mCardBackground)
                    VIBRANT -> Color(state.extractedColors.cardBackgroundDominant)
                }.darken(2f),
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "hypnotic color 2",
        )
    return Pair(hypnoticColor1, hypnoticColor2)
}

@Composable
fun getCardColors(state: LyricsPageState): Pair<Color, Color> {
    val cardBackground by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardBackgroundMuted)
                    CUSTOM -> Color(state.mCardBackground)
                    VIBRANT -> Color(state.extractedColors.cardBackgroundDominant)
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "cardBackground",
        )
    val cardContent by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardContentMuted)
                    CUSTOM -> Color(state.mCardContent)
                    VIBRANT -> Color(state.extractedColors.cardContentDominant)
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "cardContent",
        )
    return Pair(cardBackground.copy(alpha = 1f), cardContent.copy(alpha = 1f))
}

@Composable
fun getWaveColors(state: LyricsPageState): WaveColors {
    val cardBackground by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED -> Color(state.extractedColors.cardBackgroundMuted)
                    VIBRANT -> Color(state.extractedColors.cardBackgroundDominant)
                    CUSTOM -> Color(state.mCardBackground)
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val cardWaveBackground by
        animateColorAsState(
            targetValue =
                when (state.cardColors) {
                    MUTED ->
                        Color(state.extractedColors.cardBackgroundMuted)
                            .blend(Color(state.extractedColors.cardBackgroundDominant), 0.25f)
                            .darken(1.5f)

                    VIBRANT ->
                        Color(state.extractedColors.cardBackgroundDominant)
                            .blend(Color(state.extractedColors.cardBackgroundMuted), 0.25f)
                            .lighten(1.5f)

                    CUSTOM -> Color(state.mCardBackground).blend(Color(state.mCardContent), 0.25f)
                },
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )

    return WaveColors(
        cardBackground = cardBackground.toArgb(),
        cardWaveBackground = cardWaveBackground.toArgb(),
    )
}

fun LyricsAlignment.toTransformOrigin(): TransformOrigin {
    return when (this) {
        START -> TransformOrigin(0f, 0.5f)
        CENTER -> TransformOrigin(0.5f, 0.5f)
        END -> TransformOrigin(1f, 0.5f)
    }
}

@Composable
fun BoxScope.ApplyLyricsBackground(
    background: LyricsBackground,
    artUrl: String?,
    cardBackground: Color,
    waveData: List<Byte>?,
    waveColors: WaveColors,
    hypnoticColor1: Color,
    hypnoticColor2: Color,
) {
    when (background) {
        ALBUM_ART -> {
            ArtFromUrl(imageUrl = artUrl, modifier = Modifier.blur(80.dp).matchParentSize())

            Box(
                modifier =
                    Modifier.matchParentSize().background(color = cardBackground.copy(alpha = 0.5f))
            )
        }

        WAVE -> {
            WaveVisualizer(
                waveData = waveData,
                colors = waveColors,
                modifier = Modifier.matchParentSize(),
            )
        }

        GRADIENT -> {
            GradientVisualizer(
                waveData = waveData,
                colors = waveColors,
                modifier = Modifier.matchParentSize(),
            )
        }

        HYPNOTIC -> {
            HypnoticVisualizer(
                modifier = Modifier.matchParentSize(),
                colors = generateGradientColors(hypnoticColor1, hypnoticColor2),
            )
        }

        SOLID_COLOR -> {
            Box(modifier = Modifier.matchParentSize().background(cardBackground))
        }

        CURVE -> {
            CurveVisualizer(
                modifier = Modifier.matchParentSize(),
                waveData = waveData,
                colors = waveColors,
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
        syncedLyrics = syncedLyrics?.let { parseLyrics(it) },
        geniusLyrics = geniusLyrics?.let { breakLyrics(it) },
        ttmlLyrics = ttmlLyrics?.let { TTMLParser.parseTTML(it) },
    )
}
