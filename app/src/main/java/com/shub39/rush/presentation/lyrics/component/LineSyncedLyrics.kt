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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import com.shub39.rush.domain.dataclasses.Lyric
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.PlaybackInfo
import com.shub39.rush.presentation.lyrics.PlayingSong
import com.shub39.rush.presentation.lyrics.TextPrefs
import com.shub39.rush.presentation.lyrics.getCurrentLyricIndex
import com.shub39.rush.presentation.lyrics.getNextLyricTime
import com.shub39.rush.presentation.lyrics.toTransformOrigin
import com.shub39.rush.presentation.toArrangement
import com.shub39.rush.presentation.toTextAlignment
import kotlin.math.abs
import kotlinx.coroutines.delay

@Composable
fun LineSyncedLyrics(
    state: LyricsPageState,
    playbackInfo: PlaybackInfo,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    val syncedLyrics = (state.lyricsState as? LyricsState.Loaded)?.song?.syncedLyrics ?: return

    // updater for synced lyrics
    LaunchedEffect(playbackInfo.position) {
        val currentIndex =
            getCurrentLyricIndex(playbackInfo.position, syncedLyrics).coerceAtLeast(0)

        val viewportHeight =
            lazyListState.layoutInfo.viewportEndOffset -
                lazyListState.layoutInfo.viewportStartOffset

        val itemHeight = itemHeights[currentIndex] ?: 0
        val centerOffset = (viewportHeight / 4) - (itemHeight / 2)

        lazyListState.animateScrollToItem(index = currentIndex, scrollOffset = -centerOffset)
    }

    // Synced Lyrics
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 256.dp),
        verticalArrangement =
            Arrangement.spacedBy(
                with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 }
            ),
        userScrollEnabled = playbackInfo.speed == 0f,
        state = lazyListState,
    ) {
        itemsIndexed(items = syncedLyrics, key = { it, _ -> it }) { index, lyric ->
            val nextTime = getNextLyricTime(index, syncedLyrics)
            val currentTime = playbackInfo.position
            val lyricIndex = syncedLyrics.indexOf(lyric)
            val currentPlayingIndex = getCurrentLyricIndex(playbackInfo.position, syncedLyrics)
            val isCurrent = lyricIndex == currentPlayingIndex

            val progress =
                nextTime?.let { nt ->
                    val denom = (nt - lyric.time).toFloat()
                    if (denom <= 0f) 1f
                    else ((currentTime - lyric.time).toFloat() / denom).coerceIn(0f, 1f)
                } ?: 1f

            val animatedProgress by
                animateFloatAsState(
                    targetValue = progress,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )

            val underTextAlpha by
                animateFloatAsState(
                    targetValue = if (isCurrent) 0.5f else 0.2f,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )

            val blur by
                animateDpAsState(
                    targetValue =
                        if (!state.blurSyncedLyrics) 0.dp
                        else (abs(lyricIndex - currentPlayingIndex) * 3).coerceIn(0..10).dp,
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
                            lyric.time <= playbackInfo.position -> cardContent
                            else -> cardContent.copy(0.3f)
                        },
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                    label = "textColor",
                )

            val glowAlpha by
                animateDpAsState(
                    targetValue = if (!state.blurSyncedLyrics || !isCurrent) 0.dp else 2.dp,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )

            SyncedLyric(
                textPrefs = state.textPrefs,
                blur = blur,
                action = action,
                lyric = lyric,
                underTextAlpha = underTextAlpha,
                glowAlpha = glowAlpha,
                textColor = textColor,
                scale = scale,
                animatedProgress = animatedProgress,
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
fun SyncedLyric(
    textPrefs: TextPrefs,
    blur: Dp,
    action: (LyricsPageAction) -> Unit,
    lyric: Lyric,
    underTextAlpha: Float,
    glowAlpha: Dp,
    textColor: Color,
    scale: Float,
    animatedProgress: Float,
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
                        action(LyricsPageAction.OnSeek(lyric.time))
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
            contentAlignment = Alignment.Center,
        ) {
            if (lyric.text.isNotEmpty()) {
                Text(
                    text = lyric.text,
                    fontWeight = FontWeight.Bold,
                    color = textColor.copy(alpha = underTextAlpha),
                    fontSize = textPrefs.fontSize.sp,
                    letterSpacing = textPrefs.letterSpacing.sp,
                    lineHeight = textPrefs.lineHeight.sp,
                    textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                    modifier =
                        Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                            .blur(
                                radius = glowAlpha,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            ),
                )

                Text(
                    text = lyric.text,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = textPrefs.fontSize.sp,
                    letterSpacing = textPrefs.letterSpacing.sp,
                    lineHeight = textPrefs.lineHeight.sp,
                    textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
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

@Preview
@Composable
fun LineSyncedLyricsPreview() {
    var position by remember { mutableLongStateOf(0L) }
    val lyrics = remember {
        listOf(
            Lyric(0, "Welcome to Rush Music Player"),
            Lyric(3000, "This is a per-word highlight animation"),
            Lyric(6000, "It looks much better than a top-down mask"),
            Lyric(9000, "Words highlight sequentially as the song plays"),
            Lyric(12000, "With smooth transitions and blurring"),
            Lyric(15000, "Enjoy your music experience"),
            Lyric(18000, ""),
            Lyric(21000, "Almost there..."),
            Lyric(24000, "Restarting preview loop..."),
        )
    }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            position = elapsed % 27000
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
                            syncedLyrics = lyrics,
                            geniusLyrics = null,
                            ttmlLyrics = null,
                        )
                ),
            playingSong = PlayingSong(title = "Preview Song", artist = "Rush"),
        )

    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            LineSyncedLyrics(
                state = state,
                lazyListState = rememberLazyListState(),
                cardContent = MaterialTheme.colorScheme.onSurface,
                action = {},
                modifier = Modifier.fillMaxSize(),
                playbackInfo = PlaybackInfo(position = position, speed = 1f),
            )
        }
    }
}
