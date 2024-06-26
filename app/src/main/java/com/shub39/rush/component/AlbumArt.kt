package com.shub39.rush.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.shub39.rush.R
import android.content.Context as Context


@Composable
fun ArtFromUrl(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: Int = R.drawable.round_music_note_24,
    imageLoader: ImageLoader
) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .apply {
                    placeholder(placeholder)
                    error(R.drawable.baseline_landscape_24)
                    crossfade(true)
                }
                .build(),
            imageLoader = imageLoader
        ),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}


@Composable
fun provideImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .crossfade(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}