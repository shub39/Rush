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
package com.shub39.rush.shared.core.dataclasses

/**
 * Floating lyrics window settings.
 *
 * @property opacity Background opacity, between 0.2 and 1.
 * @property textScale Lyrics text size multiplier.
 * @property widthDp Window width.
 * @property x Horizontal position of the window, in pixels.
 * @property y Vertical position of the window, in pixels.
 */
data class OverlaySettings(
    val opacity: Float = 0.85f,
    val textScale: Float = 1f,
    val widthDp: Int = 320,
    val x: Int = 32,
    val y: Int = 200,
) {
    companion object {
        const val MIN_OPACITY = 0.2f
        const val MIN_TEXT_SCALE = 0.7f
        const val MAX_TEXT_SCALE = 1.8f
        const val MIN_WIDTH_DP = 180
    }
}
