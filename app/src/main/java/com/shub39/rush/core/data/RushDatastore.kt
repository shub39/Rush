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
import com.shub39.rush.core.domain.Fonts
import com.shub39.rush.core.domain.PrefDatastore
import com.shub39.rush.lyrics.presentation.saved.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RushDatastore(
    private val dataStore: DataStore<Preferences>
): PrefDatastore {
    override fun getDarkThemePrefFlow(): Flow<Boolean?> = dataStore.data
        .map { preferences ->
            when (preferences[darkThemePref]) {
                "true" -> true
                "false" -> false
                else -> null
            }
        }
    override suspend fun updateDarkThemePref(pref: Boolean?) {
        dataStore.edit { settings ->
            settings[darkThemePref] = pref.toString()
        }
    }

    override fun getSeedColorFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[seedColor] ?: Color.White.toArgb() }
    override suspend fun updateSeedColor(newCardContent: Int) {
        dataStore.edit { settings ->
            settings[seedColor] = newCardContent
        }
    }

    override fun getAmoledPrefFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[amoledPref] ?: false }
    override suspend fun updateAmoledPref(amoled: Boolean) {
        dataStore.edit { settings ->
            settings[amoledPref] = amoled
        }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> = dataStore.data
        .map { preferences ->
            PaletteStyle.valueOf(preferences[paletteStyle] ?: PaletteStyle.TonalSpot.name)
        }
    override suspend fun updatePaletteStyle(style: PaletteStyle) {
        dataStore.edit { settings ->
            settings[paletteStyle] = style.name
        }
    }

    override fun getMaxLinesFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[maxLines] ?: 6 }
    override suspend fun updateMaxLines(newMaxLines: Int) {
        dataStore.edit { settings ->
            settings[maxLines] = newMaxLines
        }
    }

    override fun getSortOrderFlow(): Flow<SortOrder> = dataStore.data
        .map { preferences ->
            val order = preferences[sortOrder] ?: SortOrder.TITLE_ASC.name
            SortOrder.valueOf(order.uppercase())
        }
    override suspend fun updateSortOrder(newSortOrder: SortOrder) {
        dataStore.edit { settings ->
            settings[sortOrder] = newSortOrder.name
        }
    }

    override fun getHypnoticCanvasFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[hypnoticCanvas] ?: true }
    override suspend fun updateHypnoticCanvas(newHypnoticCanvas: Boolean) {
        dataStore.edit { settings ->
            settings[hypnoticCanvas] = newHypnoticCanvas
        }
    }

    override fun getCardFitFlow(): Flow<CardFit> = dataStore.data
        .map { preferences ->
            when (preferences[cardFit]) {
                CardFit.STANDARD.name -> CardFit.STANDARD
                else -> CardFit.FIT
            }
        }
    override suspend fun updateCardFit(newCardFit: CardFit) {
        dataStore.edit { settings ->
            settings[cardFit] = newCardFit.name
        }
    }

    override fun getLyricsColorFlow(): Flow<CardColors> = dataStore.data
        .map { preferences ->
            when (preferences[lyricsColor]) {
                CardColors.MUTED.name -> CardColors.MUTED
                else -> CardColors.VIBRANT
            }
        }
    override suspend fun updateLyricsColor(new: CardColors) {
        dataStore.edit { settings ->
            settings[lyricsColor] = new.name
        }
    }

    override fun getCardBackgroundFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardBackground] ?: Color.Black.toArgb() }
    override suspend fun updateCardBackground(newCardBackground: Int) {
        dataStore.edit { settings ->
            settings[cardBackground] = newCardBackground
        }
    }

    override fun getCardContentFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardContent] ?: Color.White.toArgb() }
    override suspend fun updateCardContent(newCardContent: Int) {
        dataStore.edit { settings ->
            settings[cardContent] = newCardContent
        }
    }

    override fun getCardThemeFlow(): Flow<CardTheme> = dataStore.data
        .map { preferences ->
            when (preferences[cardTheme]) {
                CardTheme.RUSHED.name -> CardTheme.RUSHED
                CardTheme.HYPNOTIC.name -> CardTheme.HYPNOTIC
                CardTheme.IMAGE.name -> CardTheme.IMAGE
                else -> CardTheme.SPOTIFY
            }
        }
    override suspend fun updateCardTheme(newCardTheme: CardTheme) {
        dataStore.edit { settings ->
            settings[cardTheme] = newCardTheme.name
        }
    }

    override fun getCardColorFlow(): Flow<CardColors> = dataStore.data
        .map { preferences ->
            when (preferences[cardColor]) {
                CardColors.VIBRANT.name -> CardColors.VIBRANT
                CardColors.CUSTOM.name -> CardColors.CUSTOM
                else -> CardColors.MUTED
            }
        }
    override suspend fun updateCardColor(newCardColor: CardColors) {
        dataStore.edit { settings ->
            settings[cardColor] = newCardColor.name
        }
    }

    override fun getCardRoundnessFlow(): Flow<CornerRadius> = dataStore.data
        .map { preferences ->
            when (preferences[cardRoundness]) {
                CornerRadius.DEFAULT.name -> CornerRadius.DEFAULT
                else -> CornerRadius.ROUNDED
            }
        }
    override suspend fun updateCardRoundness(newCardRoundness: CornerRadius) {
        dataStore.edit { settings ->
            settings[cardRoundness] = newCardRoundness.name
        }
    }

    override fun getOnboardingDoneFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[onboardingDone] ?: false }
    override suspend fun updateOnboardingDone(done: Boolean) {
        dataStore.edit { settings ->
            settings[onboardingDone] = done
        }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[materialTheme] ?: false }
    override suspend fun updateMaterialTheme(pref: Boolean) {
        dataStore.edit { settings ->
            settings[materialTheme] = pref
        }
    }

    override fun getFontFlow(): Flow<Fonts> = dataStore.data
        .map { prefs ->
            val font = prefs[selectedFont] ?: Fonts.POPPINS.name
            Fonts.valueOf(font)
        }
    override suspend fun updateFonts(font: Fonts) {
        dataStore.edit { settings ->
            settings[selectedFont] = font.name
        }
    }

    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val darkThemePref = stringPreferencesKey("use_dark_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")
        private val hypnoticCanvas = booleanPreferencesKey("hypnotic_canvas")
        private val materialTheme = booleanPreferencesKey("material_theme")
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
        private val selectedFont = stringPreferencesKey("font")
    }
}