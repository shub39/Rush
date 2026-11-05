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

/** Smallest size fit to screen goes down to, below this the lyrics scroll instead. */
const val FIT_MIN_FONT_SIZE = 14f

/** Upper bound for very short lyrics, where a line would otherwise fill the screen. */
const val FIT_MAX_FONT_SIZE = 128f

/** Resolution of the search, finer steps are not visible and only cost more measuring. */
const val FIT_FONT_SIZE_STEP = 0.5f

/**
 * Binary search for the largest size in [min]..[max], in increments of [step], accepted by [fits].
 * Assumes that once a size stops fitting, larger ones do not fit either. Falls back to [min] when
 * nothing fits, [fits] is never consulted about [min] itself.
 */
fun largestFittingFontSize(
    min: Float = FIT_MIN_FONT_SIZE,
    max: Float = FIT_MAX_FONT_SIZE,
    step: Float = FIT_FONT_SIZE_STEP,
    fits: (Float) -> Boolean,
): Float {
    var low = 0
    var high = ((max - min) / step).toInt()

    while (low < high) {
        val middle = (low + high + 1) / 2
        if (fits(min + middle * step)) low = middle else high = middle - 1
    }

    return min + low * step
}

/** These preferences at [fontSize], keeping the proportions the user picked. */
fun TextPrefs.scaledToFontSize(fontSize: Float): TextPrefs {
    if (this.fontSize <= 0f) return copy(fontSize = fontSize)

    val scale = fontSize / this.fontSize

    return copy(
        fontSize = fontSize,
        lineHeight = lineHeight * scale,
        letterSpacing = letterSpacing * scale,
    )
}

/** Total height of items laid out one below the other with [gap] between them. */
fun stackedHeight(itemHeights: List<Int>, gap: Int): Int =
    itemHeights.sum() + gap * (itemHeights.size - 1).coerceAtLeast(0)
