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
package com.shub39.rush.shared.ui

import androidx.compose.runtime.Composable
import com.shub39.rush.shared.core.dataclasses.AudioOutputDevice
import com.shub39.rush.shared.core.dataclasses.OffsetOrigin
import com.shub39.rush.shared.core.enums.AudioDeviceType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import rush.shared.ui.generated.resources.*

fun AudioDeviceType.label(): StringResource =
    when (this) {
        AudioDeviceType.CAR -> Res.string.device_type_car
        AudioDeviceType.HEADPHONES -> Res.string.device_type_headphones
        AudioDeviceType.HEADSET -> Res.string.device_type_headset
        AudioDeviceType.SPEAKER -> Res.string.device_type_speaker
        AudioDeviceType.PORTABLE -> Res.string.device_type_portable
        AudioDeviceType.HIFI -> Res.string.device_type_hifi
        AudioDeviceType.TV -> Res.string.device_type_tv
        AudioDeviceType.HEARING_AID -> Res.string.device_type_hearing_aid
        AudioDeviceType.BLUETOOTH_OTHER -> Res.string.device_type_bluetooth
        AudioDeviceType.WIRED -> Res.string.device_type_wired
        AudioDeviceType.USB -> Res.string.device_type_usb
        AudioDeviceType.BUILT_IN -> Res.string.device_type_built_in
        AudioDeviceType.OTHER -> Res.string.device_type_other
    }

fun AudioDeviceType.icon(): DrawableResource =
    if (isBluetooth) Res.drawable.bluetooth else Res.drawable.eq

fun OffsetOrigin.label(): StringResource =
    when (this) {
        OffsetOrigin.DEVICE -> Res.string.offset_from_device
        OffsetOrigin.TYPE -> Res.string.offset_from_type
        OffsetOrigin.NONE -> Res.string.offset_none
    }

/** Device name, or its type when the name is unknown (built-in speaker, wired...). */
@Composable
fun AudioOutputDevice.displayName(): String = name.ifBlank { stringResource(type.label()) }

/** "+150 ms", "−50 ms" or "0 ms". */
fun formatOffset(offsetMs: Long): String =
    when {
        offsetMs > 0 -> "+$offsetMs ms"
        offsetMs < 0 -> "−${-offsetMs} ms"
        else -> "0 ms"
    }
