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
package com.shub39.rush.shared.logic.audio

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.rush.shared.core.dataclasses.AudioOutputDevice
import com.shub39.rush.shared.core.dataclasses.DeviceOffset
import com.shub39.rush.shared.core.dataclasses.SyncOffsetState
import com.shub39.rush.shared.core.enums.AudioDeviceType
import com.shub39.rush.shared.core.interfaces.SyncOffsetManager
import com.shub39.rush.shared.core.valueOfOrNull
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@OptIn(ExperimentalCoroutinesApi::class)
@Single(binds = [SyncOffsetManager::class])
class SyncOffsetManagerImpl(
    private val detector: AudioOutputDetector,
    @Named("Other") private val dataStore: DataStore<Preferences>,
) : SyncOffsetManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val currentDevice = MutableStateFlow<AudioOutputDevice?>(null)
    private val session = MutableStateFlow(0L)
    private val refreshRequests = MutableStateFlow(0)
    private val permission = MutableStateFlow(true)

    private val knownDevices = dataStore.data.map { decodeDevices(it[devicesKey]) }
    private val typeOffsets = dataStore.data.map { decodeTypes(it[typesKey]) }

    private val _state = MutableStateFlow(SyncOffsetState(supported = detector.isSupported))
    override val state: StateFlow<SyncOffsetState> = _state.asStateFlow()

    init {
        // The output is only watched while somebody uses the offset (lyrics screen, overlay...)
        scope.launch {
            _state.subscriptionCount
                .map { it > 0 }
                .distinctUntilChanged()
                .flatMapLatest { observed ->
                    if (observed) refreshRequests.flatMapLatest { detector.outputs() }
                    else emptyFlow()
                }
                .distinctUntilChanged()
                .collect { onOutputChanged(it) }
        }

        scope.launch {
            combine(currentDevice, knownDevices, typeOffsets, session, permission) {
                    device,
                    devices,
                    types,
                    adjust,
                    granted ->
                    val (offset, origin) = SyncOffsetState.resolve(device, devices, types)
                    SyncOffsetState(
                        device = device,
                        deviceOffsetMs = offset,
                        origin = origin,
                        sessionAdjustMs = adjust,
                        knownDevices = devices.sortedByDescending { it.lastSeen },
                        typeOffsets = types,
                        bluetoothPermission = granted,
                        supported = detector.isSupported,
                    )
                }
                .collect { _state.value = it }
        }
    }

    private suspend fun onOutputChanged(output: AudioOutputDevice?) {
        permission.value = detector.hasBluetoothPermission()

        // Without the Bluetooth permission the type may be unknown, reuse what was learnt before
        val known =
            output?.let { device -> knownDevices.first().firstOrNull { it.key == device.key } }
        val device =
            if (output != null && known != null && output.type == AudioDeviceType.BLUETOOTH_OTHER) {
                output.copy(type = known.type, name = output.name.ifBlank { known.name })
            } else output

        if (device?.key != currentDevice.value?.key) session.value = 0
        currentDevice.value = device

        if (device != null) {
            updateDevices { devices ->
                val existing = devices.firstOrNull { it.key == device.key }
                val entry =
                    DeviceOffset(
                        key = device.key,
                        name = device.name.ifBlank { existing?.name.orEmpty() },
                        type = device.type,
                        offsetMs = existing?.offsetMs,
                        lastSeen = Clock.System.now().toEpochMilliseconds(),
                    )
                listOf(entry) + devices.filterNot { it.key == device.key }
            }
        }
    }

    override fun refresh() {
        refreshRequests.update { it + 1 }
    }

    override fun adjustSession(deltaMs: Long) {
        session.update { it + deltaMs }
    }

    override fun resetSession() {
        session.value = 0
    }

    override suspend fun saveForDevice() {
        val device = currentDevice.value ?: return
        setDeviceOffset(device.key, _state.value.deviceOffsetMs + session.value)
        session.value = 0
    }

    override suspend fun saveForType() {
        val device = currentDevice.value ?: return
        val offset = _state.value.deviceOffsetMs + session.value
        setTypeOffset(device.type, offset)
        // The device offset would win over the type one, remove it
        setDeviceOffset(device.key, null)
        session.value = 0
    }

    override suspend fun setDeviceOffset(key: String, offsetMs: Long?) {
        updateDevices { devices ->
            devices.map { if (it.key == key) it.copy(offsetMs = offsetMs) else it }
        }
    }

    override suspend fun removeDevice(key: String) {
        updateDevices { devices -> devices.filterNot { it.key == key } }
    }

    override suspend fun setTypeOffset(type: AudioDeviceType, offsetMs: Long?) {
        dataStore.edit { preferences ->
            val types = decodeTypes(preferences[typesKey]).toMutableMap()
            if (offsetMs == null) types.remove(type) else types[type] = offsetMs
            preferences[typesKey] = encodeTypes(types)
        }
    }

    private suspend fun updateDevices(transform: (List<DeviceOffset>) -> List<DeviceOffset>) {
        dataStore.edit { preferences ->
            preferences[devicesKey] =
                json.encodeToString(transform(decodeDevices(preferences[devicesKey])))
        }
    }

    companion object {
        private val devicesKey = stringPreferencesKey("sync_offset_devices")
        private val typesKey = stringPreferencesKey("sync_offset_types")
        private val json = Json { ignoreUnknownKeys = true }

        fun decodeDevices(value: String?): List<DeviceOffset> =
            value?.let { runCatching { json.decodeFromString<List<DeviceOffset>>(it) }.getOrNull() }
                ?: emptyList()

        fun decodeTypes(value: String?): Map<AudioDeviceType, Long> =
            value
                ?.let { runCatching { json.decodeFromString<Map<String, Long>>(it) }.getOrNull() }
                ?.mapNotNull { (name, offset) ->
                    valueOfOrNull<AudioDeviceType>(name)?.let { it to offset }
                }
                ?.toMap() ?: emptyMap()

        fun encodeTypes(types: Map<AudioDeviceType, Long>): String =
            json.encodeToString(types.mapKeys { it.key.name })
    }
}
