package com.shub39.rush.domain.backup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Export")
data class ExportSchema(
    val schemaVersion: Int = 3,
    val songs: List<SongSchema>
)

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