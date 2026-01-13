package com.shub39.rush.domain.dataclasses

/**
 * Represents the detailed information of a song.
 *
 * @property title The title of the song.
 * @property artist The name of the primary artist or band.
 * @property album The name of the album the song belongs to.
 * @property artUrl A URL pointing to the album cover art or song's artwork.
 */
data class SongDetails(
    val title: String,
    val artist: String,
    val album: String?,
    val artUrl: String
)