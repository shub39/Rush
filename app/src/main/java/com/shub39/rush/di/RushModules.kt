package com.shub39.rush.di

import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.lyrics.data.database.SongDatabase
import com.shub39.rush.lyrics.presentation.RushViewModel
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.core.data.RushDatastore
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.data.backup.export.ExportImpl
import com.shub39.rush.lyrics.data.backup.restore.RestoreImpl
import com.shub39.rush.lyrics.data.network.GeniusApi
import com.shub39.rush.lyrics.data.network.GeniusScraper
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val rushModules = module {
    single { SongDatabase.getDatabase(get()) }
    single { get<SongDatabase>().songDao() }
    single { HttpClientFactory.create() }
    singleOf(::GeniusScraper)
    singleOf(::GeniusApi)

    singleOf(::RushRepository).bind<SongRepo>()
    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()
    singleOf(::RushDatastore)

    viewModelOf(::RushViewModel)

    single { provideImageLoader(get()) }
}