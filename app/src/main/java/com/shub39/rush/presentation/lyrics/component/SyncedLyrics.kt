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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.domain.dataclasses.Lyric
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.TextPrefs
import com.shub39.rush.presentation.lyrics.getCurrentLyricIndex
import com.shub39.rush.presentation.lyrics.getNextLyricTime
import com.shub39.rush.presentation.lyrics.toTransformOrigin
import com.shub39.rush.presentation.toArrangement
import com.shub39.rush.presentation.toTextAlignment
import kotlin.math.abs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyncedLyrics(
    state: LyricsPageState,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    val syncedLyrics = (state.lyricsState as? LyricsState.Loaded)?.song?.syncedLyrics ?: return

    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        scope.launch {
            val currentIndex =
                getCurrentLyricIndex(state.playingSong.position, syncedLyrics).coerceAtLeast(0)

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
        itemsIndexed(syncedLyrics) { index, lyric ->
            val nextTime = getNextLyricTime(index, syncedLyrics)
            val currentTime = state.playingSong.position
            val lyricIndex = syncedLyrics.indexOf(lyric)
            val currentPlayingIndex = getCurrentLyricIndex(state.playingSong.position, syncedLyrics)
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
                    animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
                )

            val underTextAlpha by
                animateFloatAsState(
                    targetValue = if (isCurrent) 0.5f else 0.2f,
                    animationSpec = tween(500, easing = LinearEasing),
                )

            val glowAlpha by
                animateFloatAsState(
                    targetValue = if (!state.blurSyncedLyrics || !isCurrent) 0f else 2f,
                    animationSpec = tween(500),
                )

            val blur by
                animateDpAsState(
                    targetValue =
                        if (!state.blurSyncedLyrics) 0.dp
                        else (abs(lyricIndex - currentPlayingIndex) * 3).dp,
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
                            lyric.time <= state.playingSong.position -> cardContent
                            else -> cardContent.copy(0.3f)
                        },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "textColor",
                )

            SyncedLyric(
                textPrefs = state.textPrefs,
                blur = blur,
                action = action,
                lyric = lyric,
                underTextAlpha = underTextAlpha,
                textColor = textColor,
                glowAlpha = glowAlpha,
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
    glowAlpha: Float,
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
                        Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            .blur(
                                radius = glowAlpha.dp,
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
                    modifier =
                        Modifier.padding(horizontal = 12.dp, vertical = 6.dp).drawWithContent {
                            val height = size.height * animatedProgress
                            clipRect(top = 0f, bottom = height) {
                                this@drawWithContent.drawContent()
                            }
                        },
                )
            } else {
                DotLoadingProgress(
                    progress = animatedProgress,
                    color = textColor,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}
