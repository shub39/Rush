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
package com.shub39.rush.shared.ui.component

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberBluetoothPermission(onResult: (Boolean) -> Unit): BluetoothPermissionState {
    // BLUETOOTH_CONNECT only exists since Android 12, the legacy permission is granted at install
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return remember { BluetoothPermissionState(granted = true, request = {}) }
    }

    val permission =
        rememberPermissionState(
            Manifest.permission.BLUETOOTH_CONNECT,
            onPermissionResult = onResult,
        )
    return BluetoothPermissionState(
        granted = permission.status.isGranted,
        request = { permission.launchPermissionRequest() },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberNotificationPermission(onResult: (Boolean) -> Unit): BluetoothPermissionState {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return remember { BluetoothPermissionState(granted = true, request = {}) }
    }

    val permission =
        rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS,
            onPermissionResult = onResult,
        )
    return BluetoothPermissionState(
        granted = permission.status.isGranted,
        request = { permission.launchPermissionRequest() },
    )
}
