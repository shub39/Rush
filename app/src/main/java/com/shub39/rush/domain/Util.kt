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
package com.shub39.rush.domain

private val titleCleanupPatterns =
    listOf(
        Regex(
            """\s*\(.*?(official|video|audio|lyrics|lyric|visualizer|hd|hq|4k|remaster|remix|live|acoustic|version|edit|extended|radio|clean|explicit).*?\)""",
            RegexOption.IGNORE_CASE,
        ),
        Regex(
            """\s*\[.*?(official|video|audio|lyrics|lyric|visualizer|hd|hq|4k|remaster|remix|live|acoustic|version|edit|extended|radio|clean|explicit).*?]""",
            RegexOption.IGNORE_CASE,
        ),
        Regex("""\s*【.*?】"""),
        Regex("""\s*\|.*$"""),
        Regex(
            """\s*-\s*(official|video|audio|lyrics|lyric|visualizer).*$""",
            RegexOption.IGNORE_CASE,
        ),
        Regex("""\s*\(feat\..*?\)""", RegexOption.IGNORE_CASE),
        Regex("""\s*\(ft\..*?\)""", RegexOption.IGNORE_CASE),
        Regex("""\s*feat\..*$""", RegexOption.IGNORE_CASE),
        Regex("""\s*ft\..*$""", RegexOption.IGNORE_CASE),
    )
private val artistSeparators =
    listOf(
        " & ",
        " and ",
        ", ",
        " x ",
        " X ",
        " feat. ",
        " feat ",
        " ft. ",
        " ft ",
        " featuring ",
        " with ",
    )

fun getMainArtist(artists: String): String {
    var cleaned = artists.trim()
    for (separator in artistSeparators) {
        if (cleaned.contains(separator, ignoreCase = true)) {
            cleaned = cleaned.split(separator, ignoreCase = true, limit = 2)[0]
            break
        }
    }
    return cleaned.trim()
}

fun getMainTitle(songTitle: String): String {
    var cleaned = songTitle.trim()
    for (pattern in titleCleanupPatterns) {
        cleaned = cleaned.replace(pattern, "")
    }
    return cleaned.trim()
}
