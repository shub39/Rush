/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.logic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toArgb
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowConversionToBitmap
import coil3.toBitmap
import com.kmpalette.palette.graphics.Palette
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.interfaces.PaletteGenerator
import org.koin.core.annotation.Single

@Single(binds = [PaletteGenerator::class])
actual class PaletteGeneratorImpl(private val imageLoader: ImageLoader) : PaletteGenerator {
    override suspend fun generatePaletteFromUrl(url: String): ExtractedColors {
        val request =
            ImageRequest.Builder(PlatformContext.INSTANCE)
                .data(url)
                .allowConversionToBitmap(true)
                .build()
        val result =
            (imageLoader.execute(request) as? SuccessResult)
                ?.image
                ?.toBitmap()
                ?.asComposeImageBitmap()

        return result?.let { bitmap ->
            val colors = Palette.from(bitmap).generate()

            ExtractedColors(
                cardBackgroundDominant =
                    Color(
                            colors.vibrantSwatch?.rgb
                                ?: colors.lightVibrantSwatch?.rgb
                                ?: colors.darkVibrantSwatch?.rgb
                                ?: colors.dominantSwatch?.rgb
                                ?: Color.DarkGray.toArgb()
                        )
                        .copy(alpha = 1f)
                        .toArgb(),
                cardContentDominant =
                    Color(
                            colors.vibrantSwatch?.bodyTextColor
                                ?: colors.lightVibrantSwatch?.bodyTextColor
                                ?: colors.darkVibrantSwatch?.bodyTextColor
                                ?: colors.dominantSwatch?.bodyTextColor
                                ?: Color.White.toArgb()
                        )
                        .copy(alpha = 1f)
                        .toArgb(),
                cardBackgroundMuted =
                    Color(
                            colors.mutedSwatch?.rgb
                                ?: colors.darkMutedSwatch?.rgb
                                ?: colors.lightMutedSwatch?.rgb
                                ?: Color.DarkGray.toArgb()
                        )
                        .copy(alpha = 1f)
                        .toArgb(),
                cardContentMuted =
                    Color(
                            colors.mutedSwatch?.bodyTextColor
                                ?: colors.darkMutedSwatch?.bodyTextColor
                                ?: colors.lightMutedSwatch?.bodyTextColor
                                ?: Color.White.toArgb()
                        )
                        .copy(alpha = 1f)
                        .toArgb(),
            )
        } ?: ExtractedColors()
    }
}
