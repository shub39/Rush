package com.shub39.rush.di

import com.shub39.rush.lyrics.data.database.SongDatabase
import com.shub39.rush.lyrics.presentation.RushViewModel
import com.shub39.rush.lyrics.data.repository.RushRepository
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.data.backup.export.ExportImpl
import com.shub39.rush.lyrics.domain.ExportRepo
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val rushModules = module {
    single { SongDatabase.getDatabase(get()) }
    single { get<SongDatabase>().songDao() }

    singleOf(::RushRepository).bind<SongRepo>()
    singleOf(::ExportImpl).bind<ExportRepo>()

    viewModelOf(::RushViewModel)

    single { provideImageLoader(get()) }
}