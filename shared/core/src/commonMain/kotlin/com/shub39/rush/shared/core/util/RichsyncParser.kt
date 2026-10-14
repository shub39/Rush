/*
 * Copyright (C) 2026  Karelisio
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
package com.shub39.rush.shared.core.util

import kotlin.math.roundToLong
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Parser for Musixmatch "richsync" lyrics, a JSON list of lines: `{"ts": lineStart, "te": lineEnd,
 * "l": [{"c": "word", "o": offsetFromLineStart}], "x": "line"}` Times are in seconds.
 */
object RichsyncParser {
    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    private data class Line(val ts: Double, val te: Double, val l: List<Word> = emptyList())

    @Serializable private data class Word(val c: String, val o: Double)

    fun parse(body: String): List<TimedLine> {
        val lines =
            runCatching { json.decodeFromString<List<Line>>(body) }.getOrNull()
                ?: return emptyList()

        return lines
            .mapNotNull { line ->
                val words = mutableListOf<TimedWord>()
                line.l.forEachIndexed { index, word ->
                    val start = line.ts + word.o
                    val end = line.l.getOrNull(index + 1)?.let { line.ts + it.o } ?: line.te
                    words.appendSyllable(word.c, start.toMillis(), end.toMillis())
                }
                if (words.isEmpty()) null
                else TimedLine(line.ts.toMillis(), line.te.toMillis(), words)
            }
            .sortedBy { it.startMs }
    }

    private fun Double.toMillis(): Long = (this * 1000).roundToLong().coerceAtLeast(0)
}
