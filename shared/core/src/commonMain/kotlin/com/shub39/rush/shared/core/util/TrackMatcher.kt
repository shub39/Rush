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

import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.getMainArtist
import com.shub39.rush.shared.core.getMainTitle
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** Scores provider search results against the track we are looking for. */
object TrackMatcher {
    /** Results scoring below this are considered another song. */
    const val MIN_SCORE = 0.55

    private val accents =
        mapOf(
            'à' to "a",
            'á' to "a",
            'â' to "a",
            'ã' to "a",
            'ä' to "a",
            'å' to "a",
            'ç' to "c",
            'è' to "e",
            'é' to "e",
            'ê' to "e",
            'ë' to "e",
            'ì' to "i",
            'í' to "i",
            'î' to "i",
            'ï' to "i",
            'ñ' to "n",
            'ò' to "o",
            'ó' to "o",
            'ô' to "o",
            'õ' to "o",
            'ö' to "o",
            'ø' to "o",
            'ù' to "u",
            'ú' to "u",
            'û' to "u",
            'ü' to "u",
            'ý' to "y",
            'ÿ' to "y",
            'œ' to "oe",
            'æ' to "ae",
            'ß' to "ss",
        )

    fun normalize(text: String): String =
        buildString {
                text.lowercase().forEach { char ->
                    when {
                        char in accents -> append(accents.getValue(char))
                        char.isLetterOrDigit() -> append(char)
                        else -> append(' ')
                    }
                }
            }
            .split(' ')
            .filter { it.isNotEmpty() }
            .joinToString(" ")

    /** Similarity between 0 and 1 of two titles or artist names. */
    fun similarity(a: String, b: String): Double {
        val first = normalize(a)
        val second = normalize(b)
        if (first.isEmpty() || second.isEmpty()) return 0.0
        if (first == second) return 1.0

        val tokensA = first.split(' ').toSet()
        val tokensB = second.split(' ').toSet()
        val jaccard =
            tokensA.intersect(tokensB).size.toDouble() / tokensA.union(tokensB).size.toDouble()

        val containment =
            if (first.contains(second) || second.contains(first)) {
                0.6 + 0.3 * min(first.length, second.length) / max(first.length, second.length)
            } else 0.0

        return max(jaccard, containment)
    }

    fun score(query: TrackQuery, title: String, artist: String, durationMs: Long? = null): Double {
        val titleScore =
            max(
                similarity(query.title, title),
                similarity(getMainTitle(query.title), getMainTitle(title)),
            )
        val artistScore =
            maxOf(
                similarity(query.artist, artist),
                similarity(getMainArtist(query.artist), getMainArtist(artist)),
                // "A & B" against "B, A" or "A/B"
                if (normalize(artist).contains(normalize(getMainArtist(query.artist)))) 0.8 else 0.0,
            )

        val durationDiff =
            if (query.durationMs != null && durationMs != null && durationMs > 0) {
                abs(query.durationMs - durationMs) / 1000.0
            } else null

        // Same title from an unrelated artist: only trust it when durations agree
        if (artistScore < 0.3 && (durationDiff == null || durationDiff > 3)) return 0.0

        val durationScore =
            when {
                durationDiff == null -> 0.0
                durationDiff <= 3 -> 0.1
                durationDiff <= 10 -> 0.0
                durationDiff <= 30 -> -0.15
                else -> -0.4
            }

        return 0.65 * titleScore + 0.35 * artistScore + durationScore
    }

    /** Best scoring item above [MIN_SCORE], or null. */
    fun <T> best(
        query: TrackQuery,
        items: List<T>,
        title: (T) -> String,
        artist: (T) -> String,
        durationMs: (T) -> Long? = { null },
    ): T? = ranked(query, items, title, artist, durationMs).firstOrNull()

    /** Items above [MIN_SCORE], best first. */
    fun <T> ranked(
        query: TrackQuery,
        items: List<T>,
        title: (T) -> String,
        artist: (T) -> String,
        durationMs: (T) -> Long? = { null },
    ): List<T> =
        items
            .map { it to score(query, title(it), artist(it), durationMs(it)) }
            .filter { it.second >= MIN_SCORE }
            .sortedByDescending { it.second }
            .map { it.first }
}
