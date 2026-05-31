package com.shub39.rush.shared.logic.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.shub39.rush.shared.core.interfaces.LyricsPagePreferences
import com.shub39.rush.shared.core.interfaces.OtherPreferences
import com.shub39.rush.shared.core.interfaces.SharePagePreferences
import com.shub39.rush.shared.logic.database.DatabaseFactory
import com.shub39.rush.shared.logic.database.SongDao
import com.shub39.rush.shared.logic.database.SongDatabase
import com.shub39.rush.shared.logic.datastore.DatastoreFactory
import com.shub39.rush.shared.logic.datastore.LyricsPagePreferencesImpl
import com.shub39.rush.shared.logic.datastore.OtherPreferencesImpl
import com.shub39.rush.shared.logic.datastore.SharePagePreferencesImpl
import com.shub39.rush.shared.logic.network.ImageLoaderFactory
import coil3.ImageLoader
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.shub39.rush.shared.logic")
class DataModule {
    @Single fun provideAppDb(dbFactory: DatabaseFactory): SongDatabase = dbFactory.create().build()

    @Single fun provideSongDao(db: SongDatabase): SongDao = db.songDao()

    @Single fun provideImageLoader(factory: ImageLoaderFactory): ImageLoader = factory.create()

    @Single
    @Named("LyricsPage")
    fun provideLyricsPagePreferences(datastoreFactory: DatastoreFactory): DataStore<Preferences> =
        datastoreFactory.getLyricsPagePreferencesDataStore()

    @Single
    @Named("SharePage")
    fun provideSharePagePreferences(datastoreFactory: DatastoreFactory): DataStore<Preferences> =
        datastoreFactory.getSharePagePreferencesDataStore()

    @Single
    @Named("Other")
    fun provideOtherPreferences(datastoreFactory: DatastoreFactory): DataStore<Preferences> =
        datastoreFactory.getOtherPreferencesDataStore()

    @Single
    fun provideOtherPreferencesImpl(
        @Named("Other") dataStore: DataStore<Preferences>
    ): OtherPreferences = OtherPreferencesImpl(dataStore)

    @Single
    fun provideSharePagePreferencesImpl(
        @Named("SharePage") dataStore: DataStore<Preferences>
    ): SharePagePreferences = SharePagePreferencesImpl(dataStore)

    @Single
    fun provideLyricsPagePreferencesImpl(
        @Named("LyricsPage") dataStore: DataStore<Preferences>
    ): LyricsPagePreferences = LyricsPagePreferencesImpl(dataStore)
}