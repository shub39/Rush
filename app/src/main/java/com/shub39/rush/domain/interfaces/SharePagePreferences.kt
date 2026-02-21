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
package com.shub39.rush.domain.interfaces

import com.shub39.rush.domain.enums.AlbumArtShape
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.domain.enums.CardTheme
import com.shub39.rush.domain.enums.CornerRadius
import com.shub39.rush.domain.enums.Fonts
import kotlinx.coroutines.flow.Flow

interface SharePagePreferences {
    fun getCardFitFlow(): Flow<CardFit>

    suspend fun updateCardFit(newCardFit: CardFit)

    fun getCardBackgroundFlow(): Flow<Int>

    suspend fun updateCardBackground(newCardBackground: Int)

    fun getCardContentFlow(): Flow<Int>

    suspend fun updateCardContent(newCardContent: Int)

    fun getCardThemeFlow(): Flow<CardTheme>

    suspend fun updateCardTheme(newCardTheme: CardTheme)

    fun getCardColorFlow(): Flow<CardColors>

    suspend fun updateCardColor(newCardColor: CardColors)

    fun getCardRoundnessFlow(): Flow<CornerRadius>

    suspend fun updateCardRoundness(newCardRoundness: CornerRadius)

    fun getCardFontFlow(): Flow<Fonts>

    suspend fun updateCardFont(newCardFont: Fonts)

    fun getAlbumArtShapeFlow(): Flow<AlbumArtShape>

    suspend fun updateAlbumArtShape(shape: AlbumArtShape)

    fun showRushBranding(): Flow<Boolean>

    suspend fun updateRushBranding(newPref: Boolean)
}
