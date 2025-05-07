package com.shub39.rush.core.presentation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.ImageLoader
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Music
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import rush.app.generated.resources.Res
import rush.app.generated.resources.rush_transparent

// General Image Composable
@Composable
fun ArtFromUrl(
    imageUrl: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
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
            contentScale = contentScale
        ),
        previewPlaceholder = painterResource(Res.drawable.rush_transparent),
        failure = {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Music,
                contentDescription = "Placeholder"
            )
        }
    )
}