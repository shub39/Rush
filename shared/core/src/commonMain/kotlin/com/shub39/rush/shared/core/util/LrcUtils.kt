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

import com.shub39.rush.shared.core.dataclasses.Lyric

/** Lenient LRC helpers, providers often return LRC with metadata tags or several timestamps. */
object LrcUtils {
    private val timeTag = Regex("""\[(\d{1,3}):(\d{1,2})(?:[.:](\d{1,3}))?]""")
    private val offsetTag = Regex("""\[offset:\s*([+-]?\d+)\s*]""", RegexOption.IGNORE_CASE)
    private val wordTimeTag = Regex("""<\d{1,3}:\d{1,2}(?:[.:]\d{1,3})?>""")
    private val spaces = Regex("""\s+""")

    /** True when [text] contains at least one LRC time tag. */
    fun isSynced(text: String?): Boolean = text != null && timeTag.containsMatchIn(text)

    /**
     * Parses LRC lyrics into time sorted [Lyric]s. Invalid or metadata lines are skipped instead of
     * failing the whole parse, a line with several timestamps is expanded and `[offset:]` applied.
     */
    fun parse(lrc: String): List<Lyric> {
        val offset = offsetTag.find(lrc)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        val lyrics = mutableListOf<Lyric>()

        for (rawLine in lrc.lineSequence()) {
            val line = rawLine.trim()
            if (!line.startsWith("[")) continue

            val times = mutableListOf<Long>()
            var index = 0
            while (true) {
                val match = timeTag.find(line, index)
                if (match == null || match.range.first != index) break
                times += toMillis(match)
                index = match.range.last + 1
            }
            if (times.isEmpty()) continue

            val text = wordTimeTag.replace(line.substring(index), "").replace(spaces, " ").trim()
            times.forEach { lyrics += Lyric(time = (it - offset).coerceAtLeast(0), text = text) }
        }

        return lyrics.sortedBy { it.time }.distinctBy { it.time }
    }

    /** Returns clean `[mm:ss.xx]text` LRC, or null when [raw] holds no timed lyric. */
    fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val lyrics = parse(raw)
        if (lyrics.none { it.text.isNotBlank() }) return null
        return lyrics.joinToString("\n") { "[${formatTime(it.time)}]${it.text}" }
    }

    /** Builds LRC from word timed lines. */
    fun fromTimedLines(lines: List<TimedLine>): String? {
        val valid = lines.filter { it.text.isNotBlank() }
        if (valid.isEmpty()) return null
        return valid.joinToString("\n") { "[${formatTime(it.startMs)}]${it.text}" }
    }

    /** Strips timestamps, keeping a single empty line between paragraphs. */
    fun toPlain(lrc: String): String {
        val lines = parse(lrc).map { it.text }
        if (lines.isEmpty()) return lrc.trim()

        val result = mutableListOf<String>()
        for (line in lines) {
            if (line.isBlank() && (result.isEmpty() || result.last().isBlank())) continue
            result += line
        }
        return result.dropLastWhile { it.isBlank() }.joinToString("\n")
    }

    /** Formats milliseconds as `mm:ss.xx`. */
    fun formatTime(timeMs: Long): String {
        val minutes = timeMs / 60_000
        val seconds = (timeMs % 60_000) / 1000
        val hundredths = (timeMs % 1000) / 10
        return "${minutes.toString().padStart(2, '0')}:" +
            "${seconds.toString().padStart(2, '0')}." +
            hundredths.toString().padStart(2, '0')
    }

    private fun toMillis(match: MatchResult): Long {
        val minutes = match.groupValues[1].toLong()
        val seconds = match.groupValues[2].toLong()
        val fraction = match.groupValues[3]
        val fractionMs =
            when (fraction.length) {
                0 -> 0L
                1 -> fraction.toLong() * 100
                2 -> fraction.toLong() * 10
                else -> fraction.take(3).toLong()
            }
        return minutes * 60_000 + seconds * 1000 + fractionMs
    }
}
