package com.shub39.rush.core.presentation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import com.shub39.rush.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import org.koin.compose.koinInject

// General Image Composable
@Composable
fun ArtFromUrl(
    imageUrl: Any?,
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surface,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    imageLoader: ImageLoader = koinInject()
) {
    CoilImage(
        imageModel = { imageUrl },
        modifier = modifier,
        imageLoader = { imageLoader },
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
            contentScale = ContentScale.Crop
        ),
        previewPlaceholder = painterResource(R.drawable.baseline_square_24),
        failure = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_landscape_24),
                contentDescription = null
            )
        }
    )
}