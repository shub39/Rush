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
package com.shub39.rush.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint as AndroidPaint

fun AndroidPaint.applyShadowLayer(
    radius: Dp,
    xShifting: Dp,
    yShifting: Dp,
    shadowColor: Color,
    density: Density, // TODO: make it a context parameter
): AndroidPaint = apply {
    with(density) {
        setShadowLayer(radius.toPx(), xShifting.toPx(), yShifting.toPx(), shadowColor.toArgb())
    }
}

/** Creates a "glowing" background of specified [shape] and [color] */
fun Modifier.glowBackground(
    radius: Dp,
    shape: Shape,
    color: Color,
    xShifting: Dp = 0.dp,
    yShifting: Dp = 0.dp,
): Modifier = drawBehind {
    val path =
        when (val outline = shape.createOutline(size, layoutDirection, Density(density))) {
            is Outline.Generic -> outline.path
            is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
            is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        }
    val density = Density(density)
    drawContext.canvas.nativeCanvas.apply {
        drawPath(
            path.asAndroidPath(),
            Paint()
                .apply { this.color = Color.Transparent }
                .asFrameworkPaint()
                .applyShadowLayer(radius, xShifting, yShifting, color, density),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PreviewGlowText() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowRadius by
        infiniteTransition.animateFloat(
            initialValue = 48f,
            targetValue = 8f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "glow",
        )

    Text(
        text = "Kotlinism is a perfect disease",
        style = TextStyle(color = Color(0xFF8B00FF), fontSize = 24.sp),
        modifier =
            Modifier.padding(32.dp)
                .glowBackground(glowRadius.dp, shape = RectangleShape, color = Color.Cyan),
    )
}
