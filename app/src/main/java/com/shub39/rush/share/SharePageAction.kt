package com.shub39.rush.share

sealed interface SharePageAction {
    data class OnUpdateCardTheme(val theme: String) : SharePageAction
    data class OnUpdateCardColor(val color: String) : SharePageAction
    data class OnUpdateCardFit(val fit: String) : SharePageAction
    data class OnUpdateCardRoundness(val roundness: String) : SharePageAction
    data class OnUpdateCardContent(val color: Int): SharePageAction
    data class OnUpdateCardBackground(val color: Int): SharePageAction
}