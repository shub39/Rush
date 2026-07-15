/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.logic.datastore

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.rush.shared.core.enums.AlbumArtShape
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.CardTheme
import com.shub39.rush.shared.core.enums.CornerRadius
import com.shub39.rush.shared.core.interfaces.SharePagePreferences
import com.shub39.rush.shared.core.valueOfOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharePagePreferencesImpl(private val dataStore: DataStore<Preferences>) :
    SharePagePreferences {

    companion object {
        private val cardColor = stringPreferencesKey("card_color")
        private val cardRoundness = stringPreferencesKey("card_roundness")
        private val cardTheme = stringPreferencesKey("card_theme")
        private val cardBackground = intPreferencesKey("card_background")
        private val cardContent = intPreferencesKey("card_content")
        private val albumArtShapeKey = stringPreferencesKey("album_art_shape")
        private val fullscreenShareKey = booleanPreferencesKey("fullscreen_share")
    }

    override fun getAlbumArtShapeFlow(): Flow<AlbumArtShape> =
        dataStore.data.map { preferences ->
            valueOfOrNull<AlbumArtShape>(
                preferences[albumArtShapeKey] ?: AlbumArtShape.COOKIE_12.name
            ) ?: AlbumArtShape.COOKIE_12
        }

    override suspend fun updateAlbumArtShape(shape: AlbumArtShape) {
        dataStore.edit { preferences -> preferences[albumArtShapeKey] = shape.name }
    }

    override fun getFullscreenShare(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[fullscreenShareKey] ?: false }

    override suspend fun updateFullscreenShare(newPref: Boolean) {
        dataStore.edit { preferences -> preferences[fullscreenShareKey] = newPref }
    }

    override fun getCardBackgroundFlow(): Flow<Int> =
        dataStore.data.map { preferences -> preferences[cardBackground] ?: Color.Black.toArgb() }

    override suspend fun updateCardBackground(newCardBackground: Int) {
        dataStore.edit { settings -> settings[cardBackground] = newCardBackground }
    }

    override fun getCardContentFlow(): Flow<Int> =
        dataStore.data.map { preferences -> preferences[cardContent] ?: Color.White.toArgb() }

    override suspend fun updateCardContent(newCardContent: Int) {
        dataStore.edit { settings -> settings[cardContent] = newCardContent }
    }

    override fun getCardThemeFlow(): Flow<CardTheme> =
        dataStore.data.map { preferences ->
            val theme = preferences[cardTheme] ?: CardTheme.SPOTIFY.name
            valueOfOrNull<CardTheme>(theme) ?: CardTheme.SPOTIFY
        }

    override suspend fun updateCardTheme(newCardTheme: CardTheme) {
        dataStore.edit { settings -> settings[cardTheme] = newCardTheme.name }
    }

    override fun getCardColorFlow(): Flow<CardColors> =
        dataStore.data.map { preferences ->
            val cardColor = preferences[cardColor] ?: CardColors.MUTED.name
            valueOfOrNull<CardColors>(cardColor) ?: CardColors.MUTED
        }

    override suspend fun updateCardColor(newCardColor: CardColors) {
        dataStore.edit { settings -> settings[cardColor] = newCardColor.name }
    }

    override fun getCardRoundnessFlow(): Flow<CornerRadius> =
        dataStore.data.map { preferences ->
            val cardRoundness = preferences[cardRoundness] ?: CornerRadius.ROUNDED.name
            valueOfOrNull<CornerRadius>(cardRoundness) ?: CornerRadius.ROUNDED
        }

    override suspend fun updateCardRoundness(newCardRoundness: CornerRadius) {
        dataStore.edit { settings -> settings[cardRoundness] = newCardRoundness.name }
    }
}
