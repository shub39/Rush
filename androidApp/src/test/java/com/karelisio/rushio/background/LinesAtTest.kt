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
package com.karelisio.rushio.background

import org.junit.Assert.assertEquals
import org.junit.Test

class LinesAtTest {
    private val lines =
        listOf(
            TimedText(0, ""),
            TimedText(1_000, "First line"),
            TimedText(3_000, ""),
            TimedText(5_000, "Second line"),
        )

    @Test
    fun beforeTheFirstLyricTheNextLineIsShown() {
        assertEquals("" to "First line", linesAt(lines, 500))
    }

    @Test
    fun instrumentalBreaksSkipToTheNextSungLine() {
        assertEquals("First line" to "Second line", linesAt(lines, 1_200))
        assertEquals("" to "Second line", linesAt(lines, 3_500))
    }

    @Test
    fun lastLineHasNoNextLine() {
        assertEquals("Second line" to "", linesAt(lines, 60_000))
    }

    @Test
    fun negativePositionsComeFromTheSyncOffset() {
        assertEquals("" to "First line", linesAt(listOf(TimedText(1_000, "First line")), -200))
    }
}
