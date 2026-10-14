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
package com.shub39.rush.shared.core.interfaces

import com.shub39.rush.shared.core.dataclasses.SyncOffsetState
import com.shub39.rush.shared.core.enums.AudioDeviceType
import kotlinx.coroutines.flow.StateFlow

/** Detects the audio output and provides the lyrics sync offset to apply for it. */
interface SyncOffsetManager {
    /** Output detection only runs while this flow is collected. */
    val state: StateFlow<SyncOffsetState>

    /** Detects the output again, e.g. after the Bluetooth permission was granted. */
    fun refresh()

    /** Adds [deltaMs] to the unsaved adjustment of the current session. */
    fun adjustSession(deltaMs: Long)

    fun resetSession()

    /** Saves the offset currently applied (device + adjustment) for the current device. */
    suspend fun saveForDevice()

    /**
     * Saves the offset currently applied for the type of the current device. Its own device offset
     * is cleared so the type offset applies to it too.
     */
    suspend fun saveForType()

    /** Sets the offset of a known device, null to use its type offset. */
    suspend fun setDeviceOffset(key: String, offsetMs: Long?)

    suspend fun removeDevice(key: String)

    /** Sets the default offset of a device type, null to remove it. */
    suspend fun setTypeOffset(type: AudioDeviceType, offsetMs: Long?)
}
