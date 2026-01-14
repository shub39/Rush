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