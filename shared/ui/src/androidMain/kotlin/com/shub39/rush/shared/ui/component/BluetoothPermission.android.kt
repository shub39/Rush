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
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

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
    val context = LocalContext.current
    // Notifications can also be turned off in the system settings: check again when coming back
    var enabled by remember { mutableStateOf(notificationsEnabled(context)) }
    LifecycleResumeEffect(Unit) {
        enabled = notificationsEnabled(context)
        onPauseOrDispose {}
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return BluetoothPermissionState(
            granted = enabled,
            request = { openNotificationSettings(context) },
        )
    }

    var rationaleBeforeRequest by remember { mutableStateOf(false) }
    lateinit var permission: PermissionState
    permission =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { granted ->
            enabled = notificationsEnabled(context)
            // Denied twice earlier: Android answers without showing anything, open the settings
            if (!granted && !rationaleBeforeRequest && !permission.status.shouldShowRationale) {
                openNotificationSettings(context)
            }
            onResult(granted)
        }
    return BluetoothPermissionState(
        granted = permission.status.isGranted && enabled,
        request = {
            if (permission.status.isGranted) {
                openNotificationSettings(context)
            } else {
                rationaleBeforeRequest = permission.status.shouldShowRationale
                permission.launchPermissionRequest()
            }
        },
    )
}

private fun notificationsEnabled(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()

private fun openNotificationSettings(context: Context) {
    val notifications =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    val appDetails =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        )
    for (intent in listOf(notifications, appDetails)) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: ActivityNotFoundException) {
            // Try the next screen
        }
    }
}
