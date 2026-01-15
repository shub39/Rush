package com.shub39.rush.presentation.components

import android.graphics.Bitmap
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.createBitmap
import com.shub39.rush.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

/**
 * A general-purpose composable for displaying an image from a URL.
 * In case of a failure to load the image, a placeholder icon is displayed.
 *
 * @param imageUrl The URL of the image to be displayed. Can be null.
 * @param modifier The modifier to be applied to this composable.
 * @param contentScale The scaling algorithm to be used to fit the image in the given bounds.
 * @param baseColor The base color for the shimmer animation.
 * @param highlightColor The highlight color for the shimmer animation.
 */
@Composable
fun ArtFromUrl(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    baseColor: Color = MaterialTheme.colorScheme.surface,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
) {
    CoilImage(
        imageModel = { imageUrl },
        modifier = modifier,
        component = rememberImageComponent {
            +ShimmerPlugin(
                Shimmer.Resonate(
                    baseColor = baseColor,
                    highlightColor = highlightColor
                )
            )
        },
        imageOptions = ImageOptions(
            alignment = Alignment.Center,
            contentScale = contentScale
        ),
        previewPlaceholder = getPlaceholder(),
        failure = {
            Icon(
                painter = painterResource(R.drawable.library_music),
                contentDescription = "Placeholder"
            )
        }
    )
}

private fun getPlaceholder(): Painter {
    return BitmapPainter(
        createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ).apply { eraseColor(android.graphics.Color.RED) }.asImageBitmap()
    )
}