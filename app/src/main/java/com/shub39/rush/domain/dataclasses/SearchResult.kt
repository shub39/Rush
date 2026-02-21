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
package com.shub39.rush.domain.dataclasses

/**
 * Represents a single item returned from a search query.
 *
 * @property id A unique identifier for the track.
 * @property title The title of the track.
 * @property artist The name of the primary artist.
 * @property album The name of the album the track belongs to, if available.
 * @property artUrl The URL for the album or track artwork.
 * @property url The direct URL for streaming or downloading the track.
 */
data class SearchResult(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val artUrl: String,
    val url: String,
)
