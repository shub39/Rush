package com.shub39.rush.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.shub39.rush.data.DatastoreFactory
import com.shub39.rush.data.HttpClientFactory
import com.shub39.rush.data.LyricsPagePreferencesImpl
import com.shub39.rush.data.OtherPreferencesImpl
import com.shub39.rush.data.SharePagePreferencesImpl
import com.shub39.rush.data.database.DatabaseFactory
import com.shub39.rush.data.database.SongDao
import com.shub39.rush.data.database.SongDatabase
import com.shub39.rush.domain.interfaces.LyricsPagePreferences
import com.shub39.rush.domain.interfaces.OtherPreferences
import com.shub39.rush.domain.interfaces.SharePagePreferences
import io.ktor.client.HttpClient
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.shub39.rush")
class RushModules {
    @Single
    fun provideAppDb(dbFactory: DatabaseFactory): SongDatabase = dbFactory.create().build()

    @Single
    fun provideSongDao(db: SongDatabase): SongDao = db.songDao()

    @Single
    fun provideHttpClient(): HttpClient = HttpClientFactory.create()

    @Single
    fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }

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
    fun provideOtherPreferencesImpl(@Named("Other") dataStore: DataStore<Preferences>): OtherPreferences =
        OtherPreferencesImpl(dataStore)

    @Single
    fun provideSharePagePreferencesImpl(@Named("SharePage") dataStore: DataStore<Preferences>): SharePagePreferences =
        SharePagePreferencesImpl(dataStore)

    @Single
    fun provideLyricsPagePreferencesImpl(@Named("LyricsPage") dataStore: DataStore<Preferences>): LyricsPagePreferences =
        LyricsPagePreferencesImpl(dataStore)
}