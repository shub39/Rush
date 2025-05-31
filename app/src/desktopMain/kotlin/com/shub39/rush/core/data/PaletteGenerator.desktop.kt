package com.shub39.rush.core.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toArgb
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.kmpalette.palette.graphics.Palette
import com.shub39.rush.core.domain.data_classes.ExtractedColors

actual class PaletteGenerator(
    private val imageLoader: ImageLoader
) {
    actual suspend fun generatePaletteFromUrl(url: String): ExtractedColors {
        val request = ImageRequest.Builder(PlatformContext.INSTANCE)
            .data(url)
            .build()
        val result = (imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()?.asComposeImageBitmap()

        return result?.let { bitmap ->
            val colors = Palette.from(bitmap).generate()

            ExtractedColors(
                cardBackgroundDominant =
                    Color(
                        colors.vibrantSwatch?.rgb ?: colors.lightVibrantSwatch?.rgb
                        ?: colors.darkVibrantSwatch?.rgb ?: colors.dominantSwatch?.rgb
                        ?: Color.DarkGray.toArgb()
                    ),
                cardContentDominant =
                    Color(
                        colors.vibrantSwatch?.bodyTextColor
                            ?: colors.lightVibrantSwatch?.bodyTextColor
                            ?: colors.darkVibrantSwatch?.bodyTextColor
                            ?: colors.dominantSwatch?.bodyTextColor
                            ?: Color.White.toArgb()
                    ),
                cardBackgroundMuted =
                    Color(
                        colors.mutedSwatch?.rgb ?: colors.darkMutedSwatch?.rgb
                        ?: colors.lightMutedSwatch?.rgb ?: Color.DarkGray.toArgb()
                    ),
                cardContentMuted =
                    Color(
                        colors.mutedSwatch?.bodyTextColor
                            ?: colors.darkMutedSwatch?.bodyTextColor
                            ?: colors.lightMutedSwatch?.bodyTextColor
                            ?: Color.White.toArgb()
                    )
            )
        } ?: ExtractedColors()
    }
}