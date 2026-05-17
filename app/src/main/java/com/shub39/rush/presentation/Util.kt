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

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun hypnoticAvailable() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun blurAvailable() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

suspend fun Clipboard.copyToClipboard(text: String) {
    setClipEntry(ClipEntry(ClipData.newPlainText("lyrics", text)))
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun updateSystemBars(view: View, fullscreen: Boolean) {
    val window = view.context.findActivity()?.window ?: return
    val controller = WindowCompat.getInsetsController(window, view)

    if (fullscreen) {
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        controller.show(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    }
}

fun resetSystemBars(view: View) {
    val window = view.context.findActivity()?.window ?: return
    val controller = WindowCompat.getInsetsController(window, view)
    controller.show(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
}

fun sortMapByKeys(map: Map<Int, String>): Map<Int, String> {
    val sortedEntries = map.entries.toList().sortedBy { it.key }
    val sortedMap = LinkedHashMap<Int, String>()
    for (entry in sortedEntries) {
        sortedMap[entry.key] = entry.value
    }
    return sortedMap
}

fun generateGradientColors(color1: Color, color2: Color, steps: Int = 6): List<Color> {
    val colors = buildList {
        for (i in 0 until steps) {
            val t = i / (steps - 1).toFloat()
            val interpolatedColor = lerp(color1, color2, t)
            add(interpolatedColor)
        }
    }

    return colors
}

fun lerp(color1: Color, color2: Color, t: Float): Color {
    val r = (color1.red * (1 - t) + color2.red * t).coerceIn(0f, 1f)
    val g = (color1.green * (1 - t) + color2.green * t).coerceIn(0f, 1f)
    val b = (color1.blue * (1 - t) + color2.blue * t).coerceIn(0f, 1f)
    val a = (color1.alpha * (1 - t) + color2.alpha * t).coerceIn(0f, 1f)

    return Color(r, g, b, a)
}

// fades the top of the composable to the bottom
fun Modifier.fadeTopToBottom(fadeHeightFraction: Float = 0.1f): Modifier {
    require(fadeHeightFraction in 0f..1f) {
        "fadeHeightFraction must be between 0f and 1f, got $fadeHeightFraction"
    }

    return this.graphicsLayer { alpha = 0.99f }
        .drawWithCache {
            val fadeHeight = size.height * fadeHeightFraction
            val gradient =
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    tileMode = TileMode.Clamp,
                    startY = 0f,
                    endY = fadeHeight,
                )
            onDrawWithContent {
                drawContent()
                drawRect(brush = gradient, blendMode = BlendMode.DstIn)
            }
        }
}

// reverse of above
fun Modifier.fadeBottomToTop(fadeHeightFraction: Float = 0.8f): Modifier {
    require(fadeHeightFraction in 0f..1f) {
        "fadeHeightFraction must be between 0f and 1f, got $fadeHeightFraction"
    }

    return this.graphicsLayer { alpha = 0.99f }
        .drawWithCache {
            val fadeHeight = size.height * fadeHeightFraction
            val gradient =
                Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    tileMode = TileMode.Clamp,
                    startY = size.height - fadeHeight,
                    endY = size.height,
                )
            onDrawWithContent {
                drawContent()
                drawRect(brush = gradient, blendMode = BlendMode.DstIn)
            }
        }
}

fun Modifier.rotateVertically(clockwise: Boolean = true): Modifier {
    val rotate = rotate(if (clockwise) 90f else -90f)

    val adjustBounds = layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2),
            )
        }
    }
    return rotate then adjustBounds
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}
