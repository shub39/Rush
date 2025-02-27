package com.shub39.rush.lyrics.presentation.share

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.shub39.rush.core.data.ExtractedColors
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius

@Immutable
data class SharePageState(
    val songDetails: SongDetails = SongDetails(),
    val selectedLines: Map<Int, String> = emptyMap(),
    val extractedColors: ExtractedColors = ExtractedColors(),
    val cardColors: CardColors = CardColors.MUTED,
    val cardBackground: Int = Color.Gray.toArgb(),
    val cardContent: Int = Color.White.toArgb(),
    val cardFit: CardFit = CardFit.FIT,
    val cardRoundness: CornerRadius = CornerRadius.ROUNDED,
    val cardTheme: CardTheme = CardTheme.SPOTIFY
)