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
package com.shub39.rush.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A Composable that displays an animated, wavy gradient using a Skia shader.
 *
 * If shaders are not available on the device it falls back to a simple static
 * `Brush.verticalGradient` using the provided colors.
 *
 * @param modifier The [Modifier] to be applied to this Composable.
 * @param colors The list of [Color]s to be used in the gradient/shader. The shader is dynamically
 *   generated to support the number of colors provided.
 */
@Composable expect fun HypnoticVisualizer(modifier: Modifier = Modifier, colors: List<Color>)
