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
import com.shub39.rush.core.domain.AppTheme
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
            settings[darkThemePref] = pref.toString().also { println(it) }
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

    // Undefined

    fun getCardFitFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardFit] ?: CardFit.STANDARD.type }
    suspend fun updateCardFit(newCardFit: String) {
        dataStore.edit { settings ->
            settings[cardFit] = newCardFit
        }
    }

    fun getLyricsColorFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[lyricsColor] ?: CardColors.MUTED.color }
    suspend fun updateLyricsColor(new: String) {
        dataStore.edit { settings ->
            settings[lyricsColor] = new
        }
    }

    fun getCardBackgroundFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardBackground] ?: Color.Black.toArgb() }
    suspend fun updateCardBackground(newCardBackground: Int) {
        dataStore.edit { settings ->
            settings[cardBackground] = newCardBackground
        }
    }




    fun getCardThemeFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardTheme] ?: CardTheme.SPOTIFY.type }
    suspend fun updateCardTheme(newCardTheme: String) {
        dataStore.edit { settings ->
            settings[cardTheme] = newCardTheme
        }
    }

    fun getCardColorFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardColor] ?: CardColors.VIBRANT.color }
    suspend fun updateCardColor(newCardColor: String) {
        dataStore.edit { settings ->
            settings[cardColor] = newCardColor
        }
    }

    fun getCardRoundnessFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardRoundness] ?: CornerRadius.ROUNDED.type }
    suspend fun updateCardRoundness(newCardRoundness: String) {
        dataStore.edit { settings ->
            settings[cardRoundness] = newCardRoundness
        }
    }



    fun getToggleThemeFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[toggleTheme] ?: AppTheme.YELLOW.type }
    suspend fun updateToggleTheme(newToggleTheme: String) {
        dataStore.edit { settings ->
            settings[toggleTheme] = newToggleTheme
        }
    }



    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val darkThemePref = stringPreferencesKey("use_dark_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")

        private val maxLines = intPreferencesKey("max_lines")
        private val toggleTheme = stringPreferencesKey("toggle_theme")
        private val sortOrder = stringPreferencesKey("sort_order")
        private val cardColor = stringPreferencesKey("card_color")
        private val cardRoundness = stringPreferencesKey("card_roundness")
        private val cardTheme = stringPreferencesKey("card_theme")
        private val cardBackground = intPreferencesKey("card_background")
        private val lyricsColor = stringPreferencesKey("lyrics_color")
        private val cardFit = stringPreferencesKey("card_fit")
    }
}