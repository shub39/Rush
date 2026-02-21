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
package com.shub39.rush.data.backup

import android.util.Log
import com.shub39.rush.data.mappers.toSongSchema
import com.shub39.rush.domain.backup.ExportRepo
import com.shub39.rush.domain.backup.ExportSchema
import com.shub39.rush.domain.interfaces.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime

@Single(binds = [ExportRepo::class])
class ExportImpl(private val songRepo: SongRepository) : ExportRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun exportToJson(): String? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val songsData = songRepo.getAllSongs().map { it.toSongSchema() }

                Json.encodeToString(ExportSchema(schemaVersion = 3, songs = songsData))
            } catch (e: Exception) {
                Log.wtf("ExportImpl", e)

                null
            }
        }
}
