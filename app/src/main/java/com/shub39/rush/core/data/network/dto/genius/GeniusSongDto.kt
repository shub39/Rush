package com.shub39.rush.core.data.network.dto.genius

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeniusSongDto (
    val response: SongResponse
)

@Serializable
data class SongResponse (
    val song: Song
)

@Serializable
data class Song (
    @SerialName("artist_names")
    val artistNames: String,

    val id: Long,

    val url: String,

    val path: String,

    @SerialName("song_art_image_url")
    val songArtImageURL: String?,

    val title: String,

    val album: Album?,
)

@Serializable
data class Album (
    val name: String,
)