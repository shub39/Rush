package com.shub39.rush.core.domain

import com.materialkolor.PaletteStyle
import com.shub39.rush.lyrics.presentation.saved.SortOrder
import kotlinx.coroutines.flow.Flow

interface OtherPreferences {
    fun getAppThemePrefFlow(): Flow<AppTheme>
    suspend fun updateAppThemePref(pref: AppTheme)

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

    fun getOnboardingDoneFlow(): Flow<Boolean>
    suspend fun updateOnboardingDone(done: Boolean)

    fun getMaterialYouFlow(): Flow<Boolean>
    suspend fun updateMaterialTheme(pref: Boolean)

    fun getFontFlow(): Flow<Fonts>
    suspend fun updateFonts(font: Fonts)

    fun getFullScreenFlow(): Flow<Boolean>
    suspend fun setFullScreen(pref: Boolean)
}