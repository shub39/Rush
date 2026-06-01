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

import com.shub39.rush.shared.core.enums.LyricsBackground

actual val allBackgrounds: List<LyricsBackground> =
    if (hypnoticAvailable()) {
        listOf(
            LyricsBackground.SOLID_COLOR,
            LyricsBackground.ALBUM_ART,
            LyricsBackground.HYPNOTIC,
            LyricsBackground.WAVE,
            LyricsBackground.GRADIENT,
            LyricsBackground.CURVE,
        )
    } else if (blurAvailable()) {
        listOf(
            LyricsBackground.SOLID_COLOR,
            LyricsBackground.ALBUM_ART,
            LyricsBackground.WAVE,
            LyricsBackground.GRADIENT,
        )
    } else {
        listOf(LyricsBackground.SOLID_COLOR, LyricsBackground.WAVE, LyricsBackground.GRADIENT)
    }
