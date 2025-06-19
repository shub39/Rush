package com.shub39.rush

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.materialkolor.PaletteStyle
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.Fonts
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.di.initKoin
import com.shub39.rush.lyrics.presentation.SettingsGraph
import com.shub39.rush.lyrics.presentation.setting.SettingsPageState

fun main() {
    initKoin()

//    singleWindowApplication(
//        title = "Rush",
//        state = WindowState(width = 1200.dp, height = 900.dp),
//        resizable = false,
//        alwaysOnTop = true
//    ) {
//        RushApp() // Broke for some reason, maybe because of cmp beta??
//    }

    singleWindowApplication(
        title = "Hot Reload",
        state = WindowState(width = 450.dp, height = 950.dp),
        resizable = false,
        alwaysOnTop = true
    ) {
        var state by remember { mutableStateOf(SettingsPageState()) }

        RushTheme(
            state = Theme(
                appTheme = AppTheme.DARK,
                fonts = Fonts.MANROPE,
                style = PaletteStyle.Expressive
            )
        ) {
            SettingsGraph(
                notificationAccess = true,
                state = state,
                action = {  },
                onNavigateBack = {  }
            )
        }
    }
}