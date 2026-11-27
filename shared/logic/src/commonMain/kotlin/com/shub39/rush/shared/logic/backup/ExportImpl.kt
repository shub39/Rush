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

import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.backup.EXPORT_SCHEMA_VERSION
import com.shub39.rush.shared.core.backup.ExportRepo
import com.shub39.rush.shared.core.backup.SongSchema
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.logic.mappers.toSongSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [ExportRepo::class])
class ExportImpl(private val songRepo: SongRepository) : ExportRepo {
    /**
     * The envelope is written by hand so the songs can be appended one at a time; serialising an
     * [com.shub39.rush.shared.core.backup.ExportSchema] would materialise the whole document, which
     * is exactly what this class exists to avoid. Keep it in sync with that class.
     */
    private val documentStart = """{"schemaVersion":$EXPORT_SCHEMA_VERSION,"songs":["""

    private val documentEnd = "]}"

    override suspend fun exportToJson(write: (chunk: String) -> Unit): Boolean =
        withContext(Dispatchers.IO) {
            try {
                write(documentStart)

                var offset = 0
                var isFirstSong = true

                while (true) {
                    // Paged, so the songs are not all pulled from the database at once either.
                    val page = songRepo.getSongsPage(limit = PAGE_SIZE, offset = offset)
                    if (page.isEmpty()) break

                    for (song in page) {
                        if (!isFirstSong) write(",")

                        write(Json.encodeToString(SongSchema.serializer(), song.toSongSchema()))

                        isFirstSong = false
                    }

                    offset += page.size
                }

                write(documentEnd)

                true
            } catch (e: Exception) {
                RushLogger.e("ExportImpl", "Error exporting", e)

                false
            }
        }

    private companion object {
        /** Small enough to stay negligible on the heap, large enough to keep the cursor cheap. */
        const val PAGE_SIZE = 32
    }
}
