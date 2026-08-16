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
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Single
actual class DatastoreFactory {
    actual fun getLyricsPagePreferencesDataStore(): DataStore<Preferences> {
        return getDatastore(LYRICS_DATASTORE)
    }

    actual fun getOtherPreferencesDataStore(): DataStore<Preferences> {
        return getDatastore(OTHER_DATASTORE)
    }

    actual fun getSharePagePreferencesDataStore(): DataStore<Preferences> {
        return getDatastore(SHARE_DATASTORE)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getDatastore(fileName: String): DataStore<Preferences> {
        return createDataStore {
            val directory =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            requireNotNull(directory).path + "/$fileName"
        }
    }
}
