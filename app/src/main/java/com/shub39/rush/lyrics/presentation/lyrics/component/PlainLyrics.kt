package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.R
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.lyrics.updateSelectedLines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PlainLyrics(
    lazyListState: LazyListState,
    state: LyricsPageState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    coroutineScope: CoroutineScope,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val uriHandler = LocalUriHandler.current

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
        items(
            items = if (state.source == Sources.LrcLib) state.song?.lyrics!! else state.song?.geniusLyrics!!,
            key = { it.key }
        ) {
            if (it.value.isNotBlank()) {
                val isSelected = state.selectedLines.contains(it.key)
                val containerColor by animateColorAsState(
                    targetValue = when (!isSelected) {
                        true -> Color.Transparent
                        else -> cardContent.copy(alpha = 0.3f)
                    },
                    label = "container"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .padding(3.dp),
                        onClick = {
                            action(
                                LyricsPageAction.OnChangeSelectedLines(
                                    updateSelectedLines(
                                        state.selectedLines,
                                        it.key,
                                        it.value,
                                        state.maxLines
                                    )
                                )
                            )

                            isSelected != isSelected
                            if (!isSelected) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor,
                            contentColor = cardContent
                        )
                    ) {
                        Text(
                            text = it.value,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }

        // Lyrics not found from LRCLIB
        if (state.song.lyrics.isEmpty() && state.source != Sources.Genius) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.padding(10.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.round_warning_24),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Text(text = stringResource(id = R.string.no_lyrics))
                }
            }
        }

        // Bottom Actions Row
        item {
            Row(
                modifier = Modifier
                    .padding(vertical = 100.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.scrollToItem(0)
                        }
                    },
                    enabled = if (state.source == Sources.LrcLib) state.song.lyrics.isNotEmpty() else !state.song.geniusLyrics.isNullOrEmpty()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_upward_24),
                        contentDescription = null
                    )
                }

                Button(
                    onClick = {
                        uriHandler.openUri(state.song.sourceUrl)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cardContent.copy(alpha = 0.3f),
                        contentColor = cardContent
                    )
                ) {
                    Text(
                        text = stringResource(R.string.source)
                    )
                }

                IconButton(
                    onClick = { action(LyricsPageAction.OnToggleSearchSheet) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_search_24),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))
        }
    }
}