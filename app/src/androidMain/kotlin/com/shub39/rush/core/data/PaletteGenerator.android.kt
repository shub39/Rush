package com.shub39.rush.core.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowConversionToBitmap
import coil3.request.allowHardware
import coil3.toBitmap
import com.kmpalette.palette.graphics.Palette
import com.shub39.rush.core.domain.data_classes.ExtractedColors

actual class PaletteGenerator(
    private val context: Context,
    private val imageLoader: ImageLoader
) {
    actual suspend fun generatePaletteFromUrl(url: String): ExtractedColors {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowConversionToBitmap(true)
            .allowHardware(false)
            .build()
        val result = (imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()?.asImageBitmap()

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