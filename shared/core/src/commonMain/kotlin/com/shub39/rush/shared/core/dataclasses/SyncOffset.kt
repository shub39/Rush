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
package com.shub39.rush.shared.core.dataclasses

import com.shub39.rush.shared.core.enums.AudioDeviceType
import kotlinx.serialization.Serializable

/**
 * The audio output currently used for media.
 *
 * @property key Stable identifier: the Bluetooth address when known, else derived from the type.
 */
data class AudioOutputDevice(
    val key: String,
    val name: String,
    val type: AudioDeviceType,
    val address: String? = null,
)

/**
 * An output seen at least once, with its own sync offset.
 *
 * @property offsetMs Offset in milliseconds, null when the device uses its type offset.
 */
@Serializable
data class DeviceOffset(
    val key: String,
    val name: String,
    val type: AudioDeviceType,
    val offsetMs: Long? = null,
    val lastSeen: Long = 0,
)

/** Where the offset of the current device comes from. */
enum class OffsetOrigin {
    DEVICE,
    TYPE,
    NONE,
}

/**
 * Sync offset applied to lyrics, positive values delay them (Bluetooth latency).
 *
 * @property deviceOffsetMs Offset resolved for [device]: device > type > 0.
 * @property sessionAdjustMs Unsaved adjustment made while listening.
 * @property supported False on platforms where the output can not be detected.
 */
data class SyncOffsetState(
    val device: AudioOutputDevice? = null,
    val deviceOffsetMs: Long = 0,
    val origin: OffsetOrigin = OffsetOrigin.NONE,
    val sessionAdjustMs: Long = 0,
    val knownDevices: List<DeviceOffset> = emptyList(),
    val typeOffsets: Map<AudioDeviceType, Long> = emptyMap(),
    val bluetoothPermission: Boolean = true,
    val supported: Boolean = false,
) {
    /** Offset applied to the lyrics of a song having its own [songOffsetMs] fine tuning. */
    fun totalOffset(songOffsetMs: Long = 0): Long = deviceOffsetMs + sessionAdjustMs + songOffsetMs

    companion object {
        /** Resolves the offset of [device]: its own offset, else its type offset, else none. */
        fun resolve(
            device: AudioOutputDevice?,
            knownDevices: List<DeviceOffset>,
            typeOffsets: Map<AudioDeviceType, Long>,
        ): Pair<Long, OffsetOrigin> {
            if (device == null) return 0L to OffsetOrigin.NONE
            knownDevices
                .firstOrNull { it.key == device.key }
                ?.offsetMs
                ?.let {
                    return it to OffsetOrigin.DEVICE
                }
            typeOffsets[device.type]?.let {
                return it to OffsetOrigin.TYPE
            }
            return 0L to OffsetOrigin.NONE
        }
    }
}
