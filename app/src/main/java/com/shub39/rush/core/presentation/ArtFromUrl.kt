package com.shub39.rush.core.presentation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.shub39.rush.R
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Music

// General Image Composable
@Composable
fun ArtFromUrl(
    imageUrl: Any?,
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
        previewPlaceholder = painterResource(R.drawable.genius),
        failure = {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Music,
                contentDescription = "Placeholder"
            )
        }
    )
}