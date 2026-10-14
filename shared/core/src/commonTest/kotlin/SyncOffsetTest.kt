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
import com.shub39.rush.shared.core.dataclasses.AudioOutputDevice
import com.shub39.rush.shared.core.dataclasses.DeviceOffset
import com.shub39.rush.shared.core.dataclasses.OffsetOrigin
import com.shub39.rush.shared.core.dataclasses.SyncOffsetState
import com.shub39.rush.shared.core.enums.AudioDeviceType
import com.shub39.rush.shared.core.util.AudioDeviceClassifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SyncOffsetTest {
    private val car = AudioOutputDevice("bt:AA", "My car", AudioDeviceType.CAR, "AA")

    @Test
    fun deviceOffsetWinsOverTypeOffset() {
        val types = mapOf(AudioDeviceType.CAR to 300L)

        assertEquals(
            120L to OffsetOrigin.DEVICE,
            SyncOffsetState.resolve(
                car,
                listOf(DeviceOffset("bt:AA", "My car", AudioDeviceType.CAR, 120)),
                types,
            ),
        )
        // A known device without its own offset falls back to its type
        assertEquals(
            300L to OffsetOrigin.TYPE,
            SyncOffsetState.resolve(
                car,
                listOf(DeviceOffset("bt:AA", "My car", AudioDeviceType.CAR, null)),
                types,
            ),
        )
        assertEquals(0L to OffsetOrigin.NONE, SyncOffsetState.resolve(car, emptyList(), emptyMap()))
        assertEquals(0L to OffsetOrigin.NONE, SyncOffsetState.resolve(null, emptyList(), types))
    }

    @Test
    fun totalOffsetAddsSessionAndSong() {
        val state = SyncOffsetState(device = car, deviceOffsetMs = 200, sessionAdjustMs = -50)
        assertEquals(150, state.totalOffset())
        assertEquals(175, state.totalOffset(songOffsetMs = 25))
    }

    @Test
    fun bluetoothClassesAreClassified() {
        assertEquals(AudioDeviceType.CAR, AudioDeviceClassifier.fromBluetoothClass(1056))
        assertEquals(AudioDeviceType.HEADPHONES, AudioDeviceClassifier.fromBluetoothClass(1048))
        assertEquals(AudioDeviceType.HEADSET, AudioDeviceClassifier.fromBluetoothClass(1028))
        assertEquals(AudioDeviceType.HEADSET, AudioDeviceClassifier.fromBluetoothClass(1032))
        assertEquals(AudioDeviceType.SPEAKER, AudioDeviceClassifier.fromBluetoothClass(1044))
        assertEquals(AudioDeviceType.PORTABLE, AudioDeviceClassifier.fromBluetoothClass(1052))
        assertEquals(AudioDeviceType.HIFI, AudioDeviceClassifier.fromBluetoothClass(1064))
        assertEquals(AudioDeviceType.TV, AudioDeviceClassifier.fromBluetoothClass(1084))
        assertEquals(
            AudioDeviceType.BLUETOOTH_OTHER,
            AudioDeviceClassifier.fromBluetoothClass(1024),
        )
    }

    @Test
    fun audioDeviceTypesAreClassified() {
        // AudioDeviceInfo.TYPE_* values
        assertEquals(AudioDeviceType.BLUETOOTH_OTHER, AudioDeviceClassifier.fromAudioDeviceType(8))
        assertEquals(AudioDeviceType.SPEAKER, AudioDeviceClassifier.fromAudioDeviceType(27))
        assertEquals(AudioDeviceType.WIRED, AudioDeviceClassifier.fromAudioDeviceType(4))
        assertEquals(AudioDeviceType.USB, AudioDeviceClassifier.fromAudioDeviceType(22))
        assertEquals(AudioDeviceType.BUILT_IN, AudioDeviceClassifier.fromAudioDeviceType(2))
        assertTrue(AudioDeviceClassifier.isBluetooth(26))

        val connected = listOf(2, 4, 8)
        assertEquals(8, connected.minBy { AudioDeviceClassifier.mediaPriority(it)!! })
        assertNull(AudioDeviceClassifier.mediaPriority(1)) // earpiece never plays media
    }
}
