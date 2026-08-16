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

/**
 * A data class to hold the extracted colors from an image
 *
 * @property cardBackgroundDominant The dominant color from the image
 * @property cardContentDominant A suitable color for text or icons placed on top of
 *   [cardBackgroundDominant].
 * @property cardBackgroundMuted A more subdued or muted color from the image
 * @property cardContentMuted A suitable color for text or icons placed on top of
 *   [cardBackgroundMuted].
 */
data class ExtractedColors(
    val cardBackgroundDominant: Int = 0x393939,
    val cardContentDominant: Int = 0xFFFFFF,
    val cardBackgroundMuted: Int = 0x282828,
    val cardContentMuted: Int = 0xFFFFFF,
)
