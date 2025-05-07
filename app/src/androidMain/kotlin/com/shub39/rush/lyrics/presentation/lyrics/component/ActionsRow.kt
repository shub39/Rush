package com.shub39.rush.lyrics.presentation.lyrics.component

import android.content.ClipData
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
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
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
    modifier: Modifier = Modifier
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
                painter = painterResource(R.drawable.round_palette_24),
                contentDescription = "Edit"
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "lyrics",
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
                                }
                            )
                        )
                    )
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_content_copy_24),
                contentDescription = null
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
                        painter = painterResource(id = R.drawable.round_lyrics_24),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.genius),
                        contentDescription = null
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
                    painter = painterResource(id = R.drawable.round_edit_note_24),
                    contentDescription = null
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
                        painter = painterResource(id = R.drawable.round_sync_24),
                        contentDescription = null
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
                    painter = painterResource(id = R.drawable.rush_transparent),
                    contentDescription = null,
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
                        painter = painterResource(id = R.drawable.round_share_24),
                        contentDescription = null
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