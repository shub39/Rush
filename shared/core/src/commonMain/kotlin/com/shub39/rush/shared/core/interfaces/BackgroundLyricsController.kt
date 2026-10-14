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

import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import kotlinx.coroutines.flow.StateFlow

/** Platform side of the background lyrics (floating window or notification). */
interface BackgroundLyricsController {
    /** False on platforms without background lyrics. */
    val isSupported: Boolean

    /** Whether the background lyrics service currently runs. */
    val isRunning: StateFlow<Boolean>

    /** Whether the app may draw over other apps. */
    fun canDrawOverlays(): Boolean

    /** Opens the system screen granting the "display over other apps" permission. */
    fun requestOverlayPermission()

    /** Starts or stops the background lyrics service to match [mode]. */
    fun apply(mode: BackgroundLyricsMode)

    /** Device manufacturer, to show vendor specific battery instructions (e.g. "HONOR"). */
    val manufacturer: String

    fun isIgnoringBatteryOptimizations(): Boolean

    /** Asks to exempt the app from battery optimization. */
    fun requestIgnoreBatteryOptimizations()

    /** Opens the vendor screen controlling auto start, false when none was found. */
    fun openAutoStartSettings(): Boolean

    /** Opens the system information screen of the app. */
    fun openAppDetails()
}
