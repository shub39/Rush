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
package com.shub39.rush.shared.core.backup

interface ExportRepo {
    /**
     * Encodes the saved songs as one JSON document, handing it to [write] in small chunks instead
     * of returning a single string.
     *
     * A library of saved lyrics easily runs into tens of megabytes, and building that document in
     * memory needs several times its size on the heap, which is what used to make exports of large
     * libraries fail with an OutOfMemoryError. Chunking keeps the peak cost down to a single song,
     * so the caller can stream the chunks straight into a file.
     *
     * [write] runs on the IO dispatcher and must consume the chunk it is given; holding on to them
     * would defeat the point.
     *
     * @return `true` when the whole document was written, `false` if anything failed.
     */
    suspend fun exportToJson(write: (chunk: String) -> Unit): Boolean
}

sealed interface ExportState {
    /** Nothing has been written yet, or the last export was discarded. */
    data object Idle : ExportState

    data object Exporting : ExportState

    /** The last export finished and the file was written completely. */
    data object Exported : ExportState

    data object Error : ExportState
}
