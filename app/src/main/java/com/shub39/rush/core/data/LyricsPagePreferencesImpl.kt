package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
        private val lyricAlignment = stringPreferencesKey("lyric_alignment")
        private val fontSize = floatPreferencesKey("font_size")
        private val lineHeight = floatPreferencesKey("line_height")
        private val letterSpacing = floatPreferencesKey("letter_spacing")
    }

    override suspend fun reset() {
        dataStore.edit { settings ->
            settings[hypnoticCanvas] = false
            settings[cardBackground] = Color.Black.toArgb()
            settings[cardContent] = Color.White.toArgb()
            settings[lyricsColor] = CardColors.MUTED.name
            settings[useExtracted] = true
            settings[lyricAlignment] = TextAlign.Start.toString()
            settings[fontSize] = 28f
            settings[lineHeight] = 32f
            settings[letterSpacing] = 0f
        }
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

    override fun getLyricAlignmentFlow(): Flow<TextAlign> = dataStore.data
        .map { prefs ->
            val alignment = prefs[lyricAlignment] ?: TextAlign.Start.toString()
            TextAlign.values().find { it.toString() == alignment } ?: TextAlign.Start
        }
    override suspend fun updateLyricAlignment(alignment: TextAlign) {
        dataStore.edit { prefs ->
            prefs[lyricAlignment] = alignment.toString()
        }
    }

    override fun getFontSizeFlow(): Flow<Float> = dataStore.data
        .map { prefs ->
            prefs[fontSize] ?: 28f
        }
    override suspend fun updateFontSize(newFontSize: Float) {
        dataStore.edit { prefs ->
            prefs[fontSize] = newFontSize
        }
    }

    override fun getLineHeightFlow(): Flow<Float> = dataStore.data
        .map { prefs ->
            prefs[lineHeight] ?: 32f
        }
    override suspend fun updateLineHeight(newLineHeight: Float) {
        dataStore.edit { prefs ->
            prefs[lineHeight] = newLineHeight
        }
    }

    override fun getLetterSpacingFlow(): Flow<Float> = dataStore.data
        .map { prefs ->
            prefs[letterSpacing] ?: 0f
        }
    override suspend fun updateLetterSpacing(newLetterSpacing: Float) {
        dataStore.edit { prefs ->
            prefs[letterSpacing] = newLetterSpacing
        }
    }

}