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

import androidx.compose.runtime.Composable
import com.shub39.rush.shared.core.dataclasses.Theme

/**
 * A Composable function that applies a dynamic Material You theme to its content.
 *
 * @param theme The [Theme] data class containing all the user's selected theming options, such as
 *   the seed color, light/dark mode preference, color style, and font.
 * @param content The Composable content to which this theme will be applied.
 *
 * Thanks to nsh07!
 */
@Composable expect fun RushTheme(theme: Theme, content: @Composable () -> Unit)
