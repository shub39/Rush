package com.shub39.rush.ui.page.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ArtFromUrl(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = koinInject()
) {
    CoilImage(
        imageModel = { imageUrl },
        modifier = modifier,
        imageLoader = { imageLoader },
        component = rememberImageComponent {
            +ShimmerPlugin(
                Shimmer.Resonate(
                    baseColor = MaterialTheme.colorScheme.surface,
                    highlightColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        imageOptions = ImageOptions(
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop
        ),
        failure = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_landscape_24),
                contentDescription = null
            )
        }
    )
}