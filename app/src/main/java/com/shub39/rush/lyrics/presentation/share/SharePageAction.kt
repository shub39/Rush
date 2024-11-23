package com.shub39.rush.lyrics.presentation.share

sealed interface SharePageAction {
    data class UpdateExtractedColors(val colors: ExtractedColors) : SharePageAction
}