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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.domain.dataclasses.ParsedLine
import com.shub39.rush.domain.dataclasses.ParsedWord
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.presentation.RushPreviewWrapper
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.PlaybackInfo
import com.shub39.rush.presentation.lyrics.PlayingSong
import com.shub39.rush.presentation.lyrics.TextPrefs
import com.shub39.rush.presentation.lyrics.toTransformOrigin
import com.shub39.rush.presentation.theme.flexFontEmphasis
import com.shub39.rush.presentation.toAlignment
import com.shub39.rush.presentation.toArrangement
import com.shub39.rush.presentation.toTextAlignment
import kotlin.math.abs
import kotlinx.coroutines.delay

@Composable
fun SyllableSyncedLyrics(
    state: LyricsPageState,
    playbackInfo: PlaybackInfo,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUserScrolling by lazyListState.interactionSource.collectIsDraggedAsState()
    var pauseAutoScroll by remember { mutableStateOf(false) }

    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    val ttmlLyrics = (state.lyricsState as? LyricsState.Loaded)?.song?.ttmlLyrics ?: return

    val currentPlayingIndex =
        ttmlLyrics.indexOfLast { (it.startTime * 1000).toLong() <= playbackInfo.position }

    // updater for synced lyrics
    LaunchedEffect(currentPlayingIndex, pauseAutoScroll) {
        if (currentPlayingIndex >= 0 && !pauseAutoScroll) {
            val viewportHeight =
                lazyListState.layoutInfo.viewportEndOffset -
                    lazyListState.layoutInfo.viewportStartOffset
            val itemHeight = itemHeights[currentPlayingIndex] ?: 0
            val centerOffset = (viewportHeight / 4) - (itemHeight / 2)
            lazyListState.animateScrollToItem(
                index = currentPlayingIndex,
                scrollOffset = -centerOffset,
            )
        }
    }

    // scroll interaction
    LaunchedEffect(isUserScrolling) {
        if (!isUserScrolling) {
            delay(3000)
            pauseAutoScroll = false
        } else {
            pauseAutoScroll = true
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
        state = lazyListState,
    ) {
        itemsIndexed(items = ttmlLyrics, key = { it, _ -> it }) { index, line ->
            val currentTime = playbackInfo.position
            val isCurrent = index == currentPlayingIndex

            val nextTime = ttmlLyrics.getOrNull(index + 1)?.startTime
            val progress =
                nextTime?.let { nt ->
                    val startTime = line.startTime
                    val currentSecs = currentTime / 1000.0
                    val denom = (nt - startTime).toFloat()
                    if (denom <= 0f) 1f
                    else ((currentSecs - startTime).toFloat() / denom).coerceIn(0f, 1f)
                } ?: 1f

            val animatedProgress by
                animateFloatAsState(
                    targetValue = progress,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                    label = "loadingProgress",
                )

            val underTextAlpha by
                animateFloatAsState(
                    targetValue = if (isCurrent) 0.5f else 0.2f,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                    label = "underTextAlpha",
                )

            val blur by
                animateDpAsState(
                    targetValue =
                        if (!state.blurSyncedLyrics || pauseAutoScroll) 0.dp
                        else (abs(index - currentPlayingIndex) * 3).coerceIn(0..10).dp,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )

            val scale by
                animateFloatAsState(
                    targetValue = if (isCurrent) 1f else 0.8f,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                )

            val textColor by
                animateColorAsState(
                    targetValue =
                        when {
                            (line.startTime * 1000).toLong() <= currentTime -> cardContent
                            else -> cardContent.copy(0.3f)
                        },
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                    label = "textColor",
                )

            SyllableLine(
                textPrefs = state.textPrefs,
                blur = blur,
                action = action,
                line = line,
                romanizedText =
                    if (state.romanizationEnabled)
                        state.lyricsState.song.romanizedTtmlLyrics[line.startTime]
                    else null,
                textColor = textColor,
                scale = scale,
                currentTime = currentTime,
                animatedProgress = animatedProgress,
                underTextAlpha = underTextAlpha,
                isCurrent = isCurrent,
                expressiveSyllables = state.expressiveSyllables,
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
    romanizedText: String?,
    textColor: Color,
    scale: Float,
    currentTime: Long,
    animatedProgress: Float,
    underTextAlpha: Float,
    isCurrent: Boolean,
    expressiveSyllables: Boolean,
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
                Column(
                    horizontalAlignment = textPrefs.lyricsAlignment.toAlignment(),
                    modifier = Modifier.padding(vertical = 6.dp),
                ) {
                    FlowRow(horizontalArrangement = textPrefs.lyricsAlignment.toArrangement()) {
                        line.words.forEach { word ->
                            SyllableWord(
                                word = word,
                                currentTime = currentTime,
                                textPrefs = textPrefs,
                                expressiveSyllables = expressiveSyllables,
                                textColor = textColor,
                                underTextAlpha = underTextAlpha,
                                scale = scale,
                            )
                        }
                    }

                    // Romanized text below
                    if (!romanizedText.isNullOrBlank()) {
                        Text(
                            text = romanizedText,
                            fontWeight = FontWeight.Normal,
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = (textPrefs.fontSize * 0.75f).sp,
                            letterSpacing = (textPrefs.letterSpacing * 0.75f).sp,
                            lineHeight = (textPrefs.lineHeight * 0.75f).sp,
                            textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
            } else if (line.text.isNotEmpty()) {
                Text(
                    text = line.text,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) textColor else textColor.copy(alpha = underTextAlpha),
                    fontSize = textPrefs.fontSize.sp,
                    letterSpacing = textPrefs.letterSpacing.sp,
                    lineHeight = textPrefs.lineHeight.sp,
                    textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                DotLoadingProgress(
                    progress = { animatedProgress },
                    color = textColor,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun SyllableWord(
    word: ParsedWord,
    currentTime: Long,
    textPrefs: TextPrefs,
    expressiveSyllables: Boolean,
    textColor: Color,
    underTextAlpha: Float,
    scale: Float,
) {
    val fontFamily = MaterialTheme.typography.bodyLarge.fontFamily

    val wordStartTimeMs = remember(word) { (word.startTime * 1000).toLong() }
    val wordEndTimeMs = remember(word) { (word.endTime * 1000).toLong() }
    val duration = remember(word) { (wordEndTimeMs - wordStartTimeMs).coerceAtLeast(1) }

    val maxWordWeight =
        remember(duration) {
            when (duration) {
                in 0..500 -> 300
                in 501..1000 -> 500
                in 1001..1500 -> 700
                else -> 900
            }
        }

    val maxWordWidth =
        remember(duration) {
            when (duration) {
                in 0..500 -> 100f
                in 501..1000 -> 105f
                in 1001..1500 -> 110f
                in 1501..2000 -> 115f
                else -> 120f
            }
        }

    val wordProgress =
        if (currentTime >= wordEndTimeMs) 1f
        else if (currentTime < wordStartTimeMs) 0f
        else (currentTime - wordStartTimeMs).toFloat() / duration

    val animatedProgress by
        animateFloatAsState(
            targetValue = wordProgress,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
            label = "wordProgress",
        )

    val currentWeight =
        remember(animatedProgress, maxWordWeight) {
                ((200 + (animatedProgress * (maxWordWeight - 200))) / 10).toInt() * 10
            }
            .coerceIn(200, maxWordWeight)
    val currentWidth =
        remember(animatedProgress, maxWordWidth) {
                ((100f + (animatedProgress * (maxWordWidth - 100f))) * 2).toInt() / 2f
            }
            .coerceIn(100f, maxWordWidth)

    // word highlighting design
    val isHighlighted = currentTime >= wordStartTimeMs
    val wordScale by
        animateFloatAsState(
            targetValue = if (isHighlighted || scale != 1f) 1f else 0.95f,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
            label = "wordScale",
        )
    val glowAlpha by
        animateFloatAsState(
            targetValue = if (isHighlighted) 2f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
            label = "glowAlpha",
        )

    val textStyle =
        remember(currentWeight, currentWidth, textPrefs, expressiveSyllables) {
            TextStyle(
                fontSize = textPrefs.fontSize.sp,
                letterSpacing = textPrefs.letterSpacing.sp,
                lineHeight = textPrefs.lineHeight.sp,
                textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                fontWeight = FontWeight.Bold,
                fontFamily =
                    if (expressiveSyllables) {
                        flexFontEmphasis(fontWeight = currentWeight, fontWidth = currentWidth)
                    } else {
                        fontFamily
                    },
            )
        }

    Box(modifier = Modifier.padding(horizontal = 4.dp).scale(wordScale)) {
        // Ghost text for layout consistency
        Text(
            text = word.text,
            style =
                remember(maxWordWeight, maxWordWidth, textPrefs, expressiveSyllables) {
                    textStyle.copy(
                        fontFamily =
                            if (expressiveSyllables) {
                                flexFontEmphasis(
                                    fontWeight = maxWordWeight,
                                    fontWidth = maxWordWidth,
                                )
                            } else {
                                fontFamily
                            }
                    )
                },
            modifier = Modifier.alpha(0f),
        )

        // Undertext + Glow
        Text(
            text = word.text,
            style = textStyle,
            color = textColor.copy(alpha = underTextAlpha),
            modifier =
                Modifier.matchParentSize()
                    .blur(radius = glowAlpha.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
        )

        // Main Highlight Layer with Mask
        Text(
            text = word.text,
            style = textStyle,
            color = textColor,
            modifier =
                Modifier.matchParentSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        if (animatedProgress > 0f) {
                            drawContent()
                            if (animatedProgress < 1f) {
                                val feather = 16.dp.toPx()
                                val x = (size.width + feather) * animatedProgress
                                drawRect(
                                    brush =
                                        Brush.horizontalGradient(
                                            0f to Color.Black,
                                            ((x - feather) / size.width).coerceIn(0f, 1f) to
                                                Color.Black,
                                            (x / size.width).coerceIn(0f, 1f) to Color.Transparent,
                                            1f to Color.Transparent,
                                        ),
                                    blendMode = BlendMode.DstIn,
                                )
                            }
                        }
                    },
        )
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
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
                startTime = 6.0,
                words =
                    listOf(
                        ParsedWord("Per-", 6.0, 6.5),
                        ParsedWord("syl-", 6.5, 7.0),
                        ParsedWord("la-", 7.0, 7.5),
                        ParsedWord("ble", 7.5, 8.0),
                        ParsedWord("high-", 8.0, 8.5),
                        ParsedWord("light", 8.5, 9.0),
                    ),
            ),
            ParsedLine(
                text = "Enjoy the music",
                startTime = 10.0,
                words =
                    listOf(
                        ParsedWord("En-", 10.0, 10.5),
                        ParsedWord("joy", 10.5, 11.0),
                        ParsedWord("the", 11.0, 11.5),
                        ParsedWord("mu-", 11.5, 12.0),
                        ParsedWord("sic", 12.0, 13.0),
                    ),
            ),
            ParsedLine(
                text = "Slow and heavy beats",
                startTime = 14.0,
                words =
                    listOf(
                        ParsedWord("Sloooooooooooooooooooooooooooooooooow", 14.0, 16.0),
                        ParsedWord("and", 16.0, 16.5),
                        ParsedWord("hea-", 16.5, 18.0),
                        ParsedWord("vy", 18.0, 19.0),
                        ParsedWord("beats", 19.0, 21.0),
                    ),
            ),
            ParsedLine(
                text = "Fast fast fast fast",
                startTime = 22.0,
                words =
                    listOf(
                        ParsedWord("Fast", 22.0, 22.3),
                        ParsedWord("fast", 22.3, 22.6),
                        ParsedWord("fast", 22.6, 22.9),
                        ParsedWord("fast", 22.9, 23.2),
                    ),
            ),
            ParsedLine(
                text = "Varied rhythm here",
                startTime = 24.0,
                words =
                    listOf(
                        ParsedWord("Va-", 24.0, 24.2),
                        ParsedWord("ried", 24.2, 25.0),
                        ParsedWord("rhy-", 25.0, 25.1),
                        ParsedWord("thm", 25.1, 26.5),
                        ParsedWord("here", 26.5, 28.0),
                    ),
            ),
        )
    }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            position = elapsed % 30000
            delay(16)
        }
    }

    val state =
        LyricsPageState(
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
            expressiveSyllables = false,
            playingSong = PlayingSong(title = "Preview Song", artist = "Rush"),
        )

    Box(modifier = Modifier.fillMaxSize()) {
        SyllableSyncedLyrics(
            state = state,
            lazyListState = rememberLazyListState(),
            cardContent = Color.White,
            action = {},
            modifier = Modifier.fillMaxSize(),
            playbackInfo = PlaybackInfo(position = position, speed = 1f),
        )
    }
}
