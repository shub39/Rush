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
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shub39.rush.shared.core.dataclasses.LyricsSourceSetting
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.interfaces.LyricsSourcePreferences
import com.shub39.rush.shared.core.valueOfOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LyricsSourcePreferencesImpl(private val datastore: DataStore<Preferences>) :
    LyricsSourcePreferences {

    companion object {
        private val lyricsSources = stringPreferencesKey("lyrics_sources")

        val defaultSources = LyricsProviderId.entries.map { LyricsSourceSetting(it, true) }

        /** Decodes "LRCLIB:1,NETEASE:0", unknown entries are dropped and new sources appended. */
        fun decode(value: String?): List<LyricsSourceSetting> {
            if (value.isNullOrBlank()) return defaultSources

            val stored =
                value
                    .split(",")
                    .mapNotNull { entry ->
                        val name = entry.substringBefore(":").trim()
                        val provider =
                            valueOfOrNull<LyricsProviderId>(name) ?: return@mapNotNull null
                        LyricsSourceSetting(provider, entry.substringAfter(":", "1").trim() != "0")
                    }
                    .distinctBy { it.provider }

            val missing =
                LyricsProviderId.entries
                    .filter { provider -> stored.none { it.provider == provider } }
                    .map { LyricsSourceSetting(it, true) }

            return stored + missing
        }

        fun encode(sources: List<LyricsSourceSetting>): String =
            sources.joinToString(",") { "${it.provider.name}:${if (it.enabled) 1 else 0}" }
    }

    override fun getSourcesFlow(): Flow<List<LyricsSourceSetting>> =
        datastore.data.map { preferences -> decode(preferences[lyricsSources]) }

    override suspend fun updateSources(sources: List<LyricsSourceSetting>) {
        datastore.edit { it[lyricsSources] = encode(sources) }
    }

    override suspend fun resetSources() {
        datastore.edit { it.remove(lyricsSources) }
    }
}
