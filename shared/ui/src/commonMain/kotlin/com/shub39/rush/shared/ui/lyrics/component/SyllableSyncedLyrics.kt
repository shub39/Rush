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
package com.shub39.rush.shared.ui.lyrics.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.shared.core.dataclasses.ParsedLine
import com.shub39.rush.shared.core.dataclasses.ParsedWord
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.PlaybackInfo
import com.shub39.rush.shared.ui.lyrics.PlayingSong
import com.shub39.rush.shared.ui.lyrics.TextPrefs
import com.shub39.rush.shared.ui.lyrics.calculateLineProgress
import com.shub39.rush.shared.ui.lyrics.toTransformOrigin
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.toAlignment
import com.shub39.rush.shared.ui.toArrangement
import com.shub39.rush.shared.ui.toTextAlignment
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
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
    val song = (state.lyricsState as? LyricsState.Loaded)?.song ?: return
    val ttmlLyrics = song.ttmlLyrics ?: return

    val currentPlayingIndex =
        remember(playbackInfo.position, ttmlLyrics) {
            ttmlLyrics.indexOfLast { (it.startTime * 1000).toLong() <= playbackInfo.position }
        }

    val romanizedTtml =
        remember(state.romanizationEnabled, song) {
            if (state.romanizationEnabled) song.romanizedTtmlLyrics else emptyMap()
        }

    BaseSyncedLyrics(
        state = state,
        playbackInfo = playbackInfo,
        lazyListState = lazyListState,
        items = ttmlLyrics,
        currentPlayingIndex = currentPlayingIndex,
        itemKey = { _, line -> line.startTime },
        modifier = modifier,
    ) { index, line, blur ->
        val currentTime = playbackInfo.position
        val isCurrent = index == currentPlayingIndex

        val nextTime = ttmlLyrics.getOrNull(index + 1)?.startTime
        val progress =
            calculateLineProgress(
                currentTime = currentTime,
                startTime = line.startTime,
                nextTime = nextTime,
            )

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

        val scale by
            animateFloatAsState(
                targetValue = if (isCurrent) 1f else 0.8f,
                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                label = "scale",
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
            romanizedText = romanizedTtml[line.startTime],
            textColor = textColor,
            scale = scale,
            currentTime = currentTime,
            animatedProgress = animatedProgress,
            underTextAlpha = underTextAlpha,
            isCurrent = isCurrent,
            expressiveSyllables = state.expressiveSyllables,
        )
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
        remember(currentTime, wordStartTimeMs, wordEndTimeMs) {
            when {
                currentTime < wordStartTimeMs -> 0f
                currentTime > wordEndTimeMs -> 1f
                else -> (currentTime - wordStartTimeMs).toFloat() / duration
            }
        }

    val animatedProgress by
        animateFloatAsState(
            targetValue = wordProgress,
            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
            label = "wordProgress",
        )

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

    val baseTextStyle =
        MaterialTheme.typography.bodyLarge.copy(
            fontSize = textPrefs.fontSize.sp,
            letterSpacing = textPrefs.letterSpacing.sp,
            lineHeight = textPrefs.lineHeight.sp,
            textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
        )
    val font1 = flexFontEmphasis(fontWeight = 300, fontWidth = 100f)
    val textStyle1 =
        remember(baseTextStyle, expressiveSyllables) {
            if (expressiveSyllables) {
                baseTextStyle.copy(fontFamily = font1)
            } else baseTextStyle
        }
    val font2 = flexFontEmphasis(fontWeight = maxWordWeight, fontWidth = maxWordWidth)
    val textStyle2 =
        remember(textStyle1, maxWordWidth, maxWordWeight, expressiveSyllables) {
            if (expressiveSyllables) {
                textStyle1.copy(fontFamily = font2)
            } else textStyle1
        }

    Box(
        modifier =
            Modifier.padding(horizontal = 4.dp).graphicsLayer {
                scaleX = wordScale
                scaleY = wordScale
            }
    ) {
        // skeleton
        Text(text = word.text, style = textStyle2, modifier = Modifier.alpha(0f))

        Box(
            modifier =
                Modifier.matchParentSize()
                    .blur(radius = glowAlpha.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        if (animatedProgress > 0f) {
                            val feather = 16.dp.toPx()
                            val x = (size.width + feather) * animatedProgress
                            drawRect(
                                brush =
                                    Brush.horizontalGradient(
                                        0f to Color.Transparent,
                                        ((x - feather) / size.width).coerceIn(0f, 1f) to
                                            Color.Transparent,
                                        (x / size.width).coerceIn(0f, 1f) to Color.Black,
                                        1f to Color.Black,
                                    ),
                                blendMode = BlendMode.DstIn,
                            )
                        }
                    }
        ) {
            Text(
                text = word.text,
                style = textStyle1,
                color = textColor.copy(alpha = underTextAlpha),
            )
        }

        Box(
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
                    }
        ) {
            Text(text = word.text, style = textStyle2, color = textColor)
        }
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
        val startTime = Clock.System.now().toEpochMilliseconds()
        while (true) {
            val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
            position = elapsed % 30000
            delay(16.milliseconds)
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
            expressiveSyllables = true,
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
