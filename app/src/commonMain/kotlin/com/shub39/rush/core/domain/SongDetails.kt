package com.shub39.rush.core.domain

data class SongDetails(
    val title: String = "",
    val artist: String = "",
    val album: String? = null,
    val artUrl: String = ""
)