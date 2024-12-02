package com.shub39.rush.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.CachePolicy
import com.shub39.rush.lyrics.data.database.SongDatabase
import com.shub39.rush.lyrics.presentation.RushViewModel
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.lyrics.domain.SongRepo
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val rushModules = module {
    single { SongDatabase.getDatabase(get()) }
    single { get<SongDatabase>().songDao() }

    singleOf(::RushRepository).bind<SongRepo>()

    viewModelOf(::RushViewModel)

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