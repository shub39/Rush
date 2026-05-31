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
package com.shub39.rush.shared.logic.backup

import android.content.Context
import androidx.core.net.toUri
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.backup.ExportSchema
import com.shub39.rush.shared.core.backup.RestoreFailedException
import com.shub39.rush.shared.core.backup.RestoreRepo
import com.shub39.rush.shared.core.backup.RestoreResult
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.logic.mappers.toSong
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [RestoreRepo::class])
actual class RestoreImpl(val songRepo: SongRepository, val context: Context) : RestoreRepo {
    override suspend fun restoreSongs(path: String): RestoreResult =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val file = File(context.cacheDir, "temp.json")

                try {
                    context.contentResolver.openInputStream(path.toUri()).use { input ->
                        file.outputStream().use { output -> input?.copyTo(output) }
                    }

                    val json = Json { ignoreUnknownKeys = true }

                    val jsonDeserialized = json.decodeFromString<ExportSchema>(file.readText())

                    jsonDeserialized.songs.map { it.toSong() }.forEach { songRepo.insertSong(it) }
                } finally {
                    file.delete()
                }

                RestoreResult.Success
            } catch (e: IllegalArgumentException) {
                RushLogger.e("RestoreImpl", "Error restoring files", e)

                RestoreResult.Failure(RestoreFailedException.InvalidFile)
            } catch (e: SerializationException) {
                RushLogger.e("RestoreImpl", "Error restoring files", e)

                RestoreResult.Failure(RestoreFailedException.OldSchema)
            }
        }
}
