package com.shub39.rush.presentation.lyrics.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shub39.rush.R
import com.shub39.rush.domain.enums.Sources
import com.shub39.rush.presentation.copyToClipboard
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import com.shub39.rush.presentation.lyrics.TextPrefs
import com.shub39.rush.presentation.lyrics.updateSelectedLines
import com.shub39.rush.presentation.toArrangement
import com.shub39.rush.presentation.toTextAlignment
import kotlinx.coroutines.launch

@Composable
fun PlainLyrics(
    state: LyricsPageState,
    lazyListState: LazyListState,
    cardContent: Color,
    action: (LyricsPageAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope  = rememberCoroutineScope()

    val song = (state.lyricsState as? LyricsState.Loaded)?.song ?: return

    val items = if (state.source == Sources.LRCLIB) song.lyrics else song.geniusLyrics

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 64.dp),
        verticalArrangement = Arrangement.spacedBy(with(LocalDensity.current) { state.textPrefs.lineHeight.sp.toDp() / 2 }),
        state = lazyListState
    ) {
        // plain lyrics with logic
        if (!items.isNullOrEmpty()) {
            items(
                items = items,
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

                    PlainLyric(
                        entry = it.toPair(),
                        containerColor = containerColor,
                        textPrefs = state.textPrefs,
                        cardContent = cardContent,
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

                            if (!isSelected) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    )
                }
            }
        } else {
            when (state.source) {
                Sources.GENIUS -> {
                    item {
                        // start scraping
                        LaunchedEffect(Unit) {
                            action(LyricsPageAction.OnScrapeGeniusLyrics(song.id, song.sourceUrl))
                        }

                        AnimatedContent(
                            targetState = state.scraping
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (it.first) {
                                    CircularProgressIndicator(
                                        color = cardContent,
                                        modifier = Modifier.padding(16.dp)
                                    )

                                    Text(stringResource(R.string.loading_genius))
                                } else if (it.second != null) {
                                    Icon(
                                        painter = painterResource(R.drawable.warning),
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp)
                                    )

                                    Text(stringResource(it.second!!.errorCode))

                                    TextButton(
                                        onClick = {
                                            action(
                                                LyricsPageAction.OnScrapeGeniusLyrics(
                                                    song.id,
                                                    song.sourceUrl
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = cardContent,
                                            containerColor = Color.Transparent
                                        ),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(stringResource(R.string.retry))
                                    }

                                    TextButton(
                                        onClick = {
                                           scope.launch {
                                               clipboard.copyToClipboard(it.second!!.debugMessage ?: "i am dumb")
                                           }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = cardContent,
                                            containerColor = Color.Transparent
                                        ),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(stringResource(R.string.copy_error))
                                    }
                                }
                            }
                        }
                    }
                }

                Sources.LRCLIB -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.padding(10.dp))

                            Icon(
                                painter = painterResource(R.drawable.warning),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )

                            Spacer(modifier = Modifier.padding(10.dp))

                            Text(text = stringResource(R.string.no_lyrics))
                        }
                    }
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
                        scope.launch {
                            lazyListState.scrollToItem(0)
                        }
                    },
                    enabled = if (state.source == Sources.LRCLIB) song.lyrics.isNotEmpty() else !song.geniusLyrics.isNullOrEmpty()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_warm_up),
                        contentDescription = "Move To Top",
                    )
                }

                Button(
                    onClick = {
                        uriHandler.openUri(song.sourceUrl)
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
                        painter = painterResource(R.drawable.search),
                        contentDescription = "Search",
                    )
                }
            }
        }
    }
}

@Composable
fun PlainLyric(
    textPrefs: TextPrefs,
    entry: Pair<Int, String>,
    onClick: () -> Unit,
    containerColor: Color,
    cardContent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = textPrefs.lyricsAlignment.toArrangement()
    ) {
        Card(
            onClick = onClick,
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = cardContent
            )
        ) {
            Text(
                text = entry.second,
                fontSize = textPrefs.fontSize.sp,
                letterSpacing = textPrefs.letterSpacing.sp,
                lineHeight = textPrefs.lineHeight.sp,
                textAlign = textPrefs.lyricsAlignment.toTextAlignment(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}