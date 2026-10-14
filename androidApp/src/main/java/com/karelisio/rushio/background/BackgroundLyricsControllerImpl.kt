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
package com.karelisio.rushio.background

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import com.shub39.rush.shared.core.interfaces.BackgroundLyricsController
import kotlinx.coroutines.flow.StateFlow

class BackgroundLyricsControllerImpl(private val context: Context) : BackgroundLyricsController {
    override val isSupported: Boolean = true

    override val isRunning: StateFlow<Boolean> = BackgroundLyricsService.running

    override fun canDrawOverlays(): Boolean = Settings.canDrawOverlays(context)

    override fun requestOverlayPermission() {
        val intent =
            Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}"),
                )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
            .onFailure { RushLogger.e(TAG, "Could not open overlay settings", it) }
    }

    override fun apply(mode: BackgroundLyricsMode) {
        val intent =
            Intent(context, BackgroundLyricsService::class.java)
                .putExtra(BackgroundLyricsService.EXTRA_MODE, mode.name)

        when {
            mode == BackgroundLyricsMode.OFF -> context.stopService(intent)
            // Started again once the permission is granted
            mode == BackgroundLyricsMode.OVERLAY && !canDrawOverlays() ->
                context.stopService(intent)
            else ->
                try {
                    ContextCompat.startForegroundService(context, intent)
                } catch (e: Exception) {
                    // Android refuses foreground services started from the background
                    RushLogger.e(TAG, "Could not start the background lyrics", e)
                }
        }
    }

    override val manufacturer: String = Build.MANUFACTURER.orEmpty()

    override fun isIgnoringBatteryOptimizations(): Boolean =
        context
            .getSystemService(PowerManager::class.java)
            .isIgnoringBatteryOptimizations(context.packageName)

    override fun requestIgnoreBatteryOptimizations() {
        val request =
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:${context.packageName}"),
            )
        if (!start(request)) start(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
    }

    override fun openAutoStartSettings(): Boolean =
        autoStartScreens.any { start(Intent().setComponent(it)) }

    override fun openAppDetails() {
        start(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}"),
            )
        )
    }

    private fun start(intent: Intent): Boolean =
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (_: Exception) {
            // Not installed, or not exported on this firmware
            false
        }

    private companion object {
        const val TAG = "BackgroundLyrics"

        /** Auto start managers, Honor (MagicOS) and Huawei first. */
        val autoStartScreens =
            listOf(
                ComponentName(
                    "com.hihonor.systemmanager",
                    "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
                ),
                ComponentName(
                    "com.hihonor.systemmanager",
                    "com.hihonor.systemmanager.optimize.process.ProtectActivity",
                ),
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
                ),
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity",
                ),
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity",
                ),
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity",
                ),
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity",
                ),
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity",
                ),
            )
    }
}
