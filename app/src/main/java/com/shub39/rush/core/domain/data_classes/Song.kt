package com.shub39.rush.core.domain.data_classes

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
    val dateAdded: Long
)
