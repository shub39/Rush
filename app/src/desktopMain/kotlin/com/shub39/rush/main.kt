package com.shub39.rush

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.shub39.rush.di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        title = "Rush",
        state = WindowState(width = 1200.dp, height = 900.dp),
        resizable = false,
        alwaysOnTop = true
    ) {
        RushApp() // Broke for some reason, maybe because of cmp beta??
    }
}