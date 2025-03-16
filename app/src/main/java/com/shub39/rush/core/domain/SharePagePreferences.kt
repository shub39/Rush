package com.shub39.rush.core.domain

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
}