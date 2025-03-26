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
import com.shub39.rush.core.domain.Fonts
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.lyrics.presentation.saved.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OtherPreferencesImpl(
    private val dataStore: DataStore<Preferences>
): OtherPreferences {

    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val appTheme = stringPreferencesKey("app_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")
        private val materialTheme = booleanPreferencesKey("material_theme")
        private val maxLines = intPreferencesKey("max_lines")
        private val sortOrder = stringPreferencesKey("sort_order")
        private val onboardingDone = booleanPreferencesKey("onboarding_done")
        private val selectedFont = stringPreferencesKey("font")
        private val fullscreen = booleanPreferencesKey("fullscreen")
    }

    override fun getAppThemePrefFlow(): Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val theme  = preferences[appTheme] ?: AppTheme.SYSTEM.name
            AppTheme.valueOf(theme)
        }
    override suspend fun updateAppThemePref(pref: AppTheme) {
        dataStore.edit {
            it[appTheme] = pref.name
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
        .map { preferences -> preferences[amoledPref] == true }
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

    override fun getOnboardingDoneFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[onboardingDone] == true }
    override suspend fun updateOnboardingDone(done: Boolean) {
        dataStore.edit { settings ->
            settings[onboardingDone] = done
        }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[materialTheme] == true }
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

    override fun getFullScreenFlow(): Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[fullscreen] != false
        }
    override suspend fun setFullScreen(pref: Boolean) {
        dataStore.edit { prefs ->
            prefs[fullscreen] = pref
        }
    }

}