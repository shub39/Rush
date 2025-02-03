package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.PaletteStyle
import com.shub39.rush.core.domain.CardColors

data class Theme(
    val seedColor: Int = Color.White.toArgb(),
    val useDarkTheme: Boolean? = null,
    val withAmoled: Boolean = false,
    val style: PaletteStyle = PaletteStyle.TonalSpot,
    val hypnoticCanvas: Boolean = true,
    val lyricsColor: String = CardColors.MUTED.color,
)