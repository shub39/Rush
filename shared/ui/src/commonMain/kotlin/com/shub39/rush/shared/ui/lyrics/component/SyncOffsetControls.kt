/*
 * Copyright (C) 2026  Karelisio
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.ui.component.BluetoothPermissionState
import com.shub39.rush.shared.ui.component.RushBottomSheet
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.displayName
import com.shub39.rush.shared.ui.formatOffset
import com.shub39.rush.shared.ui.icon
import com.shub39.rush.shared.ui.label
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.lyrics.LyricsPageAction
import com.shub39.rush.shared.ui.lyrics.LyricsPageState
import com.shub39.rush.shared.ui.lyrics.LyricsState
import com.shub39.rush.shared.ui.lyrics.OffsetTarget
import com.shub39.rush.shared.ui.theme.flexFontRounded
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

/** Offset step of the +/- buttons, in milliseconds. */
const val OFFSET_STEP_MS = 50L

/** "−50 · +150 ms · +50" row shown over the playback controls in synced mode. */
@Composable
fun SyncOffsetControl(
    offsetMs: Long,
    cardBackground: Color,
    cardContent: Color,
    onAdjust: (Long) -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sideColors =
        ButtonDefaults.textButtonColors(
            containerColor = cardContent.copy(alpha = 0.15f),
            contentColor = cardContent,
        )
    val padding = PaddingValues(horizontal = 12.dp)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { onAdjust(-OFFSET_STEP_MS) },
            colors = sideColors,
            contentPadding = padding,
            modifier = Modifier.height(36.dp),
        ) {
            Text(text = "−$OFFSET_STEP_MS")
        }

        Button(
            onClick = onOpen,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = cardContent,
                    contentColor = cardBackground,
                ),
            contentPadding = padding,
            modifier = Modifier.height(36.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.sync),
                contentDescription = stringResource(Res.string.sync_offset),
                modifier = Modifier.size(16.dp),
            )
            Text(text = formatOffset(offsetMs), modifier = Modifier.padding(start = 6.dp))
        }

        TextButton(
            onClick = { onAdjust(OFFSET_STEP_MS) },
            colors = sideColors,
            contentPadding = padding,
            modifier = Modifier.height(36.dp),
        ) {
            Text(text = "+$OFFSET_STEP_MS")
        }
    }
}

/** Details of the applied offset and where to save the adjustment made while listening. */
@Composable
fun SyncOffsetSheet(
    state: LyricsPageState,
    action: (LyricsPageAction) -> Unit,
    bluetoothPermission: BluetoothPermissionState,
    onManageDevices: () -> Unit,
) {
    val offset = state.syncOffset
    val song = (state.lyricsState as? LyricsState.Loaded)?.song
    val songOffset = song?.syncOffsetMs ?: 0L
    val device = offset.device

    RushBottomSheet(
        onDismissRequest = { action(LyricsPageAction.OnShowOffsetSheet(false)) },
        padding = 16.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.sync_offset),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = flexFontRounded()),
            )
            Text(
                text = stringResource(Res.string.offset_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ListItem(
                colors = listItemColors(),
                modifier = Modifier.clip(detachedItemShape()),
                leadingContent = {
                    if (device != null) {
                        Icon(
                            painter = painterResource(device.type.icon()),
                            contentDescription = null,
                        )
                    }
                },
                headlineContent = {
                    Text(text = device?.displayName() ?: stringResource(Res.string.no_output))
                },
                supportingContent = {
                    if (device != null) Text(text = stringResource(device.type.label()))
                },
            )

            OffsetRow(
                label = stringResource(Res.string.offset_device),
                value =
                    "${formatOffset(offset.deviceOffsetMs)} (${stringResource(offset.origin.label())})",
            )
            OffsetRow(
                label = stringResource(Res.string.offset_song),
                value = formatOffset(songOffset),
            )
            OffsetRow(
                label = stringResource(Res.string.offset_session),
                value = formatOffset(offset.sessionAdjustMs),
            )
            HorizontalDivider()
            OffsetRow(
                label = stringResource(Res.string.offset_total),
                value = formatOffset(offset.totalOffset(songOffset)),
                emphasized = true,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { action(LyricsPageAction.OnAdjustOffset(-OFFSET_STEP_MS)) }
                ) {
                    Text(text = "−$OFFSET_STEP_MS ms")
                }
                OutlinedButton(
                    onClick = { action(LyricsPageAction.OnAdjustOffset(OFFSET_STEP_MS)) }
                ) {
                    Text(text = "+$OFFSET_STEP_MS ms")
                }
            }

            if (offset.sessionAdjustMs != 0L) {
                if (device != null) {
                    Button(
                        onClick = { action(LyricsPageAction.OnSaveOffset(OffsetTarget.DEVICE)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(Res.string.save_for_device))
                    }
                    FilledTonalButton(
                        onClick = { action(LyricsPageAction.OnSaveOffset(OffsetTarget.TYPE)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text =
                                stringResource(
                                    Res.string.save_for_type,
                                    stringResource(device.type.label()),
                                )
                        )
                    }
                }
                if (song != null) {
                    FilledTonalButton(
                        onClick = { action(LyricsPageAction.OnSaveOffset(OffsetTarget.SONG)) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(Res.string.save_for_song))
                    }
                }
                TextButton(onClick = { action(LyricsPageAction.OnResetOffsetAdjustment) }) {
                    Text(text = stringResource(Res.string.cancel_adjustment))
                }
            }

            if (songOffset != 0L) {
                TextButton(onClick = { action(LyricsPageAction.OnResetSongOffset) }) {
                    Text(text = stringResource(Res.string.reset_song_offset))
                }
            }

            if (!bluetoothPermission.granted && device?.type?.isBluetooth == true) {
                ListItem(
                    colors = listItemColors(),
                    modifier = Modifier.clip(detachedItemShape()),
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.bluetooth),
                            contentDescription = null,
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(Res.string.bluetooth_permission))
                    },
                    supportingContent = {
                        Text(text = stringResource(Res.string.bluetooth_permission_desc))
                    },
                    trailingContent = {
                        TextButton(onClick = bluetoothPermission.request) {
                            Text(text = stringResource(Res.string.allow))
                        }
                    },
                )
            }

            TextButton(onClick = onManageDevices) {
                Text(text = stringResource(Res.string.manage_devices))
            }
        }
    }
}

@Composable
private fun OffsetRow(label: String, value: String, emphasized: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontWeight = if (emphasized) FontWeight.Bold else null,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, fontWeight = if (emphasized) FontWeight.Bold else null)
    }
}
