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
package com.shub39.rush.shared.core.util

import com.shub39.rush.shared.core.enums.AudioDeviceType

/** Maps platform audio device information to [AudioDeviceType]. */
object AudioDeviceClassifier {
    // android.bluetooth.BluetoothClass.Device values
    private const val WEARABLE_HEADSET = 0x0404
    private const val HANDSFREE = 0x0408
    private const val LOUDSPEAKER = 0x0414
    private const val HEADPHONES = 0x0418
    private const val PORTABLE_AUDIO = 0x041C
    private const val CAR_AUDIO = 0x0420
    private const val SET_TOP_BOX = 0x0424
    private const val HIFI_AUDIO = 0x0428
    private const val VIDEO_MONITOR = 0x0438
    private const val VIDEO_DISPLAY_AND_LOUDSPEAKER = 0x043C

    /** Type from a Bluetooth device class (`BluetoothClass.getDeviceClass()`). */
    fun fromBluetoothClass(deviceClass: Int): AudioDeviceType =
        when (deviceClass) {
            CAR_AUDIO -> AudioDeviceType.CAR
            HEADPHONES -> AudioDeviceType.HEADPHONES
            WEARABLE_HEADSET,
            HANDSFREE -> AudioDeviceType.HEADSET
            LOUDSPEAKER -> AudioDeviceType.SPEAKER
            PORTABLE_AUDIO -> AudioDeviceType.PORTABLE
            HIFI_AUDIO -> AudioDeviceType.HIFI
            SET_TOP_BOX,
            VIDEO_MONITOR,
            VIDEO_DISPLAY_AND_LOUDSPEAKER -> AudioDeviceType.TV
            else -> AudioDeviceType.BLUETOOTH_OTHER
        }

    // android.media.AudioDeviceInfo types
    private const val TYPE_BUILTIN_EARPIECE = 1
    private const val TYPE_BUILTIN_SPEAKER = 2
    private const val TYPE_WIRED_HEADSET = 3
    private const val TYPE_WIRED_HEADPHONES = 4
    private const val TYPE_LINE_ANALOG = 5
    private const val TYPE_LINE_DIGITAL = 6
    private const val TYPE_BLUETOOTH_SCO = 7
    private const val TYPE_BLUETOOTH_A2DP = 8
    private const val TYPE_USB_DEVICE = 11
    private const val TYPE_USB_ACCESSORY = 12
    private const val TYPE_AUX_LINE = 19
    private const val TYPE_USB_HEADSET = 22
    private const val TYPE_HEARING_AID = 23
    private const val TYPE_BUILTIN_SPEAKER_SAFE = 24
    private const val TYPE_BLE_HEADSET = 26
    private const val TYPE_BLE_SPEAKER = 27
    private const val TYPE_BLE_BROADCAST = 30
    private const val TYPE_DOCK_ANALOG = 31
    private const val TYPE_BLE_HEARING_AID = 33

    fun isBluetooth(audioDeviceType: Int): Boolean =
        audioDeviceType in
            setOf(
                TYPE_BLUETOOTH_SCO,
                TYPE_BLUETOOTH_A2DP,
                TYPE_HEARING_AID,
                TYPE_BLE_HEADSET,
                TYPE_BLE_SPEAKER,
                TYPE_BLE_BROADCAST,
                TYPE_BLE_HEARING_AID,
            )

    /** Type from an `AudioDeviceInfo.getType()`, used when the Bluetooth class is unknown. */
    fun fromAudioDeviceType(audioDeviceType: Int): AudioDeviceType =
        when (audioDeviceType) {
            TYPE_BLE_HEADSET -> AudioDeviceType.HEADSET
            TYPE_BLE_SPEAKER -> AudioDeviceType.SPEAKER
            TYPE_HEARING_AID,
            TYPE_BLE_HEARING_AID -> AudioDeviceType.HEARING_AID
            TYPE_BLUETOOTH_SCO,
            TYPE_BLUETOOTH_A2DP,
            TYPE_BLE_BROADCAST -> AudioDeviceType.BLUETOOTH_OTHER
            TYPE_WIRED_HEADSET,
            TYPE_WIRED_HEADPHONES,
            TYPE_LINE_ANALOG,
            TYPE_LINE_DIGITAL,
            TYPE_AUX_LINE,
            TYPE_DOCK_ANALOG -> AudioDeviceType.WIRED
            TYPE_USB_DEVICE,
            TYPE_USB_ACCESSORY,
            TYPE_USB_HEADSET -> AudioDeviceType.USB
            TYPE_BUILTIN_EARPIECE,
            TYPE_BUILTIN_SPEAKER,
            TYPE_BUILTIN_SPEAKER_SAFE -> AudioDeviceType.BUILT_IN
            else -> AudioDeviceType.OTHER
        }

    /**
     * Preference among connected outputs when the routed one can not be queried: lower is better,
     * null for devices that never play media (earpiece, telephony...).
     */
    fun mediaPriority(audioDeviceType: Int): Int? =
        when (audioDeviceType) {
            TYPE_BLUETOOTH_A2DP,
            TYPE_BLE_HEADSET,
            TYPE_BLE_SPEAKER,
            TYPE_BLE_BROADCAST,
            TYPE_HEARING_AID,
            TYPE_BLE_HEARING_AID -> 0
            TYPE_USB_HEADSET,
            TYPE_USB_DEVICE,
            TYPE_USB_ACCESSORY -> 1
            TYPE_WIRED_HEADSET,
            TYPE_WIRED_HEADPHONES,
            TYPE_LINE_ANALOG,
            TYPE_LINE_DIGITAL,
            TYPE_AUX_LINE,
            TYPE_DOCK_ANALOG -> 2
            TYPE_BUILTIN_SPEAKER -> 3
            else -> null
        }
}
