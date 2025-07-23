package com.shub39.rush.lyrics.domain

data class SongUi(
    val id: Long = 0,
    val title: String = "",
    val artists: String = "",
    val album: String? = null,
    val sourceUrl: String = "",
    val artUrl: String? = null,
    val lyrics: List<Map.Entry<Int, String>> = emptyList(),
    val syncedLyrics: List<Lyric>? = null,
    val geniusLyrics: List<Map.Entry<Int, String>>? = null
)