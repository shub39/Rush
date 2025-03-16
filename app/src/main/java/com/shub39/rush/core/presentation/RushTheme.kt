package com.shub39.rush.core.presentation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialTheme
import com.shub39.rush.core.data.Theme
import com.shub39.rush.core.domain.AppTheme

@Composable
fun RushTheme(
    state: Theme = Theme(),
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = if (state.materialTheme && Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            Color(state.seedColor)
        },
        useDarkTheme = when (state.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        },
        withAmoled = state.withAmoled,
        style = state.style,
        typography = provideTypography(
            font = state.fonts.fontId
        ),
        content = content
    )
}