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

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import kotlin.math.roundToInt

/**
 * CompositionLocal for window size class to make adaptive screens.
 *
 * This is the size of the whole window. Content rendered in a split scene pane must use
 * [PaneWindowSizeClass] instead, otherwise it lays itself out for a width it doesn't have.
 */
val LocalWindowSizeClass: ProvidableCompositionLocal<WindowSizeClass> = staticCompositionLocalOf {
    error("No window size class provided")
}

/**
 * Whether the current content is one pane of a split scene, i.e. something is displayed next to it.
 *
 * Screens use this to avoid repeating information the sibling pane already shows.
 */
val LocalInSplitPane: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

/**
 * Provides a [WindowSizeClass] derived from the size of this pane instead of the whole window.
 *
 * Screens rendered inside a split scene only get a part of the window, so they must adapt to the
 * space they actually have. Without this a screen shown in a narrow pane would still see an
 * expanded window and lay itself out for a width it doesn't have.
 */
@Composable
fun PaneWindowSizeClass(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = modifier) {
        val paneSizeClass =
            remember(maxWidth, maxHeight) {
                WindowSizeClass(
                    minWidthDp = maxWidth.value.roundToInt(),
                    minHeightDp = maxHeight.value.roundToInt(),
                )
            }

        CompositionLocalProvider(
            LocalWindowSizeClass provides paneSizeClass,
            LocalInSplitPane provides true,
        ) {
            content()
        }
    }
}
