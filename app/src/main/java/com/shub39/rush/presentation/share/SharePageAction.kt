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
package com.shub39.rush.presentation.share

import com.shub39.rush.domain.enums.AlbumArtShape
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.domain.enums.CardTheme
import com.shub39.rush.domain.enums.CornerRadius
import com.shub39.rush.domain.enums.Fonts

sealed interface SharePageAction {
    data class OnToggleRushBranding(val pref: Boolean) : SharePageAction

    data class OnUpdateAlbumArtShape(val shape: AlbumArtShape) : SharePageAction

    data class OnUpdateCardTheme(val theme: CardTheme) : SharePageAction

    data class OnUpdateCardColor(val color: CardColors) : SharePageAction

    data class OnUpdateCardFit(val fit: CardFit) : SharePageAction

    data class OnUpdateCardRoundness(val roundness: CornerRadius) : SharePageAction

    data class OnUpdateCardContent(val color: Int) : SharePageAction

    data class OnUpdateCardBackground(val color: Int) : SharePageAction

    data class OnUpdateCardFont(val font: Fonts) : SharePageAction
}
