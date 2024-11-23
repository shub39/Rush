package com.shub39.rush.core.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shub39.rush.core.domain.AppTheme
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.lyrics.presentation.saved.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object RushDataStore {

    private const val TAG = "SettingsDataStore"
    private const val DATA_STORE_FILE_NAME = "settings.pb"
    private val Context.dataStore by preferencesDataStore( name = DATA_STORE_FILE_NAME )

    private val MAX_LINES = intPreferencesKey("max_lines")
    private val TOGGLE_THEME = stringPreferencesKey("toggle_theme")
    private val SORT_ORDER = stringPreferencesKey("sort_order")
    private val CARD_COLOR = stringPreferencesKey("card_color")
    private val CARD_ROUNDNESS = stringPreferencesKey("card_roundness")
    private val CARD_THEME = stringPreferencesKey("card_theme")
    private val CARD_BACKGROUND = intPreferencesKey("card_background")
    private val CARD_CONTENT = intPreferencesKey("card_content")
    private val LYRICS_COLOR = stringPreferencesKey("lyrics_color")
    private val CARD_FIT = stringPreferencesKey("card_fit")

    fun getCardFitFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_FIT] ?: CardFit.STANDARD.type
        }

    fun getLyricsColorFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[LYRICS_COLOR] ?: CardColors.MUTED.color
        }

    fun getCardBackgroundFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_BACKGROUND] ?: Color.Black.toArgb()
        }

    fun getCardContentFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_CONTENT] ?: Color.White.toArgb()
        }

    fun getCardThemeFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_THEME] ?: CardTheme.SPOTIFY.type
        }

    fun getCardColorFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_COLOR] ?: CardColors.VIBRANT.color
        }

    fun getCardRoundnessFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[CARD_ROUNDNESS] ?: CornerRadius.ROUNDED.type
        }

    fun getSortOrderFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[SORT_ORDER] ?: SortOrder.TITLE_ASC.sortOrder
        }

    fun getToggleThemeFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[TOGGLE_THEME] ?: AppTheme.YELLOW.type
        }

    fun getMaxLinesFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[MAX_LINES] ?: 6
        }

    suspend fun updateCardFit(context: Context, newCardFit: String) {
        context.dataStore.edit { settings ->
            settings[CARD_FIT] = newCardFit
        }
    }

    suspend fun updateCardBackground(context: Context, newCardBackground: Int) {
        context.dataStore.edit { settings ->
            settings[CARD_BACKGROUND] = newCardBackground
        }
    }

    suspend fun updateCardContent(context: Context, newCardContent: Int) {
        context.dataStore.edit { settings ->
            settings[CARD_CONTENT] = newCardContent
        }
    }

    suspend fun setLyricsColor(context: Context, new: String) {
        context.dataStore.edit { settings ->
            settings[LYRICS_COLOR] = new
        }
    }

    suspend fun updateSortOrder(context: Context, newSortOrder: String) {
        context.dataStore.edit { settings ->
            settings[SORT_ORDER] = newSortOrder
        }
    }

    suspend fun updateCardTheme(context: Context, newCardTheme: String) {
        context.dataStore.edit { settings ->
            settings[CARD_THEME] = newCardTheme
        }
    }

    suspend fun updateCardColor(context: Context, newCardColor: String) {
        context.dataStore.edit { settings ->
            settings[CARD_COLOR] = newCardColor
        }
    }

    suspend fun updateCardRoundness(context: Context, newCardRoundness: String) {
        context.dataStore.edit { settings ->
            settings[CARD_ROUNDNESS] = newCardRoundness
        }
    }

    suspend fun updateMaxLines(context: Context, newMaxLines: Int) {
        context.dataStore.edit { settings ->
            settings[MAX_LINES] = newMaxLines
        }
    }

    suspend fun updateToggleTheme(context: Context, newToggleTheme: String) {
        context.dataStore.edit { settings ->
            settings[TOGGLE_THEME] = newToggleTheme
        }
    }
}