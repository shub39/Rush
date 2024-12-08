package com.shub39.rush.share

sealed interface SharePageAction {
    data class UpdateExtractedColors(val colors: ExtractedColors) : SharePageAction
}