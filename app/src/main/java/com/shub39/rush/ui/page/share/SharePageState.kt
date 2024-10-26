package com.shub39.rush.ui.page.share

import androidx.compose.runtime.Immutable

@Immutable
data class SharePageState(
    val songDetails: SongDetails = SongDetails(),
    val selectedLines: Map<Int, String> = emptyMap()
)

data class SongDetails(
    val title: String = "",
    val artist: String = "",
    val album: String? = null,
    val artUrl: String = ""
)
