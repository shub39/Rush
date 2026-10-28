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
package com.shub39.rush.shared.core.matching

import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.SongMeta

/**
 * Scores a candidate [SearchResult] against the track a media session is actually playing.
 *
 * Kept free of platform/UI dependencies so the matching rules are unit tested once and shared by
 * rush mode and manual search.
 */
object SongMatcher {
    /** Minimum score a candidate must reach to be considered the same track. */
    const val MIN_SCORE = 0.55

    private const val TITLE_EXACT = 0.50
    private const val TITLE_CONTAINS = 0.35
    private const val TITLE_FUZZY = 0.50

    // When the player reports no artist, the title carries the whole decision.
    private const val TITLE_EXACT_NO_ARTIST = 0.85
    private const val TITLE_CONTAINS_NO_ARTIST = 0.60
    private const val TITLE_FUZZY_NO_ARTIST = 0.85

    private const val ARTIST_EXACT = 0.35
    private const val ARTIST_PARTIAL = 0.15
    private const val ALBUM_EXACT = 0.15

    private val BRACKETS = Regex("""[(\[].*?[)\]】]""")
    private val SUFFIX =
        Regex(
            """\s-\s(?:remaster|remastered|radio edit|single version|album version|live|acoustic|explicit|deluxe|mono|stereo|bonus track|edit|version).*$""",
            RegexOption.IGNORE_CASE,
        )
    private val FEATURING =
        Regex("""\b(feat|feat\.|ft|ft\.|featuring)\b.*$""", RegexOption.IGNORE_CASE)
    private val NON_WORD = Regex("""[^\p{L}\p{N}\s]""")
    private val WHITESPACE = Regex("""\s+""")
    private val ARTIST_SEPARATOR =
        Regex("""\s*[,&;/]\s*|\s+(?:feat\.?|ft\.?|featuring)\s+""", RegexOption.IGNORE_CASE)

    /**
     * Strips the noise that streaming services add to track titles (`feat.` credits, `(Remastered
     * 2011)`, `- Radio Edit`, punctuation) and case folds the result.
     */
    fun normalize(raw: String): String =
        raw.replace(BRACKETS, " ")
            .replace(SUFFIX, " ")
            .replace(FEATURING, " ")
            .replace(NON_WORD, " ")
            .replace(WHITESPACE, " ")
            .trim()
            .lowercase()

    /** Splits a multi-artist credit (`"A, B & C"`) into normalized individual names. */
    fun artists(raw: String): List<String> =
        raw.split(ARTIST_SEPARATOR).map(::normalize).filter(String::isNotBlank)

    /** How well [result] matches [meta]. Range `0.0..1.0`. */
    fun score(meta: SongMeta, result: SearchResult): Double {
        val metaTitle = normalize(meta.title)
        val metaArtist = meta.artist?.let(::normalize)?.takeIf(String::isNotBlank)

        var score = titleScore(metaTitle, normalize(result.title), metaArtist != null)

        if (metaArtist != null) {
            val candidates = artists(result.artist)

            score +=
                when {
                    candidates.isEmpty() -> 0.0
                    candidates.any { it == metaArtist } -> ARTIST_EXACT
                    candidates.any { it.contains(metaArtist) || metaArtist.contains(it) } ->
                        ARTIST_PARTIAL
                    else -> 0.0
                }
        }

        val metaAlbum = meta.album?.let(::normalize)?.takeIf(String::isNotBlank)
        val resultAlbum = result.album?.let(::normalize)?.takeIf(String::isNotBlank)

        if (metaAlbum != null && resultAlbum != null && metaAlbum == resultAlbum) {
            score += ALBUM_EXACT
        }

        return score.coerceIn(0.0, 1.0)
    }

    /**
     * Returns the best candidate scoring at least [minScore], or `null` when the playing track
     * cannot be resolved confidently.
     */
    fun best(
        meta: SongMeta,
        results: List<SearchResult>,
        minScore: Double = MIN_SCORE,
    ): SearchResult? {
        var best: SearchResult? = null
        var bestScore = Double.NEGATIVE_INFINITY

        for (result in results) {
            val score = score(meta, result)

            if (score > bestScore) {
                bestScore = score
                best = result
            }
        }

        return if (best != null && bestScore >= minScore) best else null
    }

    private fun titleScore(
        metaTitle: String,
        candidateTitle: String,
        artistKnown: Boolean,
    ): Double {
        if (metaTitle.isBlank() || candidateTitle.isBlank()) return 0.0

        val exact = if (artistKnown) TITLE_EXACT else TITLE_EXACT_NO_ARTIST
        val contains = if (artistKnown) TITLE_CONTAINS else TITLE_CONTAINS_NO_ARTIST
        val fuzzy = if (artistKnown) TITLE_FUZZY else TITLE_FUZZY_NO_ARTIST

        if (metaTitle == candidateTitle) return exact
        if (metaTitle.contains(candidateTitle) || candidateTitle.contains(metaTitle)) {
            return contains
        }

        val metaTokens = metaTitle.split(" ").filter(String::isNotBlank).toSet()
        val candidateTokens = candidateTitle.split(" ").filter(String::isNotBlank).toSet()
        val union = metaTokens.union(candidateTokens).size

        if (union == 0) return 0.0

        val intersection = metaTokens.intersect(candidateTokens).size.toDouble()

        return (intersection / union) * fuzzy
    }
}
