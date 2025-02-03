package com.shub39.rush.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.lyrics.data.database.DatabaseFactory
import com.shub39.rush.lyrics.data.database.SongDatabase
import com.shub39.rush.app.RushViewModel
import com.shub39.rush.core.data.DatastoreFactory
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.core.data.RushDatastore
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.data.backup.export.ExportImpl
import com.shub39.rush.lyrics.data.backup.restore.RestoreImpl
import com.shub39.rush.lyrics.data.network.GeniusApi
import com.shub39.rush.lyrics.data.network.LrcLibApi
import com.shub39.rush.lyrics.data.network.GeniusScraper
import com.shub39.rush.lyrics.presentation.setting.SettingsVM
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val rushModules = module {
    singleOf(::DatabaseFactory)
    single {
        get<DatabaseFactory>()
            .create()
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<SongDatabase>().songDao() }
    single { HttpClientFactory.create() }

    singleOf(::GeniusScraper)
    singleOf(::GeniusApi)
    singleOf(::LrcLibApi)

    singleOf(::RushRepository).bind<SongRepo>()
    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()

    singleOf(::DatastoreFactory)
    single { get<DatastoreFactory>().getPreferencesDataStore() }
    singleOf(::RushDatastore)

    viewModelOf(::RushViewModel)
    viewModelOf(::SettingsVM)

    singleOf(::provideImageLoader)
}