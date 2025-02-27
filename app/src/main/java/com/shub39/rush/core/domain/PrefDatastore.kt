package com.shub39.rush.core.domain

import com.materialkolor.PaletteStyle
import com.shub39.rush.lyrics.presentation.saved.SortOrder
import kotlinx.coroutines.flow.Flow

interface PrefDatastore {
    fun getDarkThemePrefFlow(): Flow<Boolean?>
    suspend fun updateDarkThemePref(pref: Boolean?)

    fun getSeedColorFlow(): Flow<Int>
    suspend fun updateSeedColor(newCardContent: Int)

    fun getAmoledPrefFlow(): Flow<Boolean>
    suspend fun updateAmoledPref(amoled: Boolean)

    fun getPaletteStyle(): Flow<PaletteStyle>
    suspend fun updatePaletteStyle(style: PaletteStyle)

    fun getMaxLinesFlow(): Flow<Int>
    suspend fun updateMaxLines(newMaxLines: Int)

    fun getSortOrderFlow(): Flow<SortOrder>
    suspend fun updateSortOrder(newSortOrder: SortOrder)

    fun getHypnoticCanvasFlow(): Flow<Boolean>
    suspend fun updateHypnoticCanvas(newHypnoticCanvas: Boolean)

    fun getCardFitFlow(): Flow<CardFit>
    suspend fun updateCardFit(newCardFit: CardFit)

    fun getLyricsColorFlow(): Flow<CardColors>
    suspend fun updateLyricsColor(new: CardColors)

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

    fun getOnboardingDoneFlow(): Flow<Boolean>
    suspend fun updateOnboardingDone(done: Boolean)

    fun getMaterialYouFlow(): Flow<Boolean>
    suspend fun updateMaterialTheme(pref: Boolean)

    fun getFontFlow(): Flow<Fonts>
    suspend fun updateFonts(font: Fonts)
}