package com.shub39.rush.di

import com.shub39.rush.core.data.DatastoreFactory
import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.core.data.LyricsPagePreferencesImpl
import com.shub39.rush.core.data.OtherPreferencesImpl
import com.shub39.rush.core.data.PaletteGenerator
import com.shub39.rush.core.data.SharePagePreferencesImpl
import com.shub39.rush.core.domain.LyricsPagePreferences
import com.shub39.rush.core.domain.OtherPreferences
import com.shub39.rush.core.domain.SharePagePreferences
import com.shub39.rush.lyrics.data.backup.ExportImpl
import com.shub39.rush.lyrics.data.backup.RestoreImpl
import com.shub39.rush.lyrics.data.database.DatabaseFactory
import com.shub39.rush.lyrics.data.database.SongDatabase
import com.shub39.rush.lyrics.data.listener.MediaListenerImpl
import com.shub39.rush.lyrics.data.network.GeniusApi
import com.shub39.rush.lyrics.data.network.GeniusScraper
import com.shub39.rush.lyrics.data.network.LrcLibApi
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.lyrics.domain.MediaInterface
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.viewmodels.LyricsVM
import com.shub39.rush.viewmodels.SavedVM
import com.shub39.rush.viewmodels.SearchSheetVM
import com.shub39.rush.viewmodels.SettingsVM
import com.shub39.rush.viewmodels.ShareVM
import com.shub39.rush.viewmodels.StateLayer
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val rushModules = module {
    // factories and stuff
    singleOf(::DatabaseFactory)
    singleOf(::DatastoreFactory)
    single { get<DatabaseFactory>().create().build() }
    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()
    singleOf(::PaletteGenerator)
    singleOf(::MediaListenerImpl).bind<MediaInterface>()

    // android specific
    singleOf(::provideImageLoader)

    // Database
    single { get<SongDatabase>().songDao() }
    single { HttpClientFactory.create() }

    // Network Stuff
    singleOf(::GeniusScraper)
    singleOf(::GeniusApi)
    singleOf(::LrcLibApi)

    // Repositories and backup stuff
    singleOf(::RushRepository).bind<SongRepo>()

    // Datastore
    single(named("LyricsPage")) { get<DatastoreFactory>().getLyricsPagePreferencesDataStore() }
    single(named("SharePage")) { get<DatastoreFactory>().getSharePagePreferencesDataStore() }
    single(named("Other")) { get<DatastoreFactory>().getOtherPreferencesDataStore() }
    single { OtherPreferencesImpl(get(named("Other"))) }.bind<OtherPreferences>()
    single { LyricsPagePreferencesImpl(get(named("LyricsPage"))) }.bind<LyricsPagePreferences>()
    single { SharePagePreferencesImpl(get(named("SharePage"))) }.bind<SharePagePreferences>()

    // ViewModels
    singleOf(::StateLayer)
    viewModelOf(::SearchSheetVM)
    viewModelOf(::SavedVM)
    viewModelOf(::LyricsVM)
    viewModelOf(::SettingsVM)
    viewModelOf(::ShareVM)
}