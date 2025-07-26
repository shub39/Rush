package com.shub39.rush.lyrics.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.ktx.lighten
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.lyrics.getCurrentLyricIndex
import com.shub39.rush.lyrics.getNextNonEmptyLyricTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyncedLyrics(
    state: LyricsPageState,
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        coroutineScope.launch {
            val currentIndex =
                getCurrentLyricIndex(
                    state.playingSong.position,
                    state.song?.syncedLyrics!!
                ).coerceAtLeast(0)

            val viewportHeight = lazyListState.layoutInfo.viewportEndOffset -
                    lazyListState.layoutInfo.viewportStartOffset

            val itemHeight = itemHeights[currentIndex] ?: 0
            val centerOffset = (viewportHeight / 2) - (itemHeight / 2)

            lazyListState.animateScrollToItem(
                index = currentIndex,
                scrollOffset = -centerOffset
            )
        }
    }

    // Synced Lyrics
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = state.playingSong.speed == 0f,
        state = lazyListState
    ) {
        itemsIndexed(state.song?.syncedLyrics!!) { index, lyric ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        val height = layoutCoordinates.size.height
                        itemHeights[index] = height
                    },
                horizontalArrangement = when (state.textAlign) {
                    TextAlign.Center -> Arrangement.Center
                    TextAlign.End -> Arrangement.End
                    else -> Arrangement.Start
                }
            ) {
                val isCurrent = lyric.time <= state.playingSong.position &&
                        state.song.syncedLyrics.indexOf(lyric) == getCurrentLyricIndex(
                    state.playingSong.position, state.song.syncedLyrics
                )

                val glowAlpha by animateFloatAsState(
                    targetValue = if (isCurrent) 1f else 0f,
                    animationSpec = tween(500, easing = LinearEasing)
                )

                val blur by animateDpAsState(
                    targetValue = if (isCurrent) 0.dp else 2.dp,
                    animationSpec = tween(100)
                )

                val textColor by animateColorAsState(
                    targetValue = when {
                        lyric.time <= state.playingSong.position -> cardContent
                        else -> cardContent.copy(0.3f)
                    },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    ),
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .blur(
                            radius = blur,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            action(LyricsPageAction.OnSeek(lyric.time))
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (lyric.text.isNotEmpty()) {
                        Text(
                            text = lyric.text,
                            fontWeight = FontWeight.Bold,
                            color = cardContent.lighten(2f).copy(alpha = glowAlpha),
                            fontSize = state.fontSize.sp,
                            letterSpacing = state.letterSpacing.sp,
                            lineHeight = state.lineHeight.sp,
                            textAlign = state.textAlign,
                            modifier = Modifier
                                .padding(6.dp)
                                .blur(
                                    radius = 5.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                        )

                        Text(
                            text = lyric.text,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = state.fontSize.sp,
                            letterSpacing = state.letterSpacing.sp,
                            lineHeight = state.lineHeight.sp,
                            textAlign = state.textAlign,
                            modifier = Modifier.padding(6.dp)
                        )
                    } else {
                        val nextTime = getNextNonEmptyLyricTime(index, state.song.syncedLyrics)
                        val currentTime = state.playingSong.position

                        val progress = nextTime?.let {
                            ((currentTime - lyric.time).toFloat() / (it - lyric.time).toFloat()).coerceIn(0f, 1f)
                        } ?: 0f

                        val animatedProgress by animateFloatAsState(
                            targetValue = progress
                        )

                        LinearWavyProgressIndicator(
                            progress = { animatedProgress },
                            trackColor = cardContent.copy(0.1f),
                            color = cardContent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .blur(6.dp)
                                .padding(12.dp)
                        )

                        LinearWavyProgressIndicator(
                            progress = { animatedProgress },
                            trackColor = cardContent.copy(0.1f),
                            color = cardContent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}