package com.shub39.rush.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val artists: String,
    val lyrics: String,
    val album: String?,
    val sourceUrl: String,
    val artUrl: String?,
    val syncedLyrics: String?,
    val geniusLyrics: String?
)