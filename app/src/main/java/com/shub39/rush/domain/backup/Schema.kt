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
package com.shub39.rush.domain.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Export")
data class ExportSchema(val schemaVersion: Int = 3, val songs: List<SongSchema>)

@Serializable
@SerialName("Song")
data class SongSchema(
    val id: Long,
    val title: String,
    val artists: String,
    val lyrics: String,
    val album: String?,
    val sourceUrl: String,
    val artUrl: String?,
    val syncedLyrics: String?,
    val geniusLyrics: String?,
    val dateAdded: Long = 0,
)
