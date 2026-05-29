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
package com.shub39.rush.shared.ui.lyrics.component.customisation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.shared.core.dataclasses.Lyric
import com.shub39.rush.shared.core.dataclasses.ParsedLine
import com.shub39.rush.shared.core.dataclasses.ParsedWord
import com.shub39.rush.shared.core.dataclasses.WaveColors
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.ui.lyrics.ApplyLyricsBackground
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.component.PlainLyric
import com.shub39.rush.shared.ui.lyrics.component.SyllableLine
import com.shub39.rush.shared.ui.lyrics.component.SyncedLyric
import kotlin.math.abs
import kotlin.time.Clock
import kotlinx.coroutines.delay

@Composable
fun LyricsCustomisationPreview(
    state: LyricsPageState,
    isShowingSynced: Boolean,
    cardBackground: Color,
    cardContent: Color,
    waveData: List<Byte>?,
    hypnoticColor1: Color,
    hypnoticColor2: Color,
    waveColors: WaveColors,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    var currentTime by remember { mutableLongStateOf(0L) }

    val lyrics = remember {
        listOf(
            Lyric(1000, "This is an example of Line-Synced Lyrics"),
            Lyric(3000, "This is an example of Syllable-Synced Lyrics"),
            Lyric(6500, ""),
        )
    }

    val syllableLine = remember {
        ParsedLine(
            text = "This is an example of Syllable-Synced Lyrics",
            startTime = 3.0,
            words =
                listOf(
                    ParsedWord("This", 3.0, 3.4),
                    ParsedWord("is", 3.4, 3.6),
                    ParsedWord("an", 3.6, 3.8),
                    ParsedWord("example", 3.8, 4.5),
                    ParsedWord("of", 4.5, 4.7),
                    ParsedWord("Syllable", 4.7, 5.2),
                    ParsedWord("Synced", 5.2, 6.0),
                    ParsedWord("Lyrics", 6.0, 6.5),
                ),
        )
    }

    LaunchedEffect(isShowingSynced) {
        if (isShowingSynced) {
            val startTime = Clock.System.now().toEpochMilliseconds()
            while (true) {
                currentTime = (Clock.System.now().toEpochMilliseconds() - startTime) % 13000
                delay(16)
            }
        }
    }

    val currentPlayingIndex =
        remember(currentTime) { lyrics.indexOfLast { it.time <= currentTime }.coerceAtLeast(0) }

    LaunchedEffect(currentPlayingIndex) {
        if (isShowingSynced) {
            lazyListState.animateScrollToItem(index = currentPlayingIndex, scrollOffset = -100)
        }
    }

    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(containerColor = cardBackground, contentColor = cardContent),
        shape = RoundedCornerShape(16.dp),
    ) {
        BoxWithConstraints {
            ApplyLyricsBackground(
                background = state.lyricsBackground,
                artUrl = (state.lyricsState as? LyricsState.Loaded)?.song?.artUrl,
                cardBackground = cardBackground,
                waveData = waveData,
                waveColors = waveColors,
                hypnoticColor1 = hypnoticColor1,
                hypnoticColor2 = hypnoticColor2,
            )

            AnimatedContent(
                targetState = isShowingSynced,
                modifier = Modifier.fillMaxWidth(),
                label = "SyncedContent",
            ) { showingSynced ->
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 }
                        ),
                ) {
                    if (showingSynced) {
                        LazyColumn(
                            state = lazyListState,
                            contentPadding = PaddingValues(vertical = 16.dp),
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    with(LocalDensity.current) {
                                        state.textPrefs.lineHeight.sp.toDp() / 2
                                    }
                                ),
                            userScrollEnabled = false,
                        ) {
                            items(lyrics.size) { index ->
                                val isCurrent = index == currentPlayingIndex
                                val lyric = lyrics[index]

                                val blur by
                                    animateDpAsState(
                                        targetValue =
                                            if (!state.blurSyncedLyrics) 0.dp
                                            else
                                                (abs(index - currentPlayingIndex) * 3)
                                                    .coerceIn(0..10)
                                                    .dp,
                                        animationSpec =
                                            MaterialTheme.motionScheme.fastEffectsSpec(),
                                        label = "blur",
                                    )

                                val scale by
                                    animateFloatAsState(
                                        targetValue = if (isCurrent) 1f else 0.8f,
                                        animationSpec =
                                            MaterialTheme.motionScheme.fastSpatialSpec(),
                                        label = "scale",
                                    )

                                if (index == 1) {
                                    // Syllable Line
                                    SyllableLine(
                                        textPrefs = state.textPrefs,
                                        blur = blur,
                                        action = {},
                                        underTextAlpha = if (isCurrent) 0.5f else 0.2f,
                                        textColor = cardContent,
                                        animatedProgress = 1f,
                                        scale = scale,
                                        expressiveSyllables = state.expressiveSyllables,
                                        currentTime = currentTime,
                                        isCurrent = isCurrent,
                                        line = syllableLine,
                                        romanizedText = null,
                                    )
                                } else {
                                    // Regular Line Synced
                                    val progress =
                                        remember(currentTime) {
                                            val nextTime =
                                                lyrics.getOrNull(index + 1)?.time ?: 13000L
                                            val denom = (nextTime - lyric.time).toFloat()
                                            if (denom <= 0f) 1f
                                            else
                                                ((currentTime - lyric.time).toFloat() / denom)
                                                    .coerceIn(0f, 1f)
                                        }

                                    val animatedProgress by
                                        animateFloatAsState(
                                            targetValue = progress,
                                            animationSpec =
                                                MaterialTheme.motionScheme.fastEffectsSpec(),
                                            label = "progress",
                                        )

                                    SyncedLyric(
                                        textPrefs = state.textPrefs,
                                        blur = blur,
                                        action = {},
                                        lyric = lyric,
                                        underTextAlpha = if (isCurrent) 0.5f else 0.2f,
                                        textColor = cardContent,
                                        animatedProgress = animatedProgress,
                                        scale = scale,
                                        glowAlpha = 0.dp,
                                        romanizedText = null,
                                    )
                                }
                            }
                        }
                    } else {
                        PlainLyric(
                            entry =
                                1 to
                                    "This is a very very long text depicting how lyrics should appear based on these settings",
                            romanizedText = null,
                            textPrefs = state.textPrefs,
                            onClick = {},
                            containerColor =
                                if (state.lyricsBackground != LyricsBackground.SOLID_COLOR) {
                                    Color.Transparent
                                } else cardBackground,
                            cardContent = cardContent,
                            modifier = Modifier.padding(vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}
