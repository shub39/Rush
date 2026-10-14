/*
 * Copyright (C) 2026  Karelisio
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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.rush.shared.core.dataclasses.OverlaySettings
import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import com.shub39.rush.shared.core.interfaces.BackgroundLyricsPreferences
import com.shub39.rush.shared.core.valueOfOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BackgroundLyricsPreferencesImpl(private val datastore: DataStore<Preferences>) :
    BackgroundLyricsPreferences {

    companion object {
        private val mode = stringPreferencesKey("background_lyrics_mode")
        private val opacity = floatPreferencesKey("overlay_opacity")
        private val textScale = floatPreferencesKey("overlay_text_scale")
        private val width = intPreferencesKey("overlay_width_dp")
        private val positionX = intPreferencesKey("overlay_x")
        private val positionY = intPreferencesKey("overlay_y")
    }

    override fun getModeFlow(): Flow<BackgroundLyricsMode> =
        datastore.data.map { preferences ->
            preferences[mode]?.let { valueOfOrNull<BackgroundLyricsMode>(it) }
                ?: BackgroundLyricsMode.OFF
        }

    override suspend fun setMode(mode: BackgroundLyricsMode) {
        datastore.edit { it[BackgroundLyricsPreferencesImpl.mode] = mode.name }
    }

    override fun getOverlaySettingsFlow(): Flow<OverlaySettings> =
        datastore.data.map { it.toOverlaySettings() }

    override suspend fun updateOverlaySettings(transform: (OverlaySettings) -> OverlaySettings) {
        datastore.edit { preferences ->
            val settings = transform(preferences.toOverlaySettings())
            preferences[opacity] = settings.opacity.coerceIn(OverlaySettings.MIN_OPACITY, 1f)
            preferences[textScale] =
                settings.textScale.coerceIn(
                    OverlaySettings.MIN_TEXT_SCALE,
                    OverlaySettings.MAX_TEXT_SCALE,
                )
            preferences[width] = settings.widthDp.coerceAtLeast(OverlaySettings.MIN_WIDTH_DP)
            preferences[positionX] = settings.x
            preferences[positionY] = settings.y
        }
    }

    private fun Preferences.toOverlaySettings(): OverlaySettings {
        val defaults = OverlaySettings()
        return OverlaySettings(
            opacity = this[opacity] ?: defaults.opacity,
            textScale = this[textScale] ?: defaults.textScale,
            widthDp = this[width] ?: defaults.widthDp,
            x = this[positionX] ?: defaults.x,
            y = this[positionY] ?: defaults.y,
        )
    }
}
