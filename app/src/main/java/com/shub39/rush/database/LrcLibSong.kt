package com.shub39.rush.database

data class LrcLibSong (
    val id: Int,
    val name: String,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Double,
    val instrumental: Boolean,
    val plainLyrics: String,
    val syncedLyrics: String?
)