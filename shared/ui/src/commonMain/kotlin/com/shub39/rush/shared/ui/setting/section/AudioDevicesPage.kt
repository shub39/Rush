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
package com.shub39.rush.shared.ui.setting.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.dataclasses.AudioOutputDevice
import com.shub39.rush.shared.core.dataclasses.DeviceOffset
import com.shub39.rush.shared.core.dataclasses.SyncOffsetState
import com.shub39.rush.shared.core.enums.AudioDeviceType
import com.shub39.rush.shared.ui.RushPreviewWrapper
import com.shub39.rush.shared.ui.component.PageFill
import com.shub39.rush.shared.ui.component.RushDialog
import com.shub39.rush.shared.ui.component.rememberBluetoothPermission
import com.shub39.rush.shared.ui.detachedItemShape
import com.shub39.rush.shared.ui.displayName
import com.shub39.rush.shared.ui.formatOffset
import com.shub39.rush.shared.ui.icon
import com.shub39.rush.shared.ui.label
import com.shub39.rush.shared.ui.listItemColors
import com.shub39.rush.shared.ui.lyrics.component.OFFSET_STEP_MS
import com.shub39.rush.shared.ui.segmentedListItemShapes
import com.shub39.rush.shared.ui.setting.SettingsPageAction
import com.shub39.rush.shared.ui.setting.SettingsPageState
import com.shub39.rush.shared.ui.theme.flexFontEmphasis
import com.shub39.rush.shared.ui.theme.flexFontRounded
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

private sealed interface OffsetEditTarget {
    data class Device(val device: DeviceOffset) : OffsetEditTarget

    data class Type(val type: AudioDeviceType) : OffsetEditTarget
}

/** Known audio outputs and device types with their lyrics sync offset. */
@Composable
fun AudioDevicesPage(
    state: SettingsPageState,
    onAction: (SettingsPageAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) =
    PageFill(modifier = modifier) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val offset = state.syncOffset
        val bluetoothPermission = rememberBluetoothPermission {
            onAction(SettingsPageAction.OnBluetoothPermissionChanged)
        }
        var editTarget: OffsetEditTarget? by remember { mutableStateOf(null) }

        Scaffold(
            modifier =
                Modifier.widthIn(max = 700.dp).nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumFlexibleTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = stringResource(Res.string.audio_devices),
                            fontFamily = flexFontEmphasis(),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back),
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            scrolledContainerColor = MaterialTheme.colorScheme.surface
                        ),
                )
            },
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding =
                    PaddingValues(
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 60.dp,
                        start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                        end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                    ),
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.audio_devices_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 16.dp),
                    )
                }

                item { SectionTitle(text = stringResource(Res.string.current_output)) }

                item {
                    val device = offset.device
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
                            Text(
                                text = device?.displayName() ?: stringResource(Res.string.no_output)
                            )
                        },
                        supportingContent = {
                            if (device != null) {
                                Text(
                                    text =
                                        "${stringResource(device.type.label())} · " +
                                            "${formatOffset(offset.deviceOffsetMs)} " +
                                            "(${stringResource(offset.origin.label())})"
                                )
                            }
                        },
                    )
                }

                if (offset.supported && !bluetoothPermission.granted) {
                    item {
                        ListItem(
                            colors = listItemColors(),
                            modifier = Modifier.padding(top = 8.dp).clip(detachedItemShape()),
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
                }

                if (offset.knownDevices.isNotEmpty()) {
                    item { SectionTitle(text = stringResource(Res.string.known_devices)) }

                    itemsIndexed(items = offset.knownDevices, key = { _, device -> device.key }) {
                        index,
                        device ->
                        val typeOffset = offset.typeOffsets[device.type]
                        ListItem(
                            onClick = { editTarget = OffsetEditTarget.Device(device) },
                            shapes = segmentedListItemShapes(index, offset.knownDevices.size),
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(device.type.icon()),
                                    contentDescription = null,
                                )
                            },
                            supportingContent = {
                                val value =
                                    device.offsetMs?.let(::formatOffset)
                                        ?: typeOffset?.let {
                                            stringResource(
                                                Res.string.offset_default_type,
                                                formatOffset(it),
                                            )
                                        }
                                        ?: stringResource(Res.string.not_set)
                                Text(text = "${stringResource(device.type.label())} · $value")
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        onAction(SettingsPageAction.OnRemoveDevice(device.key))
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.delete),
                                        contentDescription = stringResource(Res.string.delete),
                                    )
                                }
                            },
                            modifier = Modifier.animateItem(),
                            content = {
                                Text(
                                    text =
                                        AudioOutputDevice(device.key, device.name, device.type)
                                            .displayName()
                                )
                            },
                        )
                    }
                }

                item { SectionTitle(text = stringResource(Res.string.device_types)) }

                val types = AudioDeviceType.entries
                itemsIndexed(items = types, key = { _, type -> "type_${type.name}" }) { index, type
                    ->
                    val typeOffset = offset.typeOffsets[type]
                    ListItem(
                        onClick = { editTarget = OffsetEditTarget.Type(type) },
                        shapes = segmentedListItemShapes(index, types.size),
                        colors = listItemColors(),
                        leadingContent = {
                            Icon(painter = painterResource(type.icon()), contentDescription = null)
                        },
                        supportingContent = {
                            Text(
                                text =
                                    typeOffset?.let(::formatOffset)
                                        ?: stringResource(Res.string.not_set)
                            )
                        },
                        content = { Text(text = stringResource(type.label())) },
                    )
                }
            }
        }

        when (val target = editTarget) {
            is OffsetEditTarget.Device ->
                OffsetEditDialog(
                    title =
                        AudioOutputDevice(target.device.key, target.device.name, target.device.type)
                            .displayName(),
                    initialOffset = target.device.offsetMs,
                    onDismiss = { editTarget = null },
                    onConfirm = {
                        onAction(SettingsPageAction.OnSetDeviceOffset(target.device.key, it))
                        editTarget = null
                    },
                )

            is OffsetEditTarget.Type ->
                OffsetEditDialog(
                    title = stringResource(target.type.label()),
                    initialOffset = offset.typeOffsets[target.type],
                    onDismiss = { editTarget = null },
                    onConfirm = {
                        onAction(SettingsPageAction.OnSetTypeOffset(target.type, it))
                        editTarget = null
                    },
                )

            null -> {}
        }
    }

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontFamily = flexFontRounded()),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp),
    )
}

/** Edits an offset in milliseconds, confirming null clears it. */
@Composable
private fun OffsetEditDialog(
    title: String,
    initialOffset: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit,
) {
    var text by remember { mutableStateOf((initialOffset ?: 0L).toString()) }
    val value = text.toLongOrNull()

    RushDialog(onDismissRequest = onDismiss, padding = 24.dp) {
        Text(
            text = stringResource(Res.string.edit_offset),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(Res.string.offset_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            OutlinedButton(onClick = { text = ((value ?: 0L) - OFFSET_STEP_MS).toString() }) {
                Text(text = "−$OFFSET_STEP_MS")
            }
            OutlinedTextField(
                value = text,
                onValueChange = { new -> text = new.filter { it.isDigit() || it == '-' }.take(6) },
                singleLine = true,
                isError = value == null,
                label = { Text(text = stringResource(Res.string.offset_value_ms)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = { text = ((value ?: 0L) + OFFSET_STEP_MS).toString() }) {
                Text(text = "+$OFFSET_STEP_MS")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        ) {
            TextButton(onClick = { onConfirm(null) }) {
                Text(text = stringResource(Res.string.clear))
            }
            TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
            Button(onClick = { onConfirm(value) }, enabled = value != null) {
                Text(text = stringResource(Res.string.save))
            }
        }
    }
}

@PreviewWrapper(RushPreviewWrapper::class)
@Preview
@Composable
private fun Preview() {
    AudioDevicesPage(
        state =
            SettingsPageState(
                syncOffset =
                    SyncOffsetState(
                        device =
                            AudioOutputDevice("bt:00:11", "Car Kit", AudioDeviceType.CAR, "00:11"),
                        deviceOffsetMs = 250,
                        knownDevices =
                            listOf(
                                DeviceOffset("bt:00:11", "Car Kit", AudioDeviceType.CAR, 250),
                                DeviceOffset("bt:22:33", "Buds", AudioDeviceType.HEADSET, null),
                            ),
                        typeOffsets = mapOf(AudioDeviceType.HEADSET to 150),
                        supported = true,
                    )
            ),
        onAction = {},
        onNavigateBack = {},
    )
}
