package com.shub39.rush.di

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
    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()
    singleOf(::PaletteGenerator)
    singleOf(::MediaListenerImpl).bind<MediaInterface>()

    // android specific
    singleOf(::provideImageLoader)
}