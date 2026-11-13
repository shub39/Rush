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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FitToScreenPreferenceTest {
    private class InMemoryDataStore : DataStore<Preferences> {
        private val current = MutableStateFlow(emptyPreferences())

        override val data: Flow<Preferences> = current

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences
        ): Preferences = transform(current.value).also { current.value = it }
    }

    @Test
    fun isOffByDefault() = runBlocking {
        val prefs = LyricsPagePreferencesImpl(InMemoryDataStore())

        assertFalse(prefs.getFitToScreenFlow().first())
        assertFalse(prefs.getPreferencesFlow().first().fitToScreen)
    }

    @Test
    fun isRememberedInTheStoreAndTheSnapshot() = runBlocking {
        val store = InMemoryDataStore()
        LyricsPagePreferencesImpl(store).updateFitToScreen(true)

        // A new instance over the same store is what an app restart looks like
        val restarted = LyricsPagePreferencesImpl(store)

        assertTrue(restarted.getFitToScreenFlow().first())
        assertTrue(restarted.getPreferencesFlow().first().fitToScreen)
    }

    @Test
    fun isClearedByReset() = runBlocking {
        val prefs = LyricsPagePreferencesImpl(InMemoryDataStore())
        prefs.updateFitToScreen(true)

        prefs.reset()

        assertFalse(prefs.getFitToScreenFlow().first())
    }
}
