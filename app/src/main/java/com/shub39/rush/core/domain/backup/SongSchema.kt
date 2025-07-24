package com.shub39.rush.core.domain.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Song")
data class SongSchema(
    val id: Long,
    val title: String,
    val artists: String,
    val lyrics: String,
    val album: String?,
    val sourceUrl: String,
    val artUrl: String?,
    val syncedLyrics: String?,
    val geniusLyrics: String?,
    val dateAdded: Long = 0
)