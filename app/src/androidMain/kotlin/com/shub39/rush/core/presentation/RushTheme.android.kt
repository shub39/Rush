package com.shub39.rush.core.presentation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun RushTheme(
    state: Theme,
    fontScale: Float,
    content: @Composable () -> Unit
) {
    DynamicMaterialExpressiveTheme(
        seedColor = if (state.materialTheme && Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            Color(state.seedColor)
        },
        isDark = when (state.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        },
        isAmoled = state.withAmoled,
        style = state.style,
        typography = provideTypography(
            font = state.fonts.font,
            scale = fontScale
        ),
        content = content
    )
}