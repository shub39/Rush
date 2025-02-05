package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // updater for synced lyrics
    LaunchedEffect(state.playingSong.position) {
        coroutineScope.launch {
            var currentIndex =
                getCurrentLyricIndex(
                    state.playingSong.position,
                    state.song?.syncedLyrics!!
                )
            currentIndex -= 3
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
                val textColor by animateColorAsState(
                    targetValue = if (lyric.time <= state.playingSong.position + 1000) {
                        cardContent
                    } else {
                        cardContent.copy(0.3f)
                    },
                    label = "textColor"
                )

                Card(
                    modifier = Modifier.padding(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent,
                        contentColor = cardContent
                    ),
                    shape = MaterialTheme.shapes.small,
                    onClick = {
                        MediaListener.seek(lyric.time)
                    }
                ) {
                    if (lyric.text.isNotEmpty()) {
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