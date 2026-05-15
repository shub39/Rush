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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.PlaybackInfo
import kotlin.math.abs
import kotlinx.coroutines.delay

@Composable
fun <T> BaseSyncedLyrics(
    state: LyricsPageState,
    playbackInfo: PlaybackInfo,
    lazyListState: LazyListState,
    items: List<T>,
    currentPlayingIndex: Int,
    modifier: Modifier = Modifier,
    itemKey: ((Int, T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(index: Int, item: T, blur: Dp) -> Unit,
) {
    val isUserScrolling by lazyListState.interactionSource.collectIsDraggedAsState()
    var pauseAutoScroll by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    val lineSpacing =
        remember(state.textPrefs.lineHeight, density) {
            with(density) { state.textPrefs.lineHeight.sp.toDp() / 2 }
        }

    // updater for synced lyrics
    LaunchedEffect(
        currentPlayingIndex,
        pauseAutoScroll,
        isUserScrolling,
        playbackInfo.speed,
        itemHeights[currentPlayingIndex],
    ) {
        if (
            currentPlayingIndex < 0 ||
                pauseAutoScroll ||
                isUserScrolling ||
                playbackInfo.speed == 0f
        ) {
            return@LaunchedEffect
        }

        val viewportHeight =
            lazyListState.layoutInfo.viewportEndOffset -
                lazyListState.layoutInfo.viewportStartOffset
        val itemHeight = itemHeights[currentPlayingIndex] ?: 0
        val centerOffset = (viewportHeight / 3) - (itemHeight / 2)
        val distance = abs(currentPlayingIndex - lazyListState.firstVisibleItemIndex)

        if (distance > 10) {
            lazyListState.scrollToItem(index = currentPlayingIndex, scrollOffset = -centerOffset)
        } else {
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

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 256.dp),
        verticalArrangement = Arrangement.spacedBy(lineSpacing),
        state = lazyListState,
    ) {
        itemsIndexed(items = items, key = itemKey) { index, item ->
            val blur by
                animateDpAsState(
                    targetValue =
                        if (!state.blurSyncedLyrics || pauseAutoScroll) 0.dp
                        else (abs(index - currentPlayingIndex) * 3).coerceIn(0..10).dp,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                    label = "blur",
                )

            BaseSyncedItemWrapper(
                index = index,
                onHeightMeasured = { itemHeights[index] = it },
                content = { itemContent(index, item, blur) },
            )
        }
    }
}

@Composable
private fun BaseSyncedItemWrapper(
    index: Int,
    onHeightMeasured: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Box(
        modifier =
            Modifier.onGloballyPositioned { layoutCoordinates ->
                onHeightMeasured(layoutCoordinates.size.height)
            }
    ) {
        content()
    }
}
