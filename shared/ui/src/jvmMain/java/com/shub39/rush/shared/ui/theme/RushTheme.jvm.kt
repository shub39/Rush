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
package com.shub39.rush.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.shub39.rush.shared.core.dataclasses.Theme
import com.shub39.rush.shared.core.enums.AppTheme
import com.shub39.rush.shared.ui.toFontRes
import com.shub39.rush.shared.ui.toMPaletteStyle

@Composable
actual fun RushTheme(theme: Theme, content: @Composable (() -> Unit)) {
    DynamicMaterialTheme(
        seedColor = Color(theme.seedColor),
        isDark =
            when (theme.appTheme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
            },
        isAmoled = theme.withAmoled,
        style = theme.style.toMPaletteStyle(),
        typography = provideTypography(theme.font.toFontRes()),
        content = content,
    )
}
