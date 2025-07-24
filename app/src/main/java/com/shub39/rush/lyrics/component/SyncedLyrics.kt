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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.ktx.lighten
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import com.shub39.rush.lyrics.getCurrentLyricIndex
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SyncedLyrics(
    state: LyricsPageState,
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier,
    textAnimatedPadding: Dp = 0.dp
) {
    val hapticFeedback = LocalHapticFeedback.current

    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        coroutineScope.launch {
            val currentIndex =
                getCurrentLyricIndex(
                    state.playingSong.position,
                    state.song?.syncedLyrics!!
                )
            lazyListState.animateScrollToItem(currentIndex.coerceAtLeast(0))
        }
    }

    // Synced Lyrics
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = state.playingSong.speed == 0f,
        state = lazyListState
    ) {
        item {
            Spacer(modifier = Modifier.padding(32.dp))
        }

        items(state.song?.syncedLyrics!!, key = { it.time }) { lyric ->
            Row(
                modifier = Modifier.fillMaxWidth(),
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

                val padding by animateDpAsState(
                    targetValue = if (isCurrent) textAnimatedPadding else 0.dp,
                    animationSpec = tween(300)
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
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .blur(
                            radius = blur,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                        .padding(vertical = padding)
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
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Music,
                            contentDescription = "Instrumental break",
                            tint = cardContent.lighten(2f).copy(alpha = glowAlpha),
                            modifier = Modifier
                                .padding(6.dp)
                                .size(state.fontSize.dp)
                                .blur(
                                    radius = 5.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                        )

                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Music,
                            contentDescription = "Instrumental break",
                            tint = textColor,
                            modifier = Modifier.size(state.fontSize.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }

    }
}