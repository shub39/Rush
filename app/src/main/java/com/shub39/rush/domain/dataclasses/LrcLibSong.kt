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
 * Represents a song entry fetched from the LRC-Lib API.
 *
 * @property id The unique identifier for the song in the LRC-Lib database.
 * @property name The full name of the song
 * @property trackName The official name of the track.
 * @property artistName The name of the primary artist(s).
 * @property albumName The name of the album.
 * @property duration The total duration of the song in seconds.
 * @property instrumental A boolean flag indicating whether the song is instrumental (has no
 *   lyrics).
 * @property plainLyrics The non-synchronized (plain text) lyrics of the song, if available. Null if
 *   not.
 * @property syncedLyrics The synchronized (LRC format) lyrics of the song, if available. Null if
 *   not.
 */
data class LrcLibSong(
    val id: Int,
    val name: String,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Double,
    val instrumental: Boolean,
    val plainLyrics: String?,
    val syncedLyrics: String?,
)
