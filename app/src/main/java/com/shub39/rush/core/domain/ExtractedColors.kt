package com.shub39.rush.core.domain

import androidx.compose.ui.graphics.Color

data class ExtractedColors(
    val cardBackgroundDominant: Color = Color.DarkGray,
    val cardContentDominant: Color = Color.White,
    val cardBackgroundMuted: Color = Color.DarkGray,
    val cardContentMuted: Color = Color.LightGray
)
