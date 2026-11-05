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
import androidx.compose.ui.unit.max

/** Smallest size fit to screen goes down to, below this the lyrics scroll instead. */
const val FIT_MIN_FONT_SIZE = 14f

/** Upper bound for very short lyrics, where a line would otherwise fill the screen. */
const val FIT_MAX_FONT_SIZE = 128f

/** Resolution of the search, finer steps are not visible and only cost more measuring. */
const val FIT_FONT_SIZE_STEP = 0.5f

/** Space kept between the fitted lyrics and whatever is at the edge of the page. */
val FitEdgePadding = 8.dp

/** Distance between the rush mode overlay and the bottom of the page. */
val RushOverlayBottomPadding = 32.dp

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

/**
 * Where fitted lyrics have to end, measured from the bottom of the page: clear of the system bars
 * and of [overlayClearance]. Both are measured from the same edge, so the larger one decides.
 */
fun fitBottomPadding(systemInset: Dp, overlayClearance: Dp = 0.dp): Dp =
    max(systemInset + FitEdgePadding, overlayClearance)

/**
 * Distance from the bottom of the page that the rush mode overlay takes up, or nothing when it has
 * no card to show. It is not affected by the overlay fading out when the controls hide: any touch
 * brings it back, and it should neither cover the lyrics then nor resize them on every touch.
 *
 * @param overlayHeight height of the card, zero until it has been laid out for the first time.
 */
fun rushOverlayClearance(rushEnabled: Boolean, searchState: SearchState, overlayHeight: Dp): Dp =
    if (rushEnabled && searchState != SearchState.Idle && overlayHeight > 0.dp) {
        RushOverlayBottomPadding + overlayHeight + FitEdgePadding
    } else {
        0.dp
    }

/** Space for the fitted lyrics, in pixels. */
data class FitViewport(
    /** Width a line can wrap in. */
    val textWidth: Int,
    /** Height the lines have to stack in. */
    val listHeight: Int,
)

/**
 * What is left of a page for the lyrics after all of the padding around them, or null if nothing
 * is. All of the values are in pixels.
 *
 * @param linePadding horizontal padding of a single line, on both sides combined.
 */
fun fitViewport(
    pageWidth: Int,
    pageHeight: Int,
    startPadding: Int,
    endPadding: Int,
    topPadding: Int,
    bottomPadding: Int,
    linePadding: Int,
): FitViewport? {
    val textWidth = pageWidth - startPadding - endPadding - linePadding
    val listHeight = pageHeight - topPadding - bottomPadding

    return if (textWidth > 0 && listHeight > 0) FitViewport(textWidth, listHeight) else null
}

/** Height of a line, including its romanization if it has any. */
fun fitLineHeight(
    lyricHeight: Int,
    romanizedHeight: Int?,
    verticalPadding: Int,
    romanizedGap: Int,
): Int =
    verticalPadding +
        lyricHeight +
        if (romanizedHeight != null) romanizedGap + romanizedHeight else 0

/**
 * Whether [lines] fit in [maxHeight] when laid out one below the other with [gap] between them.
 *
 * Stops at the first line that overflows, as [lineHeight] lays out text: the search rejects about
 * half of the sizes it tries, and those are the largest and most expensive ones to lay out.
 */
inline fun <T> linesFitHeight(
    lines: List<T>,
    gap: Int,
    maxHeight: Int,
    lineHeight: (T) -> Int,
): Boolean {
    var total = 0

    lines.forEachIndexed { index, line ->
        total += lineHeight(line) + if (index > 0) gap else 0
        if (total > maxHeight) return false
    }

    return true
}
