package com.shub39.rush.presentation.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.enums.Sources
import com.shub39.rush.presentation.audioDependentBackgrounds
import com.shub39.rush.presentation.copyToClipboard
import com.shub39.rush.presentation.glowBackground
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import com.shub39.rush.presentation.lyrics.LyricsPageState
import com.shub39.rush.presentation.lyrics.LyricsState
import kotlinx.coroutines.launch

@Composable
fun Actions(
    state: LyricsPageState,
    action: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    cardBackground: Color,
    cardContent: Color,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    glowMultiplier: Float = 0f,
) {
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()


    IconButton(
        onClick = onEdit
    ) {
        Icon(
            painter = painterResource(R.drawable.palette),
            contentDescription = "Open Palette",
        )
    }

    IconButton(
        onClick = {
            if (state.lyricsState is LyricsState.Loaded) {
                coroutineScope.launch {
                    clipboardManager.copyToClipboard(
                        if (state.selectedLines.isEmpty()) {
                            buildAnnotatedString {
                                append(
                                    if (state.source == Sources.LRCLIB) {
                                        state.lyricsState.song.lyrics.joinToString("\n") { it.value }
                                    } else {
                                        state.lyricsState.song.geniusLyrics?.joinToString("\n") { it.value }
                                            ?: ""
                                    }
                                )
                            }
                        } else {
                            buildAnnotatedString {
                                append(
                                    state.selectedLines.toSortedMap().values.joinToString("\n")
                                )
                            }
                        }.toString()
                    )
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.copy),
            contentDescription = "Copy",
        )
    }

    AnimatedVisibility(visible = state.selectedLines.isEmpty()) {
        IconButton(onClick = {
            action(
                LyricsPageAction.OnSourceChange(
                    if (state.source == Sources.LRCLIB) Sources.GENIUS else Sources.LRCLIB
                )
            )

            action(
                LyricsPageAction.OnSync(false)
            )
        }) {
            if (state.source == Sources.GENIUS) {
                Icon(
                    painter = painterResource(R.drawable.quote),
                    contentDescription = "LrcLib",
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.genius),
                    contentDescription = "Genius"
                )
            }
        }
    }

    AnimatedVisibility(
        visible = state.source == Sources.LRCLIB && state.selectedLines.isEmpty()
    ) {
        IconButton(
            onClick = {
                action(
                    LyricsPageAction.OnLyricsCorrect(true)
                )
                action(
                    LyricsPageAction.OnSync(false)
                )
                if (state.autoChange) action(
                    LyricsPageAction.OnToggleAutoChange
                )
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.edit),
                contentDescription = "Correct Lyrics"
            )
        }
    }

    AnimatedVisibility(
        visible = state.syncedAvailable && state.selectedLines.isEmpty() && state.source == Sources.LRCLIB && notificationAccess
    ) {
        Row {
            IconButton(
                onClick = {
                    action(
                        LyricsPageAction.OnSync(!state.sync)
                    )
                },
                colors = if (state.sync) {
                    IconButtonDefaults.iconButtonColors(
                        contentColor = cardBackground,
                        containerColor = cardContent
                    )
                } else {
                    IconButtonDefaults.iconButtonColors()
                },
                modifier = Modifier.run {
                    if (state.sync &&
                        (state.lyricsBackground in audioDependentBackgrounds)
                    ) {
                        glowBackground(
                            (12 * glowMultiplier).dp,
                            IconButtonDefaults.standardShape,
                            cardContent
                        )
                    } else {
                        this
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.sync),
                    contentDescription = "Synced Lyrics",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    AnimatedVisibility(visible = notificationAccess) {
        IconButton(
            onClick = { action(LyricsPageAction.OnToggleAutoChange) },
            colors = if (state.autoChange) {
                IconButtonDefaults.iconButtonColors(
                    contentColor = cardBackground,
                    containerColor = cardContent
                )
            } else {
                IconButtonDefaults.iconButtonColors()
            },
            modifier = Modifier.run {
                if (
                    state.autoChange &&
                    (state.lyricsBackground in audioDependentBackgrounds)
                ) {
                    glowBackground(
                        (12 * glowMultiplier).dp,
                        IconButtonDefaults.standardShape,
                        cardContent
                    )
                } else {
                    this
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.meteor),
                contentDescription = "Rush Mode",
                modifier = Modifier.size(20.dp)
            )
        }
    }

    AnimatedVisibility(visible = state.selectedLines.isNotEmpty()) {
        IconButton(
            onClick = {
                if (state.lyricsState is LyricsState.Loaded) {
                    action(
                        LyricsPageAction.OnUpdateShareLines(
                            songDetails = SongDetails(
                                title = state.lyricsState.song.title,
                                artist = state.lyricsState.song.artists,
                                album = state.lyricsState.song.album,
                                artUrl = state.lyricsState.song.artUrl ?: ""
                            )
                        )
                    )

                    onShare()
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.share),
                contentDescription = "Share"
            )
        }
    }

    AnimatedVisibility(visible = state.selectedLines.isNotEmpty()) {
        IconButton(
            onClick = { action(LyricsPageAction.OnChangeSelectedLines(emptyMap())) }
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = null
            )
        }
    }
}