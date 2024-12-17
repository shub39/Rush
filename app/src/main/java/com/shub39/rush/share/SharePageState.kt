package com.shub39.rush.share

import androidx.compose.runtime.Immutable
import com.shub39.rush.core.data.ExtractedColors
import com.shub39.rush.core.data.SongDetails

@Immutable
data class SharePageState(
    val songDetails: SongDetails = SongDetails(),
    val selectedLines: Map<Int, String> = emptyMap(),
    val extractedColors: ExtractedColors = ExtractedColors()
)