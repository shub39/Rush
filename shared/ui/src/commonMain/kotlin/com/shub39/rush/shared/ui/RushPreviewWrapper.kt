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
package com.shub39.rush.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import androidx.window.core.layout.WindowSizeClass
import com.shub39.rush.shared.core.dataclasses.Theme
import com.shub39.rush.shared.ui.theme.RushTheme

class RushPreviewWrapper : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        RushTheme(theme = Theme()) {
            // Screens read the window size class, so previews need a sensible default.
            CompositionLocalProvider(
                LocalWindowSizeClass provides WindowSizeClass(minWidthDp = 412, minHeightDp = 915),
                content = content,
            )
        }
    }
}
