package com.shub39.rush.ui.page.share

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SharePageState(
    val songDetails: SongDetails = SongDetails(),
    val selectedLines: Map<Int, String> = emptyMap(),
    val extractedColors: ExtractedColors = ExtractedColors()
)

data class SongDetails(
    val title: String = "",
    val artist: String = "",
    val album: String? = null,
    val artUrl: String = ""
)

data class ExtractedColors(
    val cardBackgroundDominant: Color = Color.DarkGray,
    val cardContentDominant: Color = Color.White,
    val cardBackgroundMuted: Color = Color.DarkGray,
    val cardContentMuted: Color = Color.LightGray
)
