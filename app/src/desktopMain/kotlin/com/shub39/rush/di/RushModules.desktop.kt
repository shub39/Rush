package com.shub39.rush.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.shub39.rush.core.data.DatastoreFactory
import com.shub39.rush.core.data.PaletteGenerator
import com.shub39.rush.lyrics.data.backup.ExportImpl
import com.shub39.rush.lyrics.data.backup.RestoreImpl
import com.shub39.rush.lyrics.data.database.DatabaseFactory
import com.shub39.rush.lyrics.data.listener.MediaListenerImpl
import com.shub39.rush.lyrics.domain.MediaInterface
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DatabaseFactory)
    singleOf(::DatastoreFactory)
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()
    singleOf(::PaletteGenerator)
    singleOf(::MediaListenerImpl).bind<MediaInterface>()

    single {
        ImageLoader.Builder(PlatformContext.INSTANCE)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}