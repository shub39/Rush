package com.shub39.rush

import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.rush.app.App
import com.shub39.rush.di.RushModules
import com.shub39.rush.shared.ui.LocalWindowSizeClass
import org.koin.plugin.module.dsl.startKoin

fun main() {
    startKoin<RushModules>()

    singleWindowApplication(
        title = "Rush",
        state = WindowState(width = 400.dp, height = 900.dp)
    ) {
        val windowSizeClass = calculateWindowSizeClass()

        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            App()
        }
    }
}