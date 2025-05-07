package com.shub39.rush.lyrics.domain

data class LrcLibSong (
    val id: Int,
    val name: String,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Double,
    val instrumental: Boolean,
    val plainLyrics: String?,
    val syncedLyrics: String?
)