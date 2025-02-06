package com.shub39.rush.core.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color

fun openLinkInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

fun sortMapByKeys(map: Map<Int, String>): Map<Int, String> {
    val sortedEntries = map.entries.toList().sortedBy { it.key }
    val sortedMap = LinkedHashMap<Int, String>()
    for (entry in sortedEntries) {
        sortedMap[entry.key] = entry.value
    }
    return sortedMap
}

fun getMainArtist(artists: String): String {
    val regex = Regex("\\s*\\(.*?\\)\\s*$")
    return artists.replace(regex, "").split(",")[0].trim()
}

fun getMainTitle(songTitle: String): String {
    val regex = Regex("\\s*\\(.*?\\)\\s*$")
    return songTitle.replace(regex, "").trim()
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

fun generateGradientColors(baseColor: Color): List<Color> {
    val complementaryColor = Color(1f - baseColor.red, 1f - baseColor.green, 1f - baseColor.blue, baseColor.alpha)

    val colors = buildList {
        for (i in 0 until 6) {
            val t = i / 7f
            val interpolatedColor = lerp(baseColor, complementaryColor, t)
            add(interpolatedColor)
        }
    }

    return colors
}