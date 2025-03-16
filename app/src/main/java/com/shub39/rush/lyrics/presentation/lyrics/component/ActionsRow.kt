package com.shub39.rush.lyrics.presentation.lyrics.component

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.lighten
import com.shub39.rush.R
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.core.presentation.ColorPickerDialog
import com.shub39.rush.core.presentation.RushDialog
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState

@Composable
fun ActionsRow(
    state: LyricsPageState,
    action: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    cardBackground: Color,
    cardContent: Color,
    onShare: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var paletteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { paletteDialog = true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_palette_24),
                    contentDescription = "palette"
                )
            }

            IconButton(
                onClick = {
                    if (state.selectedLines.isEmpty()) {
                        clipboardManager.setText(
                            buildAnnotatedString {
                                append(
                                    if (state.source == Sources.LrcLib) {
                                        state.song?.lyrics?.joinToString("\n") { it.value } ?: ""
                                    } else {
                                        state.song?.geniusLyrics?.joinToString("\n") { it.value }
                                            ?: ""
                                    }
                                )
                            }
                        )
                    } else {
                        clipboardManager.setText(
                            buildAnnotatedString {
                                append(state.selectedLines.toSortedMap().values.joinToString("\n"))
                            }
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

    if (paletteDialog) {
        RushDialog(
            onDismissRequest = { paletteDialog = false }
        ) {
            var colorPickerDialog by remember { mutableStateOf(false) }
            var editTarget by remember { mutableStateOf("content") }

            if (colorPickerDialog) {
                ColorPickerDialog(
                    initialColor = if (editTarget == "content") Color(state.mCardContent) else Color(
                        state.mCardBackground
                    ),
                    onSelect = {
                        if (editTarget == "content") {
                            action(LyricsPageAction.OnUpdatemContent(it.toArgb()))
                        } else {
                            action(LyricsPageAction.OnUpdatemBackground(it.toArgb()))
                        }
                    },
                    onDismiss = { colorPickerDialog = false }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ListItem(
                        modifier = Modifier.clip(MaterialTheme.shapes.large),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.hypnotic_canvas)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.hypnoticCanvas,
                                onCheckedChange = { action(LyricsPageAction.OnHypnoticToggle(it)) }
                            )
                        }
                    )

                    ListItem(
                        modifier = Modifier.clip(MaterialTheme.shapes.large),
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.mesh_speed)
                            )
                        },
                        supportingContent = {
                            Slider(
                                value = state.meshSpeed,
                                valueRange = 0.5f..3f,
                                onValueChange = {
                                    action(LyricsPageAction.OnMeshSpeedChange(it))
                                },
                                enabled = state.hypnoticCanvas
                            )
                        }
                    )
                }

                ListItem(
                    modifier = Modifier.clip(MaterialTheme.shapes.large),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.use_extracted_colors)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.useExtractedColors,
                            onCheckedChange = { action(LyricsPageAction.OnToggleColorPref(it)) }
                        )
                    }
                )

                ListItem(
                    modifier = Modifier.clip(MaterialTheme.shapes.large),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.vibrant_colors)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = state.cardColors == CardColors.VIBRANT,
                            onCheckedChange = { action(LyricsPageAction.OnVibrantToggle(it)) },
                            enabled = state.useExtractedColors
                        )
                    }
                )

                ListItem(
                    modifier = Modifier.clip(MaterialTheme.shapes.large),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.colors)
                        )
                    },
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = {
                                    editTarget = "content"
                                    colorPickerDialog = true
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(state.mCardContent),
                                    contentColor = Color(state.mCardContent).lighten(2f)
                                ),
                                enabled = !state.useExtractedColors
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Select Color",
                                )
                            }

                            IconButton(
                                onClick = {
                                    editTarget = "background"
                                    colorPickerDialog = true
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(state.mCardBackground),
                                    contentColor = Color(state.mCardBackground).lighten(2f)
                                ),
                                enabled = !state.useExtractedColors
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Select Color"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}