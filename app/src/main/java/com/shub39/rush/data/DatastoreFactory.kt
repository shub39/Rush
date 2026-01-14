package com.shub39.rush.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

@Single
class DatastoreFactory(private val context: Context) {
    fun getLyricsPagePreferencesDataStore() : DataStore<Preferences> = createDataStore (
        producePath = { context.filesDir.resolve(LYRICS_DATASTORE).absolutePath }
    )

    fun getOtherPreferencesDataStore(): DataStore<Preferences> = createDataStore (
        producePath = { context.filesDir.resolve(OTHER_DATASTORE).absolutePath }
    )

    fun getSharePagePreferencesDataStore(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve(SHARE_DATASTORE).absolutePath }
    )
}

internal const val LYRICS_DATASTORE = "rush.lyrics.preferences_pb"
internal const val OTHER_DATASTORE = "rush.other.preferences_pb"
internal const val SHARE_DATASTORE = "rush.share.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })