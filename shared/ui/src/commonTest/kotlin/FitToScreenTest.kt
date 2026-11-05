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
package com.shub39.rush.shared.ui.lyrics

import com.shub39.rush.shared.core.enums.LyricsAlignment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FitToScreenTest {
    @Test
    fun picksTheLargestSizeThatFits() {
        val size = largestFittingFontSize(min = 16f, max = 128f, step = 0.5f) { it <= 41.5f }

        assertEquals(41.5f, size)
    }

    @Test
    fun roundsDownToAStepWhenTheLimitIsBetweenSteps() {
        val size = largestFittingFontSize(min = 16f, max = 128f, step = 0.5f) { it <= 41.7f }

        assertEquals(41.5f, size)
    }

    @Test
    fun usesTheMaximumWhenEverythingFits() {
        assertEquals(128f, largestFittingFontSize(min = 16f, max = 128f, step = 0.5f) { true })
    }

    @Test
    fun usesTheMinimumWhenNothingFits() {
        assertEquals(16f, largestFittingFontSize(min = 16f, max = 128f, step = 0.5f) { false })
    }

    @Test
    fun usesTheMinimumWhenTheRangeIsEmpty() {
        assertEquals(16f, largestFittingFontSize(min = 16f, max = 10f, step = 0.5f) { true })
    }

    @Test
    fun neverAsksAboutSizesOutsideTheRange() {
        val asked = mutableListOf<Float>()

        largestFittingFontSize(min = 16f, max = 128f, step = 0.5f) {
            asked += it
            it <= 60f
        }

        assertTrue(asked.isNotEmpty())
        assertTrue(asked.all { it in 16f..128f })
        // Measuring a song is expensive, the search has to stay logarithmic
        assertTrue(asked.size <= 8, "asked ${asked.size} times")
    }

    @Test
    fun scalesLineHeightAndLetterSpacingWithTheFontSize() {
        val prefs =
            TextPrefs(
                fontSize = 20f,
                lineHeight = 30f,
                letterSpacing = 1f,
                lyricsAlignment = LyricsAlignment.END,
            )

        assertEquals(
            TextPrefs(
                fontSize = 40f,
                lineHeight = 60f,
                letterSpacing = 2f,
                lyricsAlignment = LyricsAlignment.END,
            ),
            prefs.scaledToFontSize(40f),
        )
    }

    @Test
    fun scalingToTheCurrentSizeChangesNothing() {
        val prefs = TextPrefs(fontSize = 28f, lineHeight = 32f, letterSpacing = -1f)

        assertEquals(prefs, prefs.scaledToFontSize(28f))
    }

    @Test
    fun scalingFromAnInvalidFontSizeKeepsTheOtherValues() {
        val prefs = TextPrefs(fontSize = 0f, lineHeight = 32f, letterSpacing = 1f)

        assertEquals(prefs.copy(fontSize = 20f), prefs.scaledToFontSize(20f))
    }

    @Test
    fun stackedHeightAddsGapsOnlyBetweenItems() {
        assertEquals(100 + 50 + 25 + 2 * 10, stackedHeight(listOf(100, 50, 25), gap = 10))
    }

    @Test
    fun stackedHeightOfASingleItemHasNoGap() {
        assertEquals(100, stackedHeight(listOf(100), gap = 10))
    }

    @Test
    fun stackedHeightOfNothingIsZero() {
        assertEquals(0, stackedHeight(emptyList(), gap = 10))
    }
}
