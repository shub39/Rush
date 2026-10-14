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

/**
 * Parser for NetEase "yrc" word by word lyrics:
 * `[lineStart,lineDuration](wordStart,wordDuration,0)word(wordStart,wordDuration,0)word...`
 *
 * Metadata lines (JSON objects) are ignored.
 */
object YrcParser {
    private val lineRegex = Regex("""^\[(\d+),(\d+)](.*)$""")
    private val wordTag = Regex("""\((\d+),(\d+),-?\d+\)""")

    fun parse(yrc: String): List<TimedLine> {
        val lines = mutableListOf<TimedLine>()

        for (rawLine in yrc.lineSequence()) {
            val match = lineRegex.matchEntire(rawLine.trim()) ?: continue
            val lineStart = match.groupValues[1].toLong()
            val lineEnd = lineStart + match.groupValues[2].toLong()
            val body = match.groupValues[3]

            val tags = wordTag.findAll(body).toList()
            if (tags.isEmpty()) continue

            val words = mutableListOf<TimedWord>()
            tags.forEachIndexed { index, tag ->
                val textEnd = tags.getOrNull(index + 1)?.range?.first ?: body.length
                val start = tag.groupValues[1].toLong()
                val end = start + tag.groupValues[2].toLong()
                words.appendSyllable(body.substring(tag.range.last + 1, textEnd), start, end)
            }

            if (words.isNotEmpty()) lines += TimedLine(lineStart, lineEnd, words)
        }

        return lines.sortedBy { it.startMs }
    }
}
