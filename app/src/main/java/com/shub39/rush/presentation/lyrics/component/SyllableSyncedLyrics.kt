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
package com.shub39.rush.presentation.lyrics.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.domain.dataclasses.ParsedLine
import com.shub39.rush.domain.dataclasses.ParsedWord
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.PlayingSong
import com.shub39.rush.presentation.lyrics.TextPrefs
import com.shub39.rush.presentation.lyrics.toTransformOrigin
import com.shub39.rush.presentation.toArrangement
import com.shub39.rush.presentation.toTextAlignment
import kotlin.collections.set
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyllableSyncedLyrics(
    state: LyricsPageState,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    val ttmlLyrics = (state.lyricsState as? LyricsState.Loaded)?.song?.ttmlLyrics ?: return

    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        scope.launch {
            val currentIndex =
                ttmlLyrics
                    .indexOfLast { (it.startTime * 1000).toLong() <= state.playingSong.position }
                    .coerceAtLeast(0)

            val viewportHeight =
                lazyListState.layoutInfo.viewportEndOffset -
                    lazyListState.layoutInfo.viewportStartOffset

            val itemHeight = itemHeights[currentIndex] ?: 0
            val centerOffset = (viewportHeight / 4) - (itemHeight / 2)

            lazyListState.animateScrollToItem(index = currentIndex, scrollOffset = -centerOffset)
        }
    }

    // Synced Lyrics
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 256.dp),
        verticalArrangement =
            Arrangement.spacedBy(
                with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 }
            ),
        userScrollEnabled = state.playingSong.speed == 0f,
        state = lazyListState,
    ) {
        itemsIndexed(ttmlLyrics) { index, line ->
            val currentTime = state.playingSong.position
            val lineIndex = index
            val currentPlayingIndex =
                ttmlLyrics
                    .indexOfLast { (it.startTime * 1000).toLong() <= currentTime }
                    .coerceAtLeast(0)
            val isCurrent = lineIndex == currentPlayingIndex

            val blur by
                animateDpAsState(
                    targetValue =
                        if (!state.blurSyncedLyrics) 0.dp
                        else (abs(lineIndex - currentPlayingIndex) * 3).coerceIn(0..10).dp,
                    animationSpec = tween(100),
                )

            val scale by
                animateFloatAsState(
                    targetValue = if (isCurrent) 1f else 0.8f,
                    animationSpec = tween(100),
                )

            val textColor by
                animateColorAsState(
                    targetValue =
                        when {
                            (line.startTime * 1000).toLong() <= currentTime -> cardContent
                            else -> cardContent.copy(0.3f)
                        },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "textColor",
                )

            SyllableLine(
                textPrefs = state.textPrefs,
                blur = blur,
                action = action,
                line = line,
                textColor = textColor,
                scale = scale,
                currentTime = currentTime,
                modifier =
                    Modifier.onGloballyPositioned { layoutCoordinates ->
                        val height = layoutCoordinates.size.height
                        itemHeights[index] = height
                    },
            )
        }
    }
}

@Composable
fun SyllableLine(
    textPrefs: TextPrefs,
    blur: Dp,
    action: (LyricsPageAction) -> Unit,
    line: ParsedLine,
    textColor: Color,
    scale: Float,
    currentTime: Long,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = textPrefs.lyricsAlignment.toArrangement(),
    ) {
        Box(
            modifier =
                Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = textPrefs.lyricsAlignment.toTransformOrigin()
                    }
                    .blur(radius = blur, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        action(LyricsPageAction.OnSeek((line.startTime * 1000).toLong()))
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
            contentAlignment = Alignment.Center,
        ) {
            if (line.words.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = textPrefs.lyricsAlignment.toArrangement(),
                    modifier = Modifier.padding(vertical = 6.dp),
                ) {
                    line.words.forEach { word ->
                        val wordStartTimeMs = (word.startTime * 1000).toLong()

                        // word highlighting design
                        val isHighlighted = currentTime >= wordStartTimeMs
                        val wordScale by
                            animateFloatAsState(
                                targetValue = if (isHighlighted || scale != 1f) 1f else 0.95f,
                                animationSpec = spring(),
                            )
                        val glowAlpha by
                            animateFloatAsState(targetValue = if (isHighlighted) 5f else 0f)
                        val underAlpha by
                            animateFloatAsState(targetValue = if (isHighlighted) 0.7f else 0.2f)
                        val textAlpha by
                            animateFloatAsState(targetValue = if (isHighlighted) 1f else 0f)

                        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Text(
                                text = word.text,
                                fontWeight = FontWeight.Bold,
                                color = textColor.copy(alpha = underAlpha),
                                fontSize = textPrefs.fontSize.sp,
                                letterSpacing = textPrefs.letterSpacing.sp,
                                lineHeight = textPrefs.lineHeight.sp,
                                textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                                modifier =
                                    Modifier.scale(wordScale)
                                        .blur(
                                            radius = glowAlpha.dp,
                                            edgeTreatment = BlurredEdgeTreatment.Unbounded,
                                        ),
                            )

                            Text(
                                text = word.text,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                fontSize = textPrefs.fontSize.sp,
                                letterSpacing = textPrefs.letterSpacing.sp,
                                lineHeight = textPrefs.lineHeight.sp,
                                textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                                modifier = Modifier.scale(wordScale).alpha(textAlpha),
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = line.text,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = textPrefs.fontSize.sp,
                    letterSpacing = textPrefs.letterSpacing.sp,
                    lineHeight = textPrefs.lineHeight.sp,
                    textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xAB89)
@Composable
fun SyllableSyncedLyricsPreview() {
    var position by remember { mutableLongStateOf(0L) }
    val ttmlLyrics = remember {
        listOf(
            ParsedLine(
                text = "Welcome to Rush",
                startTime = 0.0,
                words =
                    listOf(
                        ParsedWord("Welcome", 0.0, 1.0),
                        ParsedWord("to", 1.0, 1.5),
                        ParsedWord("Rush", 1.5, 2.5),
                    ),
            ),
            ParsedLine(
                text = "Per-syllable highlight",
                startTime = 3.0,
                words =
                    listOf(
                        ParsedWord("Per-", 3.0, 3.5),
                        ParsedWord("syl-", 3.5, 4.0),
                        ParsedWord("la-", 4.0, 4.5),
                        ParsedWord("ble", 4.5, 5.0),
                        ParsedWord("high-", 5.0, 5.5),
                        ParsedWord("light", 5.5, 6.0),
                    ),
            ),
            ParsedLine(
                text = "Enjoy the music",
                startTime = 7.0,
                words =
                    listOf(
                        ParsedWord("En-", 7.0, 7.5),
                        ParsedWord("joy", 7.5, 8.0),
                        ParsedWord("the", 8.0, 8.5),
                        ParsedWord("mu-", 8.5, 9.0),
                        ParsedWord("sic", 9.0, 10.0),
                    ),
            ),
        )
    }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            position = elapsed % 12000
            delay(16)
        }
    }

    val state =
        LyricsPageState(
            textPrefs = TextPrefs(fontSize = 32f),
            lyricsState =
                LyricsState.Loaded(
                    song =
                        SongUi(
                            id = 1,
                            title = "Preview Song",
                            artists = "Rush",
                            album = "Rush Demo",
                            sourceUrl = "",
                            artUrl = null,
                            lyrics = emptyList(),
                            syncedLyrics = null,
                            geniusLyrics = null,
                            ttmlLyrics = ttmlLyrics,
                        )
                ),
            playingSong =
                PlayingSong(
                    title = "Preview Song",
                    artist = "Rush",
                    position = position,
                    speed = 1f,
                ),
        )

    RushTheme(theme = Theme()) {
        Box(modifier = Modifier.fillMaxSize()) {
            SyllableSyncedLyrics(
                state = state,
                lazyListState = rememberLazyListState(),
                cardContent = Color.White,
                action = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
