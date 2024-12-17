package com.shub39.rush.share

import androidx.compose.runtime.Immutable
import com.shub39.rush.core.domain.ExtractedColors

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