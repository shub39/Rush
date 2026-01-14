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
 * @property instrumental A boolean flag indicating whether the song is instrumental (has no lyrics).
 * @property plainLyrics The non-synchronized (plain text) lyrics of the song, if available. Null if not.
 * @property syncedLyrics The synchronized (LRC format) lyrics of the song, if available. Null if not.
 */
data class LrcLibSong (
    val id: Int,
    val name: String,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Double,
    val instrumental: Boolean,
    val plainLyrics: String?,
    val syncedLyrics: String?
)