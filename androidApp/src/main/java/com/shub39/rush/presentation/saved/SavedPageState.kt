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
package com.shub39.rush.presentation.saved

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.domain.dataclasses.ExtractedColors
import com.shub39.rush.domain.dataclasses.Song
import com.shub39.rush.domain.dataclasses.SongUi
import com.shub39.rush.domain.enums.SortOrder

@Immutable
@Stable
data class SavedPageState(
    val extractedColors: ExtractedColors = ExtractedColors(),
    val currentSong: SongUi? = null,
    val autoChange: Boolean = false,
    val songsByTime: List<Song> = emptyList(),
    val songsAsc: List<Song> = emptyList(),
    val songsDesc: List<Song> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DATE_ADDED,
)
