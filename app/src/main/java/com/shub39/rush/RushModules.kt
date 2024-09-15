package com.shub39.rush

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.CachePolicy
import com.shub39.rush.viewmodel.RushViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val rushModules = module {
    viewModel { RushViewModel(get()) }
    single { provideImageLoader(get()) }
}

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