package com.shub39.rush.lyrics.domain

data class Song(
    val id: Long,
    val title: String,
    val artists: String,
    val lyrics: String,
    val album: String?,
    val sourceUrl: String,
    val artUrl: String?,
    val syncedLyrics: String?,
    val dateAdded: Long
)
