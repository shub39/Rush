package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.ktx.lighten
import com.shub39.rush.R
import com.shub39.rush.lyrics.data.listener.MediaListener
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.lyrics.getCurrentLyricIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SyncedLyrics(
    state: LyricsPageState,
    coroutineScope: CoroutineScope,
    lazyListState: LazyListState,
    cardContent: Color
) {
    val hapticFeedback = LocalHapticFeedback.current

    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        coroutineScope.launch {
            val currentIndex =
                getCurrentLyricIndex(
                    state.playingSong.position,
                    state.song?.syncedLyrics!!
                ) - 3
            lazyListState.animateScrollToItem(currentIndex.coerceAtLeast(0))
        }
    }

    // Synced Lyrics
    LazyColumn(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .padding(
                end = 16.dp,
                start = 16.dp,
                top = 16.dp,
                bottom = 32.dp
            ),
        state = lazyListState
    ) {
        items(state.song?.syncedLyrics!!, key = { it.time }) { lyric ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val isCurrent = lyric.time <= state.playingSong.position &&
                        state.song.syncedLyrics.indexOf(lyric) == getCurrentLyricIndex(
                    state.playingSong.position, state.song.syncedLyrics
                )

                val glowAlpha by animateFloatAsState(
                    targetValue = if (isCurrent) 1f else 0f,
                    animationSpec = tween(500, easing = LinearEasing)
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
                        .padding(6.dp)
                        .clickable {
                            MediaListener.seek(lyric.time)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (lyric.text.isNotEmpty()) {
                        Text(
                            text = lyric.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = cardContent.lighten(2f).copy(alpha = glowAlpha),
                            fontSize = 19.sp,
                            modifier = Modifier
                                .padding(6.dp)
                                .blur(
                                    radius = 10.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                        )

                        Text(
                            text = lyric.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = 19.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.round_music_note_24),
                            contentDescription = null,
                            tint = cardContent.lighten(2f).copy(alpha = glowAlpha),
                            modifier = Modifier.blur(
                                radius = 10.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.round_music_note_24),
                            contentDescription = null,
                            tint = textColor
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