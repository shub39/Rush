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
    val url: String
)