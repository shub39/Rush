package com.shub39.rush.core.domain

import androidx.compose.ui.text.style.TextAlign
import com.shub39.rush.core.domain.enums.CardColors
import kotlinx.coroutines.flow.Flow

interface LyricsPagePreferences {
    suspend fun reset()

    fun getHypnoticCanvasFlow(): Flow<Boolean>
    suspend fun updateHypnoticCanvas(newHypnoticCanvas: Boolean)

    fun getLyricsColorFlow(): Flow<CardColors>
    suspend fun updateLyricsColor(new: CardColors)

    fun getCardBackgroundFlow(): Flow<Int>
    suspend fun updateCardBackground(newCardBackground: Int)

    fun getCardContentFlow(): Flow<Int>
    suspend fun updateCardContent(newCardContent: Int)

    fun getUseExtractedFlow(): Flow<Boolean>
    suspend fun updateUseExtractedFlow(pref: Boolean)

    fun getLyricAlignmentFlow(): Flow<TextAlign>
    suspend fun updateLyricAlignment(alignment: TextAlign)

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
}