/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.presentation.lyrics.component

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
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.domain.enums.Sources
import com.shub39.rush.presentation.copyToClipboard
import com.shub39.rush.presentation.lyrics.LyricsPageAction
import kotlinx.coroutines.launch

data class ActionsState(
    val song: SongUi?,
    val selectedLines: Map<Int, String>,
    val source: Sources,
    val syncedAvailable: Boolean,
    val sync: Boolean,
    val autoChange: Boolean,
)

@Composable
fun Actions(
    state: ActionsState,
    action: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    cardBackground: Color,
    cardContent: Color,
    onShare: () -> Unit,
    onEdit: () -> Unit,
) {
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = onEdit) {
        Icon(painter = painterResource(R.drawable.palette), contentDescription = "Open Palette")
    }

    IconButton(
        onClick = {
            if (state.song != null) {
                coroutineScope.launch {
                    clipboardManager.copyToClipboard(
                        if (state.selectedLines.isEmpty()) {
                                buildAnnotatedString {
                                    append(
                                        if (state.source == Sources.LRCLIB) {
                                            state.song.lyrics.joinToString("\n") { it.value }
                                        } else {
                                            state.song.geniusLyrics?.joinToString("\n") { it.value }
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
                            .toString()
                    )
                }
            }
        }
    ) {
        Icon(painter = painterResource(R.drawable.copy), contentDescription = "Copy")
    }

    if (state.selectedLines.isEmpty()) {
        IconButton(
            onClick = {
                action(
                    LyricsPageAction.OnSourceChange(
                        if (state.source == Sources.LRCLIB) Sources.GENIUS else Sources.LRCLIB
                    )
                )

                action(LyricsPageAction.OnSync(false))
            }
        ) {
            if (state.source == Sources.GENIUS) {
                Icon(painter = painterResource(R.drawable.quote), contentDescription = "LrcLib")
            } else {
                Icon(painter = painterResource(R.drawable.genius), contentDescription = "Genius")
            }
        }
    }

    if (state.source == Sources.LRCLIB && state.selectedLines.isEmpty()) {
        IconButton(
            onClick = {
                action(LyricsPageAction.OnLyricsCorrect(true))
                action(LyricsPageAction.OnSync(false))
                if (state.autoChange) action(LyricsPageAction.OnToggleAutoChange)
            }
        ) {
            Icon(painter = painterResource(R.drawable.edit), contentDescription = "Correct Lyrics")
        }
    }

    if (
        state.syncedAvailable &&
            state.selectedLines.isEmpty() &&
            state.source == Sources.LRCLIB &&
            notificationAccess
    ) {
        Row {
            IconButton(
                onClick = { action(LyricsPageAction.OnSync(!state.sync)) },
                colors =
                    if (state.sync) {
                        IconButtonDefaults.iconButtonColors(
                            contentColor = cardBackground,
                            containerColor = cardContent,
                        )
                    } else {
                        IconButtonDefaults.iconButtonColors()
                    },
            ) {
                Icon(
                    painter = painterResource(R.drawable.sync),
                    contentDescription = "Synced Lyrics",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    if (notificationAccess) {
        IconButton(
            onClick = { action(LyricsPageAction.OnToggleAutoChange) },
            colors =
                if (state.autoChange) {
                    IconButtonDefaults.iconButtonColors(
                        contentColor = cardBackground,
                        containerColor = cardContent,
                    )
                } else {
                    IconButtonDefaults.iconButtonColors()
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.meteor),
                contentDescription = "Rush Mode",
                modifier = Modifier.size(20.dp),
            )
        }
    }

    if (state.selectedLines.isNotEmpty()) {
        IconButton(
            onClick = {
                if (state.song != null) {
                    action(
                        LyricsPageAction.OnUpdateShareLines(
                            songDetails =
                                SongDetails(
                                    title = state.song.title,
                                    artist = state.song.artists,
                                    album = state.song.album,
                                    artUrl = state.song.artUrl ?: "",
                                )
                        )
                    )

                    onShare()
                }
            }
        ) {
            Icon(painter = painterResource(R.drawable.share), contentDescription = "Share")
        }
    }

    if (state.selectedLines.isNotEmpty()) {
        IconButton(onClick = { action(LyricsPageAction.OnChangeSelectedLines(emptyMap())) }) {
            Icon(painter = painterResource(R.drawable.close), contentDescription = null)
        }
    }
}
