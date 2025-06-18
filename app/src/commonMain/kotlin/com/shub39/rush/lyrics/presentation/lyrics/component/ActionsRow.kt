package com.shub39.rush.lyrics.presentation.lyrics.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.domain.data_classes.SongDetails
import com.shub39.rush.core.domain.enums.Sources
import com.shub39.rush.core.presentation.copyToClipboard
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Copy
import compose.icons.fontawesomeicons.solid.Edit
import compose.icons.fontawesomeicons.solid.Palette
import compose.icons.fontawesomeicons.solid.QuoteLeft
import compose.icons.fontawesomeicons.solid.Share
import compose.icons.fontawesomeicons.solid.SyncAlt
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.genius
import rush.app.generated.resources.rush_transparent

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
                imageVector = FontAwesomeIcons.Solid.Palette,
                contentDescription = "Edit",
                modifier = Modifier.size(20.dp)
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
                imageVector = FontAwesomeIcons.Solid.Copy,
                contentDescription = "Copy",
                modifier = Modifier.size(20.dp)
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
                        imageVector = FontAwesomeIcons.Solid.QuoteLeft,
                        contentDescription = "LrcLib",
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.genius),
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
                    imageVector = FontAwesomeIcons.Solid.Edit,
                    contentDescription = "Correct Lyrics",
                    modifier = Modifier.size(20.dp)
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
                    }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.SyncAlt,
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
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.rush_transparent),
                    contentDescription = "Rush Mode",
                    modifier = Modifier.size(28.dp)
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
                        imageVector = FontAwesomeIcons.Solid.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(20.dp)
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