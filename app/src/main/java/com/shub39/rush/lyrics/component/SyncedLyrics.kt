package com.shub39.rush.lyrics.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.core.domain.data_classes.Lyric
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.lyrics.LyricsState
import com.shub39.rush.lyrics.TextPrefs
import com.shub39.rush.lyrics.getCurrentLyricIndex
import com.shub39.rush.lyrics.getNextLyricTime
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

    val syncedLyrics = (state.lyricsState as? LyricsState.Loaded)?.song?.syncedLyrics ?: return

    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        coroutineScope.launch {
            val currentIndex =
                getCurrentLyricIndex(
                    state.playingSong.position,
                    syncedLyrics
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 }),
        userScrollEnabled = state.playingSong.speed == 0f,
        state = lazyListState
    ) {
        itemsIndexed(syncedLyrics) { index, lyric ->
            val nextTime = getNextLyricTime(index, syncedLyrics)
            val currentTime = state.playingSong.position

            val progress = nextTime?.let { nt ->
                val denom = (nt - lyric.time).toFloat()
                if (denom <= 0f) 1f
                else ((currentTime - lyric.time).toFloat() / denom).coerceIn(0f, 1f)
            } ?: 1f

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )

            val isCurrent = lyric.time <= state.playingSong.position &&
                    syncedLyrics.indexOf(lyric) == getCurrentLyricIndex(
                state.playingSong.position, syncedLyrics
            )

            val glowAlpha by animateFloatAsState(
                targetValue = if (isCurrent) 0.5f else 0.2f,
                animationSpec = tween(500, easing = LinearEasing)
            )

            val blur by animateDpAsState(
                targetValue = if (isCurrent || !state.blurSyncedLyrics) 0.dp else 2.dp,
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

            SyncedLyric(
                textPrefs = state.textPrefs,
                blur = blur,
                action = action,
                lyric = lyric,
                hapticFeedback = hapticFeedback,
                glowAlpha = glowAlpha,
                textColor = textColor,
                animatedProgress = animatedProgress,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val height = layoutCoordinates.size.height
                        itemHeights[index] = height
                    }
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
    hapticFeedback: HapticFeedback?,
    glowAlpha: Float,
    textColor: Color,
    animatedProgress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = when (textPrefs.textAlign) {
            TextAlign.Center -> Arrangement.Center
            TextAlign.End -> Arrangement.End
            else -> Arrangement.Start
        }
    ) {
        Box(
            modifier = Modifier
                .blur(
                    radius = blur,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    action(LyricsPageAction.OnSeek(lyric.time))
                    hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
                },
            contentAlignment = Alignment.Center
        ) {
            if (lyric.text.isNotEmpty()) {
                Text(
                    text = lyric.text,
                    fontWeight = FontWeight.Bold,
                    color = textColor.copy(alpha = glowAlpha),
                    fontSize = textPrefs.fontSize.sp,
                    letterSpacing = textPrefs.letterSpacing.sp,
                    lineHeight = textPrefs.lineHeight.sp,
                    textAlign = textPrefs.textAlign,
                    modifier = Modifier.padding(6.dp)
                )

                Text(
                    text = lyric.text,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = textPrefs.fontSize.sp,
                    letterSpacing = textPrefs.letterSpacing.sp,
                    lineHeight = textPrefs.lineHeight.sp,
                    textAlign = textPrefs.textAlign,
                    modifier = Modifier
                        .padding(6.dp)
                        .drawWithContent {
                            val height = size.height * animatedProgress
                            clipRect(
                                top = 0f,
                                bottom = height
                            ) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
            } else {
                DotLoadingProgress(
                    progress = animatedProgress,
                    color = textColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}