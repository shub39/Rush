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

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shub39.rush.shared.core.enums.LyricsAlignment
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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
    fun viewportSubtractsThePaddingOfEverySide() {
        val viewport =
            fitViewport(
                pageWidth = 1000,
                pageHeight = 2000,
                startPadding = 10,
                endPadding = 20,
                topPadding = 30,
                bottomPadding = 40,
                linePadding = 50,
            )

        assertEquals(FitViewport(textWidth = 920, listHeight = 1930), viewport)
    }

    @Test
    fun viewportIsNullWithoutAnyWidthLeft() {
        assertNull(viewport(pageWidth = 100, startPadding = 50, endPadding = 40, linePadding = 10))
    }

    @Test
    fun viewportIsNullWithoutAnyHeightLeft() {
        assertNull(viewport(pageHeight = 100, topPadding = 60, bottomPadding = 40))
    }

    @Test
    fun bottomPaddingKeepsClearOfTheSystemBars() {
        assertEquals(48.dp + FitEdgePadding, fitBottomPadding(systemInset = 48.dp))
    }

    @Test
    fun bottomPaddingKeepsClearOfTheOverlay() {
        assertEquals(100.dp, fitBottomPadding(systemInset = 24.dp, overlayClearance = 100.dp))
    }

    @Test
    fun bottomPaddingDoesNotAddTheOverlayToTheSystemBars() {
        // The overlay is positioned from the bottom of the page, not from above the system bars
        assertEquals(
            200.dp + FitEdgePadding,
            fitBottomPadding(systemInset = 200.dp, overlayClearance = 100.dp),
        )
    }

    @Test
    fun overlayClearanceCoversTheCardAndWhatIsBelowIt() {
        val expected = RushOverlayBottomPadding + 52.dp + FitEdgePadding

        assertEquals(expected, rushOverlayClearance(true, SearchState.Searching("song"), 52.dp))
        assertEquals(expected, rushOverlayClearance(true, SearchState.UserPrompt, 52.dp))
    }

    @Test
    fun overlayClearanceIsZeroWithoutACard() {
        assertEquals(0.dp, rushOverlayClearance(true, SearchState.Idle, 52.dp))
        assertEquals(0.dp, rushOverlayClearance(false, SearchState.UserPrompt, 52.dp))
        assertEquals(0.dp, rushOverlayClearance(true, SearchState.UserPrompt, 0.dp))
    }

    @Test
    fun aVisibleOverlayLowersTheFittedFontSize() {
        // At a density of 1, so that a dp is a pixel
        fun fittedSize(overlayClearance: Dp): Float {
            val bottomPadding = fitBottomPadding(0.dp, overlayClearance).value.toInt()
            val listHeight = viewport(pageHeight = 500, bottomPadding = bottomPadding)!!.listHeight

            return largestFittingFontSize(min = 14f, max = 128f, step = 0.5f) { size ->
                linesFitHeight(List(10) { it }, gap = 0, maxHeight = listHeight) {
                    ceil(size).toInt()
                }
            }
        }

        val clearance = rushOverlayClearance(true, SearchState.UserPrompt, 52.dp)

        assertEquals(49f, fittedSize(0.dp))
        assertEquals(40f, fittedSize(clearance))
    }

    @Test
    fun lineHeightOfALyricOnlyIgnoresTheRomanizationGap() {
        assertEquals(
            52,
            fitLineHeight(
                lyricHeight = 50,
                romanizedHeight = null,
                verticalPadding = 2,
                romanizedGap = 4,
            ),
        )
    }

    @Test
    fun lineHeightIncludesTheRomanizationAndItsGap() {
        assertEquals(
            86,
            fitLineHeight(
                lyricHeight = 50,
                romanizedHeight = 30,
                verticalPadding = 2,
                romanizedGap = 4,
            ),
        )
    }

    @Test
    fun linesFitWhenTheyExactlyFillTheHeight() {
        assertTrue(linesFitHeight(listOf(100, 50, 25), gap = 10, maxHeight = 195) { it })
    }

    @Test
    fun linesOverflowOnePixelPastTheHeight() {
        assertFalse(linesFitHeight(listOf(100, 50, 25), gap = 10, maxHeight = 194) { it })
    }

    @Test
    fun gapsCanBeWhatMakesLinesOverflow() {
        val lines = listOf(100, 100)

        assertTrue(linesFitHeight(lines, gap = 0, maxHeight = 200) { it })
        assertFalse(linesFitHeight(lines, gap = 1, maxHeight = 200) { it })
    }

    @Test
    fun aSingleLineHasNoGap() {
        assertTrue(linesFitHeight(listOf(100), gap = 50, maxHeight = 100) { it })
    }

    @Test
    fun noLinesAlwaysFit() {
        assertTrue(linesFitHeight(emptyList<Int>(), gap = 10, maxHeight = 1) { it })
    }

    @Test
    fun stopsMeasuringAtTheFirstLineThatOverflows() {
        val measured = mutableListOf<Int>()

        val fits =
            linesFitHeight(List(100) { it }, gap = 0, maxHeight = 250) { line ->
                measured += line
                100
            }

        assertFalse(fits)
        assertEquals(listOf(0, 1, 2), measured)
    }

    @Test
    fun measuresEveryLineWhenTheyFit() {
        var measured = 0

        val fits =
            linesFitHeight(List(100) { it }, gap = 0, maxHeight = 1000) {
                measured++
                10
            }

        assertTrue(fits)
        assertEquals(100, measured)
    }

    @Test
    fun aWordWiderThanTheLineWrapsInsteadOfShrinkingTheLyrics() {
        val pageWidth = 300
        val wordLength = 20
        // The largest size at which the word still fits on one line
        val oneLineLimit = pageWidth / (wordLength * WrappingCharWidth)

        val size =
            largestFittingFontSize(min = 14f, max = 128f, step = 0.5f) { candidate ->
                linesFitHeight(listOf(wordLength), gap = 0, maxHeight = 500) { length ->
                    wrappedHeight(length, candidate, pageWidth)
                }
            }

        assertTrue(size > oneLineLimit, "$size is not above $oneLineLimit")
        assertTrue(wrappedHeight(wordLength, size, pageWidth) <= 500)
        assertTrue(wrappedHeight(wordLength, size + 0.5f, pageWidth) > 500)
    }

    private fun viewport(
        pageWidth: Int = 1000,
        pageHeight: Int = 1000,
        startPadding: Int = 0,
        endPadding: Int = 0,
        topPadding: Int = 0,
        bottomPadding: Int = 0,
        linePadding: Int = 0,
    ) =
        fitViewport(
            pageWidth = pageWidth,
            pageHeight = pageHeight,
            startPadding = startPadding,
            endPadding = endPadding,
            topPadding = topPadding,
            bottomPadding = bottomPadding,
            linePadding = linePadding,
        )

    /**
     * Height of [length] monospaced characters when broken to fit in [width], like Compose does.
     */
    private fun wrappedHeight(length: Int, size: Float, width: Int): Int {
        val wrappedLines = ceil(length * size * WrappingCharWidth / width).toInt()

        return wrappedLines * ceil(size * 1.2f).toInt()
    }

    private companion object {
        const val WrappingCharWidth = 0.6f
    }
}
