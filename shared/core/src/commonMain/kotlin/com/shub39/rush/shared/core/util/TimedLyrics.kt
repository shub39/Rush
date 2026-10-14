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

/** A word (or syllable) with its timing, in milliseconds. */
data class TimedWord(
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val trailingSpace: Boolean = false,
)

/** A lyrics line made of timed words, in milliseconds. */
data class TimedLine(val startMs: Long, val endMs: Long, val words: List<TimedWord>) {
    val text: String
        get() =
            buildString {
                    words.forEach { word ->
                        append(word.text)
                        if (word.trailingSpace) append(' ')
                    }
                }
                .trim()
}

/**
 * Appends a raw syllable as returned by providers. Spaces are carried by the syllables themselves
 * ("Hel", "lo ", "world"), a whitespace only syllable just ends the previous word.
 */
internal fun MutableList<TimedWord>.appendSyllable(rawText: String, startMs: Long, endMs: Long) {
    if (rawText.isBlank()) {
        if (isNotEmpty()) this[lastIndex] = last().copy(trailingSpace = true)
        return
    }
    if (rawText.first().isWhitespace() && isNotEmpty()) {
        this[lastIndex] = last().copy(trailingSpace = true)
    }
    add(
        TimedWord(
            text = rawText.trim(),
            startMs = startMs,
            endMs = endMs.coerceAtLeast(startMs),
            trailingSpace = rawText.last().isWhitespace(),
        )
    )
}

/** Builds TTML understood by [TTMLParser] from word timed lines. */
object TtmlBuilder {
    fun build(lines: List<TimedLine>): String? {
        val valid = lines.filter { it.words.isNotEmpty() && it.text.isNotBlank() }
        if (valid.isEmpty()) return null

        return buildString {
            append("<tt xmlns=\"http://www.w3.org/ns/ttml\"><body><div>")
            for (line in valid) {
                append("<p begin=\"${seconds(line.startMs)}\" end=\"${seconds(line.endMs)}\">")
                line.words.forEachIndexed { index, word ->
                    append(
                        "<span begin=\"${seconds(word.startMs)}\" end=\"${seconds(word.endMs)}\">"
                    )
                    append(escape(word.text))
                    append("</span>")
                    if (word.trailingSpace && index != line.words.lastIndex) append(' ')
                }
                append("</p>")
            }
            append("</div></body></tt>")
        }
    }

    private fun seconds(ms: Long): String =
        "${ms / 1000}.${(ms % 1000).toString().padStart(3, '0')}"

    private fun escape(text: String): String =
        text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
}
