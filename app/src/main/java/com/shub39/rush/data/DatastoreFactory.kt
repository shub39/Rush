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
package com.shub39.rush.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

@Single
class DatastoreFactory(private val context: Context) {
    fun getLyricsPagePreferencesDataStore(): DataStore<Preferences> =
        createDataStore(producePath = { context.filesDir.resolve(LYRICS_DATASTORE).absolutePath })

    fun getOtherPreferencesDataStore(): DataStore<Preferences> =
        createDataStore(producePath = { context.filesDir.resolve(OTHER_DATASTORE).absolutePath })

    fun getSharePagePreferencesDataStore(): DataStore<Preferences> =
        createDataStore(producePath = { context.filesDir.resolve(SHARE_DATASTORE).absolutePath })
}

internal const val LYRICS_DATASTORE = "rush.lyrics.preferences_pb"
internal const val OTHER_DATASTORE = "rush.other.preferences_pb"
internal const val SHARE_DATASTORE = "rush.share.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
