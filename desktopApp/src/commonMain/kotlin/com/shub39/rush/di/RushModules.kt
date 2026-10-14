/*
 * Copyright (C) 2026  Shubham Gorai
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
package com.shub39.rush.di

import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import com.shub39.rush.shared.core.interfaces.AnalyticsWrapper
import com.shub39.rush.shared.core.interfaces.BackgroundLyricsController
import com.shub39.rush.shared.logic.di.DataModule
import com.shub39.rush.shared.ui.di.UIModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Configuration
@Module(includes = [DataModule::class, UIModule::class])
class RushModules {
    @Single
    fun provideAnalyticsWrapper(): AnalyticsWrapper =
        AnalyticsWrapper.Companion.DummyAnalyticsWrapper()

    // No background lyrics on desktop
    @Single
    fun provideBackgroundLyricsController(): BackgroundLyricsController =
        object : BackgroundLyricsController {
            override val isSupported = false
            override val isRunning: StateFlow<Boolean> = MutableStateFlow(false)

            override fun canDrawOverlays() = false

            override fun requestOverlayPermission() {}

            override fun apply(mode: BackgroundLyricsMode) {}

            override val manufacturer = ""

            override fun isIgnoringBatteryOptimizations() = true

            override fun requestIgnoreBatteryOptimizations() {}

            override fun openAutoStartSettings() = false

            override fun openAppDetails() {}
        }
}
