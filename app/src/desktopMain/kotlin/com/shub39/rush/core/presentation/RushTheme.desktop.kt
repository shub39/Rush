package com.shub39.rush.core.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme

@Composable
actual fun RushTheme(
    state: Theme,
    fontScale: Float,
    content: @Composable (() -> Unit)
) {
    DynamicMaterialTheme(
        seedColor = Color(state.seedColor),
        useDarkTheme = when (state.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        },
        withAmoled = state.withAmoled,
        style = state.style,
        typography = provideTypography(
            font = state.fonts.font,
            scale = fontScale
        ),
        content = content
    )
}