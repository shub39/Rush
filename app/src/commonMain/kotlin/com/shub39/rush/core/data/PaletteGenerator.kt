package com.shub39.rush.core.data

import com.shub39.rush.core.domain.data_classes.ExtractedColors

expect class PaletteGenerator {
    suspend fun generatePaletteFromUrl(url: String): ExtractedColors
}