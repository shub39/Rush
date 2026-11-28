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
package com.shub39.rush.shared.ui.lyrics.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.ui.copyToClipboard
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.sortMapByKeys
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

data class ActionsState(
    val song: SongUi?,
    val selectedLines: Map<Int, String>,
    val source: Sources,
    val syncedAvailable: Boolean,
    val sync: Boolean,
    val rushEnabled: Boolean,
    val fitToScreen: Boolean,
) {
    val canCompact: Boolean
        get() = fitToScreen && !sync && selectedLines.isEmpty()
}

@Composable
fun Actions(
    state: ActionsState,
    action: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    cardBackground: Color,
    cardContent: Color,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    isCustomisationsOpened: Boolean,
    allowCompact: Boolean = false,
) {
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    val onCopy: () -> Unit = {
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
                                    state.selectedLines.sortMapByKeys().values.joinToString("\n")
                                )
                            }
                        }
                        .toString()
                )
            }
        }
    }

    val onSourceSwitch: () -> Unit = {
        action(
            LyricsPageAction.OnSourceChange(
                if (state.source == Sources.LRCLIB) Sources.GENIUS else Sources.LRCLIB
            )
        )

        action(LyricsPageAction.OnSync(false))
    }

    val onCorrectLyrics: () -> Unit = {
        action(LyricsPageAction.OnLyricsCorrect(true))
        action(LyricsPageAction.OnSync(false))
        if (state.rushEnabled) action(LyricsPageAction.OnDisableRushMode)
    }

    val onSyncToggle: () -> Unit = { action(LyricsPageAction.OnSync(!state.sync)) }
    val onFitToggle: (Boolean) -> Unit = { action(LyricsPageAction.OnFitToScreenToggle(it)) }
    val onRushToggle: () -> Unit = { action(LyricsPageAction.OnToggleRushMode) }

    val showSourceSwitch = state.selectedLines.isEmpty()
    val showCorrectLyrics = state.source == Sources.LRCLIB && state.selectedLines.isEmpty()
    val showSync =
        state.syncedAvailable &&
            state.selectedLines.isEmpty() &&
            state.source == Sources.LRCLIB &&
            notificationAccess
    val compact = allowCompact && state.canCompact

    if (compact) {
        FitToScreenButton(
            checked = state.fitToScreen,
            onCheckedChange = onFitToggle,
            cardBackground = cardBackground,
            cardContent = cardContent,
        )

        var menuExpanded by remember(state.song?.id) { mutableStateOf(false) }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    painter = painterResource(Res.drawable.more_vert),
                    contentDescription = stringResource(Res.string.more_options),
                )
            }

            val dismissMenu = { menuExpanded = false }

            DropdownMenu(expanded = menuExpanded, onDismissRequest = dismissMenu) {
                ActionMenuItem(
                    text = Res.string.customisations,
                    icon = Res.drawable.palette,
                    onDismiss = dismissMenu,
                    onClick = onEdit,
                    enabled = !isCustomisationsOpened,
                )

                ActionMenuItem(
                    text = Res.string.copy,
                    icon = Res.drawable.copy,
                    onDismiss = dismissMenu,
                    onClick = onCopy,
                )

                if (showSourceSwitch) {
                    val switchToLrcLib = state.source == Sources.GENIUS
                    ActionMenuItem(
                        text = if (switchToLrcLib) Res.string.lrclib else Res.string.genius,
                        icon = if (switchToLrcLib) Res.drawable.quote else Res.drawable.genius,
                        onDismiss = dismissMenu,
                        onClick = onSourceSwitch,
                    )
                }

                if (showCorrectLyrics) {
                    ActionMenuItem(
                        text = Res.string.correct_lyrics,
                        icon = Res.drawable.edit,
                        onDismiss = dismissMenu,
                        onClick = onCorrectLyrics,
                    )
                }

                if (showSync) {
                    ActionMenuItem(
                        text = Res.string.show_synced_lyrics,
                        icon = Res.drawable.sync,
                        onDismiss = dismissMenu,
                        onClick = onSyncToggle,
                        iconSize = 20.dp,
                    )
                }

                if (notificationAccess) {
                    ActionMenuItem(
                        text = Res.string.rush_mode,
                        icon = Res.drawable.meteor,
                        onDismiss = dismissMenu,
                        onClick = onRushToggle,
                        modifier =
                            Modifier.semantics {
                                toggleableState = ToggleableState(state.rushEnabled)
                            },
                        iconSize = 20.dp,
                        trailingIcon = if (state.rushEnabled) Res.drawable.check else null,
                    )
                }
            }
        }

        return
    }

    IconButton(onClick = onEdit, enabled = !isCustomisationsOpened) {
        Icon(painter = painterResource(Res.drawable.palette), contentDescription = "Open Palette")
    }

    IconButton(onClick = onCopy) {
        Icon(painter = painterResource(Res.drawable.copy), contentDescription = "Copy")
    }

    if (showSourceSwitch) {
        IconButton(onClick = onSourceSwitch) {
            if (state.source == Sources.GENIUS) {
                Icon(painter = painterResource(Res.drawable.quote), contentDescription = "LrcLib")
            } else {
                Icon(painter = painterResource(Res.drawable.genius), contentDescription = "Genius")
            }
        }
    }

    if (showCorrectLyrics) {
        IconButton(onClick = onCorrectLyrics) {
            Icon(
                painter = painterResource(Res.drawable.edit),
                contentDescription = "Correct Lyrics",
            )
        }
    }

    if (showSync) {
        Row {
            IconButton(
                onClick = onSyncToggle,
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
                    painter = painterResource(Res.drawable.sync),
                    contentDescription = "Synced Lyrics",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    if (!state.sync && state.selectedLines.isEmpty()) {
        FitToScreenButton(
            checked = state.fitToScreen,
            onCheckedChange = onFitToggle,
            cardBackground = cardBackground,
            cardContent = cardContent,
        )
    }

    if (notificationAccess) {
        IconButton(
            onClick = onRushToggle,
            colors =
                if (state.rushEnabled) {
                    IconButtonDefaults.iconButtonColors(
                        contentColor = cardBackground,
                        containerColor = cardContent,
                    )
                } else {
                    IconButtonDefaults.iconButtonColors()
                },
        ) {
            Icon(
                painter = painterResource(Res.drawable.meteor),
                contentDescription = "Rush Mode",
                modifier = Modifier.size(20.dp),
            )
        }
    }

    if (state.selectedLines.isNotEmpty()) {
        IconButton(onClick = { onShare() }) {
            Icon(painter = painterResource(Res.drawable.share), contentDescription = "Share")
        }
    }

    if (state.selectedLines.isNotEmpty()) {
        IconButton(onClick = { action(LyricsPageAction.OnChangeSelectedLines(emptyMap())) }) {
            Icon(
                painter = painterResource(Res.drawable.close),
                contentDescription = stringResource(Res.string.clear_selection),
            )
        }
    }
}

@Composable
private fun ActionMenuItem(
    text: StringResource,
    icon: DrawableResource,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp? = null,
    trailingIcon: DrawableResource? = null,
) {
    DropdownMenuItem(
        text = { Text(stringResource(text)) },
        onClick = {
            onDismiss()
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = if (iconSize != null) Modifier.size(iconSize) else Modifier,
            )
        },
        trailingIcon =
            trailingIcon?.let { { Icon(painter = painterResource(it), contentDescription = null) } },
    )
}

@Composable
private fun FitToScreenButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    cardBackground: Color,
    cardContent: Color,
) {
    IconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            IconButtonDefaults.iconToggleButtonColors(
                checkedContentColor = cardBackground,
                checkedContainerColor = cardContent,
            ),
    ) {
        Icon(
            painter = painterResource(Res.drawable.fit_screen),
            contentDescription = stringResource(Res.string.fit_to_screen),
        )
    }
}
