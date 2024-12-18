package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shub39.rush.core.domain.AppTheme
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.lyrics.presentation.saved.SortOrder

data class Settings(
    val cardFit: String = CardFit.STANDARD.type,
    val lyricsColor: String = CardColors.MUTED.color,
    val cardBackground: Int = Color.Black.toArgb(),
    val cardContent: Int = Color.White.toArgb(),
    val cardTheme: String = CardTheme.SPOTIFY.type,
    val cardColor: String = CardColors.VIBRANT.color,
    val cardRoundness: String = CornerRadius.ROUNDED.type,
    val sortOrder: String = SortOrder.TITLE_ASC.sortOrder,
    val toggleTheme: String = AppTheme.YELLOW.type,
    val maxLines: Int = 6,
)
