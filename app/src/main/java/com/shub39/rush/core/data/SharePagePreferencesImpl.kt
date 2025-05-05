package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.core.domain.SharePagePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharePagePreferencesImpl(
    private val dataStore: DataStore<Preferences>
): SharePagePreferences {

    companion object {
        private val cardColor = stringPreferencesKey("card_color")
        private val cardRoundness = stringPreferencesKey("card_roundness")
        private val cardTheme = stringPreferencesKey("card_theme")
        private val cardBackground = intPreferencesKey("card_background")
        private val cardContent = intPreferencesKey("card_content")
        private val cardFit = stringPreferencesKey("card_fit")
    }

    override fun getCardFitFlow(): Flow<CardFit> = dataStore.data
        .map { preferences ->
            val cardFit = preferences[cardFit] ?: CardFit.FIT.name
            CardFit.valueOf(cardFit)
        }
    override suspend fun updateCardFit(newCardFit: CardFit) {
        dataStore.edit { settings ->
            settings[cardFit] = newCardFit.name
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
            val theme = preferences[cardTheme] ?: CardTheme.SPOTIFY.name
            CardTheme.valueOf(theme)
        }
    override suspend fun updateCardTheme(newCardTheme: CardTheme) {
        dataStore.edit { settings ->
            settings[cardTheme] = newCardTheme.name
        }
    }

    override fun getCardColorFlow(): Flow<CardColors> = dataStore.data
        .map { preferences ->
            val cardColor = preferences[cardColor] ?: CardColors.MUTED.name
            CardColors.valueOf(cardColor)
        }
    override suspend fun updateCardColor(newCardColor: CardColors) {
        dataStore.edit { settings ->
            settings[cardColor] = newCardColor.name
        }
    }

    override fun getCardRoundnessFlow(): Flow<CornerRadius> = dataStore.data
        .map { preferences ->
            val cardRoundness = preferences[cardRoundness] ?: CornerRadius.DEFAULT.name
            CornerRadius.valueOf(cardRoundness)
        }
    override suspend fun updateCardRoundness(newCardRoundness: CornerRadius) {
        dataStore.edit { settings ->
            settings[cardRoundness] = newCardRoundness.name
        }
    }

}