package com.shub39.rush.presentation.share

import com.shub39.rush.domain.enums.AlbumArtShape
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.CardFit
import com.shub39.rush.domain.enums.CardTheme
import com.shub39.rush.domain.enums.CornerRadius
import com.shub39.rush.domain.enums.Fonts

sealed interface SharePageAction {
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