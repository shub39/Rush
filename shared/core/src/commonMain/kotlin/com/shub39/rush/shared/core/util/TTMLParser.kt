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
package com.shub39.rush.shared.core.util

import com.shub39.rush.shared.core.dataclasses.ParsedLine
import com.shub39.rush.shared.core.dataclasses.ParsedWord
import com.shub39.rush.shared.core.dataclasses.SpanInfo

object TTMLParser {

    fun isValidTTML(ttml: String): Boolean {
        val parsedLines = parseTTML(ttml)
        return parsedLines.any { it.words.isNotEmpty() }
    }

    private fun Map<String, String>.getAttributeByLocalName(localName: String): String {
        return this[localName]
            ?: this["ttm:$localName"]
            ?: this.entries.find { it.key.endsWith(":$localName") }?.value
            ?: ""
    }

    private fun parseAttributes(attrStr: String): Map<String, String> {
        val attrs = mutableMapOf<String, String>()
        val attrRegex = Regex("""([\w:]+)="([^"]*)"""")
        attrRegex.findAll(attrStr).forEach { match ->
            attrs[match.groupValues[1]] = unescapeHtml(match.groupValues[2])
        }
        return attrs
    }

    fun parseTTML(ttml: String): List<ParsedLine> {
        val lines = mutableListOf<ParsedLine>()

        try {
            // Using (?s) flag for DOT_MATCHES_ALL as it's more cross-platform compatible in some
            // Kotlin versions
            val pRegex = Regex("""(?s)<p\s+([^>]*)>(.*?)</p>""")
            val pMatches = pRegex.findAll(ttml)

            for (pMatch in pMatches) {
                val pAttributesStr = pMatch.groupValues[1]
                val pContent = pMatch.groupValues[2]

                val pAttributes = parseAttributes(pAttributesStr)
                val begin = pAttributes["begin"] ?: ""
                if (begin.isEmpty()) continue

                val startTime = parseTime(begin)
                val spanInfos = mutableListOf<SpanInfo>()
                val backgroundLines = mutableListOf<ParsedLine>()

                val agent = pAttributes.getAttributeByLocalName("agent").ifEmpty { null }

                val childRegex = Regex("""(?s)<span\s+([^>]*)>(.*?)</span>|([^<]+)""")
                val children = childRegex.findAll(pContent)

                for (child in children) {
                    val spanAttrStr = child.groupValues[1]
                    val spanContent = child.groupValues[2]

                    if (spanAttrStr.isNotEmpty()) {
                        val spanAttributes = parseAttributes(spanAttrStr)
                        val role = spanAttributes.getAttributeByLocalName("role")

                        when (role) {
                            "x-bg" -> {
                                val bgLine =
                                    parseBackgroundSpan(spanAttributes, spanContent, startTime)
                                if (bgLine != null) {
                                    backgroundLines.add(bgLine)
                                }
                            }
                            "x-translation",
                            "x-roman" -> {
                                // Skip
                            }
                            else -> {
                                val wordBegin = spanAttributes["begin"] ?: ""
                                val wordEnd = spanAttributes["end"] ?: ""
                                val wordText = unescapeHtml(spanContent.trim())

                                if (
                                    wordText.isNotEmpty() &&
                                        wordBegin.isNotEmpty() &&
                                        wordEnd.isNotEmpty()
                                ) {
                                    val nextPart = pContent.substring(child.range.last + 1)
                                    val hasTrailingSpace =
                                        nextPart.takeWhile { it != '<' }.contains(Regex("\\s"))

                                    spanInfos.add(
                                        SpanInfo(
                                            text = wordText,
                                            startTime = parseTime(wordBegin),
                                            endTime = parseTime(wordEnd),
                                            hasTrailingSpace = hasTrailingSpace,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                val words = mergeSpansIntoWords(spanInfos)
                val lineText = words.joinToString(" ") { it.text }
                val finalText = lineText.ifEmpty { getDirectTextContent(pContent).trim() }

                if (finalText.isNotEmpty()) {
                    lines.add(
                        ParsedLine(
                            text = finalText,
                            startTime = startTime,
                            words = words,
                            agent = agent,
                            isBackground = false,
                            backgroundLines = backgroundLines,
                        )
                    )
                }
            }
        } catch (_: Exception) {
            return emptyList()
        }

        return buildList {
            if (lines.firstOrNull()?.let { it.startTime > 5.0 } == true) {
                add(ParsedLine(text = "", startTime = 0.0, words = emptyList()))
            }
            lines.forEachIndexed { index, line ->
                add(line)
                val nextLine = lines.getOrNull(index + 1)
                if (nextLine != null) {
                    val currentLineEnd = line.words.lastOrNull()?.endTime ?: line.startTime
                    val nextLineStart = nextLine.startTime
                    if (nextLineStart - currentLineEnd > 5.0) {
                        add(ParsedLine(text = "", startTime = currentLineEnd, words = emptyList()))
                    }
                }
            }
        }
    }

    private fun parseBackgroundSpan(
        attributes: Map<String, String>,
        content: String,
        parentStartTime: Double,
    ): ParsedLine? {
        val bgBegin = attributes["begin"] ?: ""
        val bgStartTime = if (bgBegin.isNotEmpty()) parseTime(bgBegin) else parentStartTime

        val spanInfos = mutableListOf<SpanInfo>()
        val childRegex = Regex("""(?s)<span\s+([^>]*)>(.*?)</span>|([^<]+)""")
        val children = childRegex.findAll(content)

        for (child in children) {
            val spanAttrStr = child.groupValues[1]
            val spanContent = child.groupValues[2]

            if (spanAttrStr.isNotEmpty()) {
                val spanAttributes = parseAttributes(spanAttrStr)
                val role = spanAttributes.getAttributeByLocalName("role")

                if (role == "x-translation" || role == "x-roman") continue

                val wordBegin = spanAttributes["begin"] ?: ""
                val wordEnd = spanAttributes["end"] ?: ""
                val wordText = unescapeHtml(spanContent.trim())

                if (wordText.isNotEmpty() && wordBegin.isNotEmpty() && wordEnd.isNotEmpty()) {
                    val nextPart = content.substring(child.range.last + 1)
                    val hasTrailingSpace = nextPart.takeWhile { it != '<' }.contains(Regex("\\s"))

                    spanInfos.add(
                        SpanInfo(
                            text = wordText,
                            startTime = parseTime(wordBegin),
                            endTime = parseTime(wordEnd),
                            hasTrailingSpace = hasTrailingSpace,
                        )
                    )
                }
            }
        }

        val words = mergeSpansIntoWords(spanInfos)
        val lineText = words.joinToString(" ") { it.text }
        val finalText = lineText.ifEmpty { getDirectTextContent(content).trim() }

        return if (finalText.isNotEmpty()) {
            ParsedLine(
                text = finalText,
                startTime = bgStartTime,
                words = words,
                agent = null,
                isBackground = true,
                backgroundLines = emptyList(),
            )
        } else null
    }

    private fun getDirectTextContent(content: String): String {
        val spanRegex = Regex("""(?s)<span\s+([^>]*)>(.*?)</span>""")
        val sb = StringBuilder()
        var lastIndex = 0
        spanRegex.findAll(content).forEach { match ->
            sb.append(content.substring(lastIndex, match.range.first))

            val attrStr = match.groupValues[1]
            val spanContent = match.groupValues[2]
            val attrs = parseAttributes(attrStr)
            val role = attrs.getAttributeByLocalName("role")

            if (role != "x-bg" && role != "x-translation" && role != "x-roman") {
                sb.append(getDirectTextContent(spanContent))
            }
            lastIndex = match.range.last + 1
        }
        sb.append(content.substring(lastIndex))
        return unescapeHtml(sb.toString().replace(Regex("""<[^>]+>"""), ""))
    }

    private fun mergeSpansIntoWords(spanInfos: List<SpanInfo>): List<ParsedWord> {
        if (spanInfos.isEmpty()) return emptyList()

        val words = mutableListOf<ParsedWord>()
        var currentText = StringBuilder()
        var currentStartTime = spanInfos[0].startTime
        var currentEndTime = spanInfos[0].endTime

        for ((index, span) in spanInfos.withIndex()) {
            if (index == 0) {
                currentText.append(span.text)
                currentStartTime = span.startTime
                currentEndTime = span.endTime
            } else {
                val prevSpan = spanInfos[index - 1]
                if (prevSpan.hasTrailingSpace) {
                    if (currentText.isNotEmpty()) {
                        words.add(
                            ParsedWord(
                                text = currentText.toString().trim(),
                                startTime = currentStartTime,
                                endTime = currentEndTime,
                            )
                        )
                    }
                    currentText = StringBuilder(span.text)
                    currentStartTime = span.startTime
                    currentEndTime = span.endTime
                } else {
                    currentText.append(span.text)
                    currentEndTime = span.endTime
                }
            }
        }

        if (currentText.isNotEmpty()) {
            words.add(
                ParsedWord(
                    text = currentText.toString().trim(),
                    startTime = currentStartTime,
                    endTime = currentEndTime,
                )
            )
        }

        return words
    }

    fun toLRC(ttml: String): String {
        val lines = parseTTML(ttml)

        return buildString {
            lines.forEach { line ->
                val timeMs = (line.startTime * 1000).toLong()
                val minutes = timeMs / 60000
                val seconds = (timeMs % 60000) / 1000
                val centiseconds = (timeMs % 1000) / 10

                appendLine(
                    "[${minutes.pad(2)}:${seconds.pad(2)}.${centiseconds.pad(2)}]${line.text}"
                )

                if (line.words.isNotEmpty()) {
                    val wordsData =
                        line.words.joinToString("|") { word ->
                            "${word.text}:${word.startTime}:${word.endTime}"
                        }
                    appendLine("<$wordsData>")
                }

                line.backgroundLines.forEach { bgLine ->
                    val bgTimeMs = (bgLine.startTime * 1000).toLong()
                    val bgMinutes = bgTimeMs / 60000
                    val bgSeconds = (bgTimeMs % 60000) / 1000
                    val bgCentiseconds = (bgTimeMs % 1000) / 10

                    appendLine(
                        "[${bgMinutes.pad(2)}:${bgSeconds.pad(2)}.${bgCentiseconds.pad(2)}]{bg}${bgLine.text}"
                    )

                    if (bgLine.words.isNotEmpty()) {
                        val bgWordsData =
                            bgLine.words.joinToString("|") { word ->
                                "${word.text}:${word.startTime}:${word.endTime}"
                            }
                        appendLine("<$bgWordsData>")
                    }
                }
            }
        }
    }

    private fun Long.pad(length: Int): String = this.toString().padStart(length, '0')

    private fun parseTime(timeStr: String): Double {
        return try {
            when {
                timeStr.contains(":") -> {
                    val parts = timeStr.split(":")
                    when (parts.size) {
                        2 -> {
                            val minutes = parts[0].toDouble()
                            val seconds = parts[1].toDouble()
                            minutes * 60 + seconds
                        }
                        3 -> {
                            val hours = parts[0].toDouble()
                            val minutes = parts[1].toDouble()
                            val seconds = parts[2].toDouble()
                            hours * 3600 + minutes * 60 + seconds
                        }
                        else -> timeStr.toDoubleOrNull() ?: 0.0
                    }
                }
                else -> timeStr.toDoubleOrNull() ?: 0.0
            }
        } catch (_: Exception) {
            0.0
        }
    }

    private fun unescapeHtml(text: String): String {
        if (!text.contains('&')) return text
        return text
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&apos;", "'")
            .replace("&#x27;", "'")
            .replace("&#39;", "'")
    }
}
