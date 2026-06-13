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
package com.shub39.rush

import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.shub39.rush.app.App
import com.shub39.rush.di.RushModules
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import org.koin.plugin.module.dsl.startKoin

fun main() {
    startKoin<RushModules>()

    singleWindowApplication(title = "Rush", state = WindowState(width = 450.dp, height = 1000.dp)) {
        val windowSizeClass = calculateWindowSizeClass()
        val viewModelStoreOwner = remember {
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }
        }
        val currentDensity = LocalDensity.current
        val scaledDensity =
            Density(
                density = currentDensity.density * 1.1f,
                fontScale = currentDensity.fontScale,
            )

        CompositionLocalProvider(
            LocalDensity provides scaledDensity,
            LocalViewModelStoreOwner provides viewModelStoreOwner,
            LocalWindowSizeClass provides windowSizeClass,
        ) {
            App()
        }
    }
}
