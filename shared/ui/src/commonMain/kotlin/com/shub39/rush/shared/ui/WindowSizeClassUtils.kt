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
package com.shub39.rush.shared.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/** Max width for single column content (settings, forms) so text lines stay readable. */
val ContentMaxWidth: Dp = 700.dp

/** Max width of the lyrics column on the lyrics page. */
val LyricsMaxWidth: Dp = 1100.dp

/** Width below which the lyrics page stacks the artwork above the lyrics. */
private const val LYRICS_PORTRAIT_WIDTH_BREAKPOINT = 640

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun WindowSizeClass.getWidthSize(): WindowSize {
            return if (isWidthAtLeastBreakpoint(900)) {
                EXPANDED
            } else if (isWidthAtLeastBreakpoint(480)) {
                MEDIUM
            } else {
                COMPACT
            }
        }

        fun WindowSizeClass.getHeightSize(): WindowSize {
            return if (isHeightAtLeastBreakpoint(840)) {
                EXPANDED
            } else if (isHeightAtLeastBreakpoint(600)) {
                MEDIUM
            } else {
                COMPACT
            }
        }

        fun WindowSizeClass.isWidthExpanded(): Boolean = getWidthSize() == EXPANDED

        fun WindowSizeClass.isHeightExpanded(): Boolean = getHeightSize() == EXPANDED

        fun WindowSizeClass.isWidthCompact(): Boolean = getWidthSize() == COMPACT

        fun WindowSizeClass.isHeightCompact(): Boolean = getHeightSize() == COMPACT

        fun WindowSizeClass.isWidthMedium(): Boolean = getWidthSize() == MEDIUM

        fun WindowSizeClass.isHeightMedium(): Boolean = getHeightSize() == MEDIUM

        /** True when the lyrics page should stack content instead of placing it side by side. */
        fun WindowSizeClass.isLyricsPortrait(): Boolean =
            !isWidthAtLeastBreakpoint(LYRICS_PORTRAIT_WIDTH_BREAKPOINT)

        /** Max height for dialogs and sheets so they don't fill a short window edge to edge. */
        fun WindowSizeClass.getDialogMaxHeight(): Dp =
            when (getHeightSize()) {
                COMPACT -> 560.dp
                MEDIUM -> 640.dp
                EXPANDED -> 720.dp
            }

        /** Height of card and lyrics previews shown inside sheets. */
        fun WindowSizeClass.getPreviewHeight(): Dp =
            when (getHeightSize()) {
                COMPACT -> 200.dp
                MEDIUM -> 260.dp
                EXPANDED -> 320.dp
            }

        /** Width of the sidebar in wide two pane pages. */
        fun WindowSizeClass.getSidebarWidth(): Dp =
            when (getWidthSize()) {
                COMPACT -> 300.dp
                MEDIUM -> 320.dp
                EXPANDED -> 380.dp
            }

        /** Max width of bottom sheets so they don't stretch across wide windows. */
        fun WindowSizeClass.getSheetMaxWidth(): Dp =
            when (getWidthSize()) {
                COMPACT -> 480.dp
                MEDIUM -> 600.dp
                EXPANDED -> 700.dp
            }

        /** Edge length of the color picker inside the color picker dialog on wide windows. */
        fun WindowSizeClass.getColorPickerSize(): Dp =
            when (getWidthSize()) {
                COMPACT -> 280.dp
                MEDIUM -> 320.dp
                EXPANDED -> 360.dp
            }
    }
}
