package com.shub39.rush.share

import com.shub39.rush.core.domain.enums.AlbumArtShape
import com.shub39.rush.core.domain.enums.CardColors
import com.shub39.rush.core.domain.enums.CardFit
import com.shub39.rush.core.domain.enums.CardTheme
import com.shub39.rush.core.domain.enums.CornerRadius
import com.shub39.rush.core.domain.enums.Fonts

sealed interface SharePageAction {
    data object OnShowPaywall : SharePageAction
    data class OnToggleRushBranding(val pref: Boolean) : SharePageAction
    data class OnUpdateAlbumArtShape(val shape: AlbumArtShape) : SharePageAction
    data class OnUpdateCardTheme(val theme: CardTheme) : SharePageAction
    data class OnUpdateCardColor(val color: CardColors) : SharePageAction
    data class OnUpdateCardFit(val fit: CardFit) : SharePageAction
    data class OnUpdateCardRoundness(val roundness: CornerRadius) : SharePageAction
    data class OnUpdateCardContent(val color: Int): SharePageAction
    data class OnUpdateCardBackground(val color: Int): SharePageAction
    data class OnUpdateCardFont(val font: Fonts) : SharePageAction
}