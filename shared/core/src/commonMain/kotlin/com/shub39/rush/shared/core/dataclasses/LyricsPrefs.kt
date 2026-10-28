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
package com.shub39.rush.shared.core.dataclasses

import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.core.enums.LyricsBackground

/**
 * Immutable snapshot of every persisted lyrics page preference.
 *
 * The datastore exposes each preference as its own flow; consumers almost always need all of them
 * at once, so this snapshot exists to collapse those flows into a single emission source of truth.
 */
data class LyricsPrefs(
    val expressiveSyllables: Boolean = false,
    val blurSyncedLyrics: Boolean = true,
    val fontSize: Float = 28f,
    val lineHeight: Float = 32f,
    val letterSpacing: Float = 0f,
    val alignment: LyricsAlignment = LyricsAlignment.START,
    val cardColors: CardColors = CardColors.VIBRANT,
    val lyricsBackground: LyricsBackground = LyricsBackground.SOLID_COLOR,
    val maxLines: Int = 6,
    val cardBackground: Int = 0xFF000000.toInt(),
    val cardContent: Int = 0xFFFFFFFF.toInt(),
    val fullscreen: Boolean = false,
    val romanizationEnabled: Boolean = false,
    val hideUI: Boolean = false,
)
