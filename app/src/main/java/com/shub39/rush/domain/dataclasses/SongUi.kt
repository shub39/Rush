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
 * Represents the state of a song for display in the user interface.
 *
 * @property id The unique identifier for the song.
 * @property title The title of the song.
 * @property artists A comma-separated string of artists for the song.
 * @property album The name of the album the song belongs to, if available.
 * @property sourceUrl The URL to the audio source of the song.
 * @property artUrl The URL to the album art or song cover image, if available.
 * @property lyrics A list of un synchronized lyric lines, represented as a map entry of line number
 *   to text.
 * @property syncedLyrics A list of synchronized lyric objects, each containing a timestamp and
 *   text, if available.
 * @property geniusLyrics A list of lyric lines sourced from Genius, represented as a map entry of
 *   line number to text, if available.
 */
data class SongUi(
    val id: Long,
    val title: String,
    val artists: String,
    val album: String?,
    val sourceUrl: String,
    val artUrl: String?,
    val lyrics: List<Map.Entry<Int, String>>,
    val syncedLyrics: List<Lyric>?,
    val geniusLyrics: List<Map.Entry<Int, String>>?,
)
