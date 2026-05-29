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
package com.shub39.rush.shared.ui.share

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SongDetails
import com.shub39.rush.shared.core.enums.AlbumArtShape
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.CardFit
import com.shub39.rush.shared.core.enums.CardTheme
import com.shub39.rush.shared.core.enums.CornerRadius
import com.shub39.rush.shared.core.enums.Fonts

@Stable
@Immutable
data class SharePageState(
    val songDetails: SongDetails = SongDetails("", "", null, ""),
    val selectedLines: Map<Int, String> = emptyMap(),
    val extractedColors: ExtractedColors = ExtractedColors(),
    val cardFont: Fonts = Fonts.POPPINS,
    val cardColors: CardColors = CardColors.MUTED,
    val cardBackground: Int = Color.Gray.toArgb(),
    val cardContent: Int = Color.White.toArgb(),
    val cardFit: CardFit = CardFit.FIT,
    val cardRoundness: CornerRadius = CornerRadius.ROUNDED,
    val cardTheme: CardTheme = CardTheme.SPOTIFY,
    val albumArtShape: AlbumArtShape = AlbumArtShape.COOKIE_12,
    val rushBranding: Boolean = true,
    val fullScreen: Boolean = false,
)
