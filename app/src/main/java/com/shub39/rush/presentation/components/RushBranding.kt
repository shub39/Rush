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
package com.shub39.rush.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.shub39.rush.R
import com.shub39.rush.presentation.flexFontEmphasis
import com.shub39.rush.presentation.share.fromPx
import com.shub39.rush.presentation.share.pxToDp

/**
 * A composable that displays the "Rush" application branding.
 *
 * This consists of the application icon followed by the "Rush" text. The color of both the icon and
 * the text can be customized.
 *
 * @param modifier The [Modifier] to be applied to the branding container.
 * @param color The color to be used for tinting the icon and for the text. Defaults to the primary
 *   color from the current [MaterialTheme].
 */
@Composable
fun RushBranding(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.scale(2.5f).size(pxToDp(60)),
        )

        Text(
            text = "Rush",
            style =
                MaterialTheme.typography.headlineSmall
                    .copy(color = color, fontFamily = flexFontEmphasis())
                    .fromPx(fontSize = 36, letterSpacing = 0, lineHeight = 36),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    RushBranding()
}
