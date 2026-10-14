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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.shub39.rush.shared.core.dataclasses.DeviceOffset
import com.shub39.rush.shared.core.enums.AudioDeviceType
import com.shub39.rush.shared.logic.audio.AudioOutputDetector
import com.shub39.rush.shared.logic.audio.SyncOffsetManagerImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

private class InMemoryDataStore : DataStore<Preferences> {
    private val state = MutableStateFlow(emptyPreferences())
    override val data: Flow<Preferences> = state

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences
    ): Preferences = transform(state.value).also { state.value = it }
}

class SyncOffsetManagerTest {
    @Test
    fun typeOffsetsArePersistedAndRemoved(): Unit = runBlocking {
        val manager = SyncOffsetManagerImpl(AudioOutputDetector(), InMemoryDataStore())

        manager.setTypeOffset(AudioDeviceType.CAR, 250)
        manager.setTypeOffset(AudioDeviceType.SPEAKER, -40)
        withTimeout(5_000) { manager.state.first { it.typeOffsets.size == 2 } }
        assertEquals(250L, manager.state.value.typeOffsets[AudioDeviceType.CAR])

        manager.setTypeOffset(AudioDeviceType.CAR, null)
        withTimeout(5_000) { manager.state.first { AudioDeviceType.CAR !in it.typeOffsets } }
        assertEquals(mapOf(AudioDeviceType.SPEAKER to -40L), manager.state.value.typeOffsets)

        manager.adjustSession(50)
        manager.adjustSession(50)
        withTimeout(5_000) { manager.state.first { it.sessionAdjustMs == 100L } }
        manager.resetSession()
        withTimeout(5_000) { manager.state.first { it.sessionAdjustMs == 0L } }
    }

    @Test
    fun storedValuesSurviveBadData() {
        assertTrue(SyncOffsetManagerImpl.decodeDevices("not json").isEmpty())
        assertEquals(
            mapOf(AudioDeviceType.HEADSET to 120L),
            SyncOffsetManagerImpl.decodeTypes("""{"HEADSET":120,"SPACESHIP":5}"""),
        )
        val types = mapOf(AudioDeviceType.CAR to 300L, AudioDeviceType.TV to -20L)
        assertEquals(
            types,
            SyncOffsetManagerImpl.decodeTypes(SyncOffsetManagerImpl.encodeTypes(types)),
        )
        assertEquals(
            listOf(DeviceOffset("bt:AA", "Car", AudioDeviceType.CAR, 100, 1)),
            SyncOffsetManagerImpl.decodeDevices(
                """[{"key":"bt:AA","name":"Car","type":"CAR","offsetMs":100,"lastSeen":1}]"""
            ),
        )
    }
}
