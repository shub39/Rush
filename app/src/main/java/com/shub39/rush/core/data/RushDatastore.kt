package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
    fun getCardFitFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardFit] ?: CardFit.STANDARD.type }

    fun getLyricsColorFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[lyricsColor] ?: CardColors.MUTED.color }

    fun getCardBackgroundFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardBackground] ?: Color.Black.toArgb() }

    fun getCardContentFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[cardContent] ?: Color.White.toArgb() }

    fun getCardThemeFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardTheme] ?: CardTheme.SPOTIFY.type }

    fun getCardColorFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardColor] ?: CardColors.VIBRANT.color }

    fun getCardRoundnessFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[cardRoundness] ?: CornerRadius.ROUNDED.type }

    fun getSortOrderFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[sortOrder] ?: SortOrder.TITLE_ASC.sortOrder }

    fun getToggleThemeFlow(): Flow<String> = dataStore.data
        .map { preferences -> preferences[toggleTheme] ?: AppTheme.YELLOW.type }

    fun getMaxLinesFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[maxLines] ?: 6 }

    suspend fun updateCardFit(newCardFit: String) {
        dataStore.edit { settings ->
            settings[cardFit] = newCardFit
        }
    }

    suspend fun updateCardBackground(newCardBackground: Int) {
        dataStore.edit { settings ->
            settings[cardBackground] = newCardBackground
        }
    }

    suspend fun updateCardContent(newCardContent: Int) {
        dataStore.edit { settings ->
            settings[cardContent] = newCardContent
        }
    }

    suspend fun updateLyricsColor(new: String) {
        dataStore.edit { settings ->
            settings[lyricsColor] = new
        }
    }

    suspend fun updateSortOrder(newSortOrder: String) {
        dataStore.edit { settings ->
            settings[sortOrder] = newSortOrder
        }
    }

    suspend fun updateCardTheme(newCardTheme: String) {
        dataStore.edit { settings ->
            settings[cardTheme] = newCardTheme
        }
    }

    suspend fun updateCardColor(newCardColor: String) {
        dataStore.edit { settings ->
            settings[cardColor] = newCardColor
        }
    }

    suspend fun updateCardRoundness(newCardRoundness: String) {
        dataStore.edit { settings ->
            settings[cardRoundness] = newCardRoundness
        }
    }

    suspend fun updateMaxLines(newMaxLines: Int) {
        dataStore.edit { settings ->
            settings[maxLines] = newMaxLines
        }
    }

    suspend fun updateToggleTheme(newToggleTheme: String) {
        dataStore.edit { settings ->
            settings[toggleTheme] = newToggleTheme
        }
    }

    companion object {
        private val maxLines = intPreferencesKey("max_lines")
        private val toggleTheme = stringPreferencesKey("toggle_theme")
        private val sortOrder = stringPreferencesKey("sort_order")
        private val cardColor = stringPreferencesKey("card_color")
        private val cardRoundness = stringPreferencesKey("card_roundness")
        private val cardTheme = stringPreferencesKey("card_theme")
        private val cardBackground = intPreferencesKey("card_background")
        private val cardContent = intPreferencesKey("card_content")
        private val lyricsColor = stringPreferencesKey("lyrics_color")
        private val cardFit = stringPreferencesKey("card_fit")
    }
}