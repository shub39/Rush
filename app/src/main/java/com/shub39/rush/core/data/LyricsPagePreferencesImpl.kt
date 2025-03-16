package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.LyricsPagePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LyricsPagePreferencesImpl(
    private val dataStore: DataStore<Preferences>
): LyricsPagePreferences {

    companion object {
        private val hypnoticCanvas = booleanPreferencesKey("hypnotic_canvas")
        private val cardBackground = intPreferencesKey("card_background")
        private val cardContent = intPreferencesKey("card_content")
        private val lyricsColor = stringPreferencesKey("lyrics_color")
        private val useExtracted = booleanPreferencesKey("use_extracted")
    }

    override fun getHypnoticCanvasFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[hypnoticCanvas] != false }
    override suspend fun updateHypnoticCanvas(newHypnoticCanvas: Boolean) {
        dataStore.edit { settings ->
            settings[hypnoticCanvas] = newHypnoticCanvas
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

    override fun getUseExtractedFlow(): Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[useExtracted] != false
        }
    override suspend fun updateUseExtractedFlow(pref: Boolean) {
        dataStore.edit { prefs ->
            prefs[useExtracted] = pref
        }
    }

}