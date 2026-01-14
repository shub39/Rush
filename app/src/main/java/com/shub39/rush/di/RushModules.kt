package com.shub39.rush.di

import com.shub39.rush.billing.BillingHandler
import com.shub39.rush.billing.BillingHandlerImpl
import com.shub39.rush.data.DatastoreFactory
import com.shub39.rush.data.HttpClientFactory
import com.shub39.rush.data.LyricsPagePreferencesImpl
import com.shub39.rush.data.OtherPreferencesImpl
import com.shub39.rush.data.PaletteGenerator
import com.shub39.rush.data.SharePagePreferencesImpl
import com.shub39.rush.data.backup.ExportImpl
import com.shub39.rush.data.backup.RestoreImpl
import com.shub39.rush.data.database.DatabaseFactory
import com.shub39.rush.data.database.SongDatabase
import com.shub39.rush.data.network.GeniusApi
import com.shub39.rush.data.network.GeniusScraper
import com.shub39.rush.data.network.LrcLibApi
import com.shub39.rush.data.repository.RushRepository
import com.shub39.rush.domain.backup.ExportRepo
import com.shub39.rush.domain.backup.RestoreRepo
import com.shub39.rush.domain.interfaces.LyricsPagePreferences
import com.shub39.rush.domain.interfaces.OtherPreferences
import com.shub39.rush.domain.interfaces.SharePagePreferences
import com.shub39.rush.domain.interfaces.SongRepository
import com.shub39.rush.viewmodels.GlobalVM
import com.shub39.rush.viewmodels.LyricsVM
import com.shub39.rush.viewmodels.SavedVM
import com.shub39.rush.viewmodels.SearchSheetVM
import com.shub39.rush.viewmodels.SettingsVM
import com.shub39.rush.viewmodels.ShareVM
import com.shub39.rush.viewmodels.SharedStates
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val rushModules = module {
    // billing
    singleOf(::BillingHandlerImpl).bind<BillingHandler>()

    // factories, listeners, generators and backup stuff
    singleOf(::DatabaseFactory)
    singleOf(::DatastoreFactory)
    single { get<DatabaseFactory>().create().build() }
    singleOf(::ExportImpl).bind<ExportRepo>()
    singleOf(::RestoreImpl).bind<RestoreRepo>()
    singleOf(::PaletteGenerator)

    // android specific imageloader with cache
    singleOf(::provideImageLoader)

    // Database
    single { get<SongDatabase>().songDao() }
    single { HttpClientFactory.create() }

    // Network Stuff
    singleOf(::GeniusScraper)
    singleOf(::GeniusApi)
    singleOf(::LrcLibApi)

    // Repositories and backup stuff
    singleOf(::RushRepository).bind<SongRepository>()

    // Datastore
    single(named("LyricsPage")) { get<DatastoreFactory>().getLyricsPagePreferencesDataStore() }
    single(named("SharePage")) { get<DatastoreFactory>().getSharePagePreferencesDataStore() }
    single(named("Other")) { get<DatastoreFactory>().getOtherPreferencesDataStore() }
    single { OtherPreferencesImpl(get(named("Other"))) }.bind<OtherPreferences>()
    single { LyricsPagePreferencesImpl(get(named("LyricsPage"))) }.bind<LyricsPagePreferences>()
    single { SharePagePreferencesImpl(get(named("SharePage"))) }.bind<SharePagePreferences>()

    // ViewModels
    singleOf(::SharedStates)
    viewModelOf(::GlobalVM)
    viewModelOf(::SearchSheetVM)
    viewModelOf(::SavedVM)
    viewModelOf(::LyricsVM)
    viewModelOf(::SettingsVM)
    viewModelOf(::ShareVM)
}