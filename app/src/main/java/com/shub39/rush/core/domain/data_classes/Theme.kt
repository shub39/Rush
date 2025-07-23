package com.shub39.rush.core.domain.data_classes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.PaletteStyle
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.Fonts

data class Theme(
    val seedColor: Int = Color.White.toArgb(),
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val withAmoled: Boolean = false,
    val style: PaletteStyle = PaletteStyle.TonalSpot,
    val materialTheme: Boolean = false,
    val fonts: Fonts = Fonts.FIGTREE
)