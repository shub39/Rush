package com.shub39.rush.presentation.components

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.materialkolor.DynamicMaterialTheme
import com.shub39.rush.domain.dataclasses.Theme
import com.shub39.rush.domain.enums.AppTheme
import com.shub39.rush.presentation.provideTypography
import com.shub39.rush.presentation.toFontRes
import com.shub39.rush.presentation.toMPaletteStyle

/**
 * A Composable function that applies a dynamic Material You theme to its content.
 *
 * @param theme The [Theme] data class containing all the user's selected theming options,
 *   such as the seed color, light/dark mode preference, color style, and font.
 * @param content The Composable content to which this theme will be applied.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RushTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = if (theme.materialTheme && Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_200)
        } else {
            Color(theme.seedColor)
        },
        isDark = when (theme.appTheme) {
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        },
        isAmoled = theme.withAmoled,
        style = theme.style.toMPaletteStyle(),
        typography = provideTypography(
            font = theme.font.toFontRes(),
        ),
        content = content
    )
}