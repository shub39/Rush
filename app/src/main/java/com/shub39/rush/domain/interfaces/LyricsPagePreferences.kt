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

import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.domain.enums.LyricsBackground
import kotlinx.coroutines.flow.Flow

interface LyricsPagePreferences {
    suspend fun reset()

    fun getLyricsColorFlow(): Flow<CardColors>

    suspend fun updateLyricsColor(new: CardColors)

    fun getCardBackgroundFlow(): Flow<Int>

    suspend fun updateCardBackground(newCardBackground: Int)

    fun getCardContentFlow(): Flow<Int>

    suspend fun updateCardContent(newCardContent: Int)

    fun getUseExtractedFlow(): Flow<Boolean>

    suspend fun updateUseExtractedFlow(pref: Boolean)

    fun getLyricAlignmentFlow(): Flow<LyricsAlignment>

    suspend fun updateLyricAlignment(alignment: LyricsAlignment)

    fun getFontSizeFlow(): Flow<Float>

    suspend fun updateFontSize(newFontSize: Float)

    fun getLineHeightFlow(): Flow<Float>

    suspend fun updateLineHeight(newLineHeight: Float)

    fun getLetterSpacingFlow(): Flow<Float>

    suspend fun updateLetterSpacing(newLetterSpacing: Float)

    fun getFullScreenFlow(): Flow<Boolean>

    suspend fun setFullScreen(pref: Boolean)

    fun getMaxLinesFlow(): Flow<Int>

    suspend fun updateMaxLines(newMaxLines: Int)

    fun getLyricsBackgroundFlow(): Flow<LyricsBackground>

    suspend fun updateLyricsBackround(background: LyricsBackground)

    fun getBlurSynced(): Flow<Boolean>

    suspend fun updateBlurSynced(pref: Boolean)
}
