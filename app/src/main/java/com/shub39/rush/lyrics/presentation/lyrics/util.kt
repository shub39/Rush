package com.shub39.rush.lyrics.presentation.lyrics

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

fun getCurrentLyricIndex(playbackPosition: Long, lyrics: List<Lyric>): Int {
    return if (lyrics.indexOfLast { it.time <= playbackPosition } < 0) {
        0
    } else {
        lyrics.indexOfLast { it.time <= playbackPosition }
    }
}