package com.shub39.rush.lyrics.data.network.dto.genius

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class GeniusSearchDto (
    val response: SearchResponse
)

@Serializable
data class SearchResponse (
    val hits: List<Hit>
)

@Serializable
data class Hit (
    val highlights: JsonArray,
    val index: String,
    val type: String,
    val result: Result
)

@Serializable
data class Result (
    @SerialName("_type")
    val type: String? = null,

    @SerialName("artist_names")
    val artistNames: String,

    @SerialName("full_title")
    val fullTitle: String,

    val id: Long,

    val instrumental: Boolean = false,

    @SerialName("release_date_components")
    val releaseDateComponents: ReleaseDateComponents? = null,

    @SerialName("song_art_image_url")
    val songArtImageURL: String,

    val title: String,

    val url: String,
)

@Serializable
data class ReleaseDateComponents (
    val year: Long,
    val month: Long? = null,
    val day: Long? = null
)