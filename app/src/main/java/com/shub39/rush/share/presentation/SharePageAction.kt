package com.shub39.rush.share.presentation

sealed interface SharePageAction {
    data class UpdateExtractedColors(val colors: ExtractedColors) : SharePageAction
}