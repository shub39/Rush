package com.shub39.rush.ui.page.share

sealed interface SharePageAction {
    data class UpdateExtractedColors(val colors: ExtractedColors) : SharePageAction
}