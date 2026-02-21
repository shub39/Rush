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
 * Represents a song entity within the application.
 *
 * @property id The unique identifier for the song.
 * @property title The title of the song.
 * @property artists A string containing the name(s) of the artist(s).
 * @property lyrics The plain text lyrics of the song.
 * @property album The name of the album the song belongs to, if available.
 * @property sourceUrl The URL of the source of lyrics
 * @property artUrl The URL for the album art or song cover, if available.
 * @property geniusLyrics The lyrics fetched from Genius
 * @property syncedLyrics The time-coded or synchronized lyrics
 * @property dateAdded The timestamp (in milliseconds) when the song was added to the library.
 */
data class Song(
    val id: Long,
    val title: String,
    val artists: String,
    val lyrics: String,
    val album: String?,
    val sourceUrl: String,
    val artUrl: String?,
    val geniusLyrics: String?,
    val syncedLyrics: String?,
    val dateAdded: Long,
)
