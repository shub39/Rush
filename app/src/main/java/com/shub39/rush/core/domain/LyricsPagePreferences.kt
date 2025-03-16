package com.shub39.rush.core.domain

import kotlinx.coroutines.flow.Flow

interface LyricsPagePreferences {
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
}