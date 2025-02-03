package com.shub39.rush.lyrics.presentation.lyrics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.graphics.Color
import com.shub39.rush.lyrics.domain.Lyric

fun breakLyrics(lyrics: String): List<Map.Entry<Int, String>> {
    if (lyrics.isEmpty()) return emptyList()
    val lines = lyrics.lines()
    val map = mutableMapOf<Int, String>()
    for (i in lines.indices) {
        map[i] = lines[i]
    }
    return map.entries.toList()
}

fun parseLyrics(lyricsString: String): List<Lyric> {
    val seenTimes = mutableSetOf<Long>()

    return lyricsString.lines().mapNotNull { line ->
        val parts = line.split("] ")
        if (parts.size == 2) {
            val time = parts[0].removePrefix("[").split(":").let { (minutes, seconds) ->
                minutes.toLong() * 60 * 1000 + (seconds.toDouble() * 1000).toLong()
            }
            if (time in seenTimes) {
                null
            } else {
                seenTimes.add(time)
                val text = parts[1]
                Lyric(time, text)
            }
        } else {
            null
        }
    }
}

fun updateSelectedLines(
    selectedLines: Map<Int, String>,
    key: Int,
    value: String,
    maxSelections: Int = 6
): Map<Int, String> {
    return if (!selectedLines.contains(key) && selectedLines.size < maxSelections) {
        selectedLines.plus(key to value)
    } else {
        selectedLines.minus(key)
    }
}

fun copyToClipBoard(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun getCurrentLyricIndex(playbackPosition: Long, lyrics: List<Lyric>): Int {
    return if (lyrics.indexOfLast { it.time <= playbackPosition } < 0) {
        0
    } else {
        lyrics.indexOfLast { it.time <= playbackPosition }
    }
}


fun generateGradientColors(color1: Color, color2: Color, steps: Int): List<Color> {
    val colors = mutableListOf<Color>()

    for (i in 0 until steps) {
        val t = i / (steps - 1).toFloat()
        val interpolatedColor = lerp(color1, color2, t)
        colors.add(interpolatedColor)
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