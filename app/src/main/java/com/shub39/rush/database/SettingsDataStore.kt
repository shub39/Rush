package com.shub39.rush.database

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object SettingsDataStore {
    private const val DATA_STORE_FILE_NAME = "settings.pb"
    private val Context.dataStore by preferencesDataStore( name = DATA_STORE_FILE_NAME )
    private val MAX_LINES = intPreferencesKey("max_lines")
    private val TOGGLE_THEME = stringPreferencesKey("toggle_theme")
    private val SORT_ORDER = stringPreferencesKey("sort_order")

    fun getSortOrderFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[SORT_ORDER] ?: "title_asc"
        }

    fun getToggleThemeFlow(context: Context): Flow<String> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[TOGGLE_THEME] ?: "Yellow"
        }

    fun getMaxLinesFlow(context: Context): Flow<Int> = context.dataStore.data
        .catch {
            Log.e(TAG, it.message, it)
        }
        .map { preferences ->
            preferences[MAX_LINES] ?: 6
        }

    suspend fun updateSortOrder(context: Context, newSortOrder: String) {
        context.dataStore.edit { settings ->
            settings[SORT_ORDER] = newSortOrder
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