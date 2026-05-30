package com.shub39.rush.shared.core.interfaces

import com.shub39.rush.shared.core.dataclasses.ExtractedColors

interface PaletteGenerator {
    suspend fun generatePaletteFromUrl(url: String): ExtractedColors
}