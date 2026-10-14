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

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.dataclasses.AudioOutputDevice
import com.shub39.rush.shared.core.enums.AudioDeviceType
import com.shub39.rush.shared.core.util.AudioDeviceClassifier
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
actual class AudioOutputDetector(private val context: Context) {
    private val audioManager by lazy { context.getSystemService(AudioManager::class.java) }
    private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) }

    private val mediaAttributes =
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

    actual val isSupported: Boolean = true

    actual fun hasBluetoothPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED

    actual fun outputs(): Flow<AudioOutputDevice?> = callbackFlow {
        val callback =
            object : AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                    trySend(currentOutput())
                }

                override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                    trySend(currentOutput())
                }
            }
        audioManager.registerAudioDeviceCallback(callback, Handler(Looper.getMainLooper()))

        // The route can change without any device being added or removed (output switcher)
        val poll = launch {
            while (isActive) {
                send(currentOutput())
                delay(POLL_INTERVAL)
            }
        }

        awaitClose {
            poll.cancel()
            audioManager.unregisterAudioDeviceCallback(callback)
        }
    }

    private fun currentOutput(): AudioOutputDevice? =
        try {
            activeDevice()?.toOutputDevice()
        } catch (e: Exception) {
            RushLogger.e(TAG, "Output detection failed", e)
            null
        }

    private fun activeDevice(): AudioDeviceInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            audioManager.getAudioDevicesForAttributes(mediaAttributes).firstOrNull()?.let {
                return it
            }
        }

        return audioManager
            .getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            .mapNotNull { device ->
                AudioDeviceClassifier.mediaPriority(device.type)?.let { device to it }
            }
            .minByOrNull { it.second }
            ?.first
    }

    private fun AudioDeviceInfo.toOutputDevice(): AudioOutputDevice {
        val productName = productName?.toString()?.trim().orEmpty()

        if (!AudioDeviceClassifier.isBluetooth(type)) {
            val deviceType = AudioDeviceClassifier.fromAudioDeviceType(type)
            return when (deviceType) {
                AudioDeviceType.USB,
                AudioDeviceType.OTHER ->
                    AudioOutputDevice(
                        key = "${deviceType.name.lowercase()}:$productName",
                        name = productName,
                        type = deviceType,
                    )
                // The product name of built-in or wired outputs is the phone model
                else ->
                    AudioOutputDevice(key = "type:${deviceType.name}", name = "", type = deviceType)
            }
        }

        val address = address?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
        val bluetoothDevice =
            if (address != null && hasBluetoothPermission()) {
                try {
                    bluetoothManager?.adapter?.getRemoteDevice(address)
                } catch (_: IllegalArgumentException) {
                    null // anonymized address
                }
            } else null

        val bluetoothName =
            try {
                bluetoothDevice?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) it.alias ?: it.name
                    else it.name
                }
            } catch (_: SecurityException) {
                null
            }

        val deviceClass =
            try {
                bluetoothDevice?.bluetoothClass?.deviceClass
            } catch (_: SecurityException) {
                null
            }

        val name = bluetoothName?.takeIf { it.isNotBlank() } ?: productName

        return AudioOutputDevice(
            key = address?.let { "bt:$it" } ?: "bt-name:$name",
            name = name,
            type =
                deviceClass?.let { AudioDeviceClassifier.fromBluetoothClass(it) }
                    ?: AudioDeviceClassifier.fromAudioDeviceType(type),
            address = address,
        )
    }

    private companion object {
        const val TAG = "AudioOutputDetector"
        val POLL_INTERVAL = 3.seconds
    }
}
