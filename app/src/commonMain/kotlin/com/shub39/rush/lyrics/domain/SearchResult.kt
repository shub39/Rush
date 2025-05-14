package com.shub39.rush.lyrics.domain

data class SearchResult(
    val title: String,
    val artist: String,
    val album: String?,
    val artUrl: String,
    val url: String,
    val id: Long
)