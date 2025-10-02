package com.shub39.rush.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Sync
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
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.LyricsBackground
import com.shub39.rush.core.domain.enums.Sources
import com.shub39.rush.core.presentation.copyToClipboard
import com.shub39.rush.core.presentation.glowBackground
import com.shub39.rush.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.LyricsPageState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Meteor
import kotlinx.coroutines.launch

@Composable
fun ActionsRow(
    state: LyricsPageState,
    action: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    cardBackground: Color,
    cardContent: Color,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    glowMultiplier: Float = 0f,
) {
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
    ) {
        IconButton(
            onClick = onEdit
        ) {
            Icon(
                imageVector = Icons.Rounded.Palette,
                contentDescription = "Edit",
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    clipboardManager.copyToClipboard(
                        if (state.selectedLines.isEmpty()) {
                            buildAnnotatedString {
                                append(
                                    if (state.source == Sources.LrcLib) {
                                        state.song?.lyrics?.joinToString("\n") { it.value }
                                            ?: ""
                                    } else {
                                        state.song?.geniusLyrics?.joinToString("\n") { it.value }
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
        ) {
            Icon(
                imageVector = Icons.Rounded.CopyAll,
                contentDescription = "Copy",
            )
        }

        AnimatedVisibility(visible = state.selectedLines.isEmpty()) {
            IconButton(onClick = {
                action(
                    LyricsPageAction.OnSourceChange(
                        if (state.source == Sources.LrcLib) Sources.Genius else Sources.LrcLib
                    )
                )

                action(
                    LyricsPageAction.OnSync(false)
                )
            }) {
                if (state.source == Sources.Genius) {
                    Icon(
                        imageVector = Icons.Rounded.FormatQuote,
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
            visible = state.source == Sources.LrcLib && state.selectedLines.isEmpty()
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
                    imageVector = Icons.Default.Create,
                    contentDescription = "Correct Lyrics"
                )
            }
        }

        AnimatedVisibility(
            visible = state.syncedAvailable && state.selectedLines.isEmpty() && state.source == Sources.LrcLib && notificationAccess
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
                            (state.lyricsBackground == LyricsBackground.WAVE
                                    || state.lyricsBackground == LyricsBackground.GRADIENT)
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
                        imageVector = Icons.Rounded.Sync,
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
                    if (state.autoChange &&
                        (state.lyricsBackground == LyricsBackground.WAVE ||
                                state.lyricsBackground == LyricsBackground.GRADIENT)
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
                    imageVector = FontAwesomeIcons.Solid.Meteor,
                    contentDescription = "Rush Mode",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = state.selectedLines.isNotEmpty()) {
            Row {
                IconButton(onClick = {
                    action(
                        LyricsPageAction.OnUpdateShareLines(
                            songDetails = SongDetails(
                                title = state.song?.title!!,
                                artist = state.song.artists,
                                album = state.song.album,
                                artUrl = state.song.artUrl ?: ""
                            )
                        )
                    )

                    onShare()
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }

                IconButton(
                    onClick = { action(LyricsPageAction.OnChangeSelectedLines(emptyMap())) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = null
                    )
                }
            }
        }
    }
}