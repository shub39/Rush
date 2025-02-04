package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius
import com.shub39.rush.lyrics.presentation.saved.SortOrder

data class Settings(
    val cardContent: Int = Color.White.toArgb(),
    val maxLines: Int = 6,
    val cardFit: CardFit = CardFit.STANDARD,
    val lyricsColor: CardColors = CardColors.MUTED,
    val cardBackground: Int = Color.Black.toArgb(),
    val cardTheme: CardTheme = CardTheme.SPOTIFY,
    val cardColor: CardColors = CardColors.VIBRANT,
    val cardRoundness: CornerRadius = CornerRadius.ROUNDED,
    val sortOrder: String = SortOrder.TITLE_ASC.sortOrder,
    val hypnoticCanvas: Boolean = true,
    val onboardingDone: Boolean = true
)
