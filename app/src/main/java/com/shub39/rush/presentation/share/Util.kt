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
package com.shub39.rush.presentation.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun pxToSp(px: Int): TextUnit {
    val density = LocalDensity.current
    return with(density) { px.toSp() }
}

@Composable
fun pxToDp(px: Int): Dp {
    val density = LocalDensity.current
    return with(density) { px.toDp() }
}

@Composable
fun TextStyle.fromPx(
    fontSize: Int,
    letterSpacing: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal,
): TextStyle {
    return copy(
        fontSize = pxToSp(fontSize),
        letterSpacing = pxToSp(letterSpacing),
        lineHeight = pxToSp(lineHeight),
        fontWeight = fontWeight,
    )
}
