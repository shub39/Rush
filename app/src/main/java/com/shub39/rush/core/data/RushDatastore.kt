package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.materialkolor.PaletteStyle
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.lyrics.presentation.saved.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RushDatastore(
    private val dataStore: DataStore<Preferences>
) {
    fun getDarkThemePrefFlow(): Flow<Boolean?> = dataStore.data
        .map { preferences ->
            when (preferences[darkThemePref]) {
                "true" -> true
                "false" -> false
                else -> null
            }
        }

    suspend fun updateDarkThemePref(pref: Boolean?) {
        dataStore.edit { settings ->
            settings[darkThemePref] = pref.toString()
        }
    }

    fun getSeedColorFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[seedColor] ?: Color.White.toArgb() }
    suspend fun updateSeedColor(newCardContent: Int) {
        dataStore.edit { settings ->
            settings[seedColor] = newCardContent
        }
    }

    fun getAmoledPrefFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[amoledPref] ?: false }
    suspend fun updateAmoledPref(amoled: Boolean) {
        dataStore.edit { settings ->
            settings[amoledPref] = amoled
        }
    }

    fun getPaletteStyle(): Flow<PaletteStyle> = dataStore.data
        .map { preferences ->
            PaletteStyle.valueOf(preferences[paletteStyle] ?: PaletteStyle.TonalSpot.name)
        }
    suspend fun updatePaletteStyle(style: PaletteStyle) {
        dataStore.edit { settings ->
            settings[paletteStyle] = style.name
        }
    }

    fun getMaxLinesFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[maxLines] ?: 6 }
    suspend fun updateMaxLines(newMaxLines: Int) {
        dataStore.edit { settings ->
            settings[maxLines] = newMaxLines
        }
    }

    fun getSortOrderFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[sortOrder] ?: SortOrder.TITLE_ASC.sortOrder }
    suspend fun updateSortOrder(newSortOrder: String) {
        dataStore.edit { settings ->
            settings[sortOrder] = newSortOrder
        }
    }

    fun getHypnoticCanvasFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[hypnoticCanvas] ?: true }
    suspend fun updateHypnoticCanvas(newHypnoticCanvas: Boolean) {
        dataStore.edit { settings ->
            settings[hypnoticCanvas] = newHypnoticCanvas
        }
    }

    fun getCardFitFlow(): Flow<CardFit> = dataStore.data
        .map { preferences ->
            when (preferences[cardFit]) {
                CardFit.STANDARD.name -> CardFit.STANDARD
                else -> CardFit.FIT
            }
        }
    suspend fun updateCardFit(newCardFit: CardFit) {
        dataStore.edit { settings ->
            settings[cardFit] = newCardFit.name
        }
    }

    fun getLyricsColorFlow(): Flow<CardColors> = dataStore.data
        .map { preferences ->
            when (preferences[lyricsColor]) {
                CardColors.MUTED.name -> CardColors.MUTED
                else -> CardColors.VIBRANT
            }
        }
    suspend fun updateLyricsColor(new: CardColors) {
        dataStore.edit { settings ->
            settings[lyricsColor] = new.name
        }
    }

    fun getCardBackgroundFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardBackground] ?: Color.Black.toArgb() }
    suspend fun updateCardBackground(newCardBackground: Int) {
        dataStore.edit { settings ->
            settings[cardBackground] = newCardBackground
        }
    }

    fun getCardContentFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardContent] ?: Color.White.toArgb() }
    suspend fun updateCardContent(newCardContent: Int) {
        dataStore.edit { settings ->
            settings[cardContent] = newCardContent
        }
    }

    fun getCardThemeFlow(): Flow<CardTheme> = dataStore.data
        .map { preferences ->
            when (preferences[cardTheme]) {
                CardTheme.RUSHED.name -> CardTheme.RUSHED
                CardTheme.HYPNOTIC.name -> CardTheme.HYPNOTIC
                CardTheme.IMAGE.name -> CardTheme.IMAGE
                else -> CardTheme.SPOTIFY
            }
        }
    suspend fun updateCardTheme(newCardTheme: CardTheme) {
        dataStore.edit { settings ->
            settings[cardTheme] = newCardTheme.name
        }
    }

    fun getCardColorFlow(): Flow<CardColors> = dataStore.data
        .map { preferences ->
            when (preferences[cardColor]) {
                CardColors.VIBRANT.name -> CardColors.VIBRANT
                CardColors.CUSTOM.name -> CardColors.CUSTOM
                else -> CardColors.MUTED
            }
        }
    suspend fun updateCardColor(newCardColor: CardColors) {
        dataStore.edit { settings ->
            settings[cardColor] = newCardColor.name
        }
    }

    fun getCardRoundnessFlow(): Flow<CornerRadius> = dataStore.data
        .map { preferences ->
            when (preferences[cardRoundness]) {
                CornerRadius.DEFAULT.name -> CornerRadius.DEFAULT
                else -> CornerRadius.ROUNDED
            }
        }
    suspend fun updateCardRoundness(newCardRoundness: CornerRadius) {
        dataStore.edit { settings ->
            settings[cardRoundness] = newCardRoundness.name
        }
    }

    fun getOnboardingDoneFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[onboardingDone] ?: false }
    suspend fun updateOnboardingDone(done: Boolean) {
        dataStore.edit { settings ->
            settings[onboardingDone] = done
        }
    }

    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val darkThemePref = stringPreferencesKey("use_dark_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")
        private val hypnoticCanvas = booleanPreferencesKey("hypnotic_canvas")
        private val maxLines = intPreferencesKey("max_lines")
        private val sortOrder = stringPreferencesKey("sort_order")
        private val cardColor = stringPreferencesKey("card_color")
        private val cardRoundness = stringPreferencesKey("card_roundness")
        private val cardTheme = stringPreferencesKey("card_theme")
        private val cardBackground = intPreferencesKey("card_background")
        private val cardContent = intPreferencesKey("card_content")
        private val lyricsColor = stringPreferencesKey("lyrics_color")
        private val cardFit = stringPreferencesKey("card_fit")
        private val onboardingDone = booleanPreferencesKey("onboarding_done")
    }
}