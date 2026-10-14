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

import androidx.compose.runtime.Composable

/** A runtime permission: whether it is granted and how to ask for it. */
class BluetoothPermissionState(val granted: Boolean, val request: () -> Unit)

/** Bluetooth access, needed to read the name and type of the Bluetooth audio output. */
@Composable
expect fun rememberBluetoothPermission(onResult: (Boolean) -> Unit): BluetoothPermissionState

/** Notifications permission (Android 13+), for the background lyrics notification. */
@Composable
expect fun rememberNotificationPermission(onResult: (Boolean) -> Unit): BluetoothPermissionState
