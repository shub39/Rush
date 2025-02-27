package com.shub39.rush.lyrics.presentation.share

import com.shub39.rush.core.domain.CardColors
import com.shub39.rush.core.domain.CardFit
import com.shub39.rush.core.domain.CardTheme
import com.shub39.rush.core.domain.CornerRadius

sealed interface SharePageAction {
    data class OnUpdateCardTheme(val theme: CardTheme) : SharePageAction
    data class OnUpdateCardColor(val color: CardColors) : SharePageAction
    data class OnUpdateCardFit(val fit: CardFit) : SharePageAction
    data class OnUpdateCardRoundness(val roundness: CornerRadius) : SharePageAction
    data class OnUpdateCardContent(val color: Int): SharePageAction
    data class OnUpdateCardBackground(val color: Int): SharePageAction
}