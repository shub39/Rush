package com.shub39.rush.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

expect class DatastoreFactory {
    fun getLyricsPagePreferencesDataStore() : DataStore<Preferences>
    fun getOtherPreferencesDataStore(): DataStore<Preferences>
    fun getSharePagePreferencesDataStore(): DataStore<Preferences>
}

internal const val LYRICS_DATASTORE = "rush.lyrics.preferences_pb"
internal const val OTHER_DATASTORE = "rush.other.preferences_pb"
internal const val SHARE_DATASTORE = "rush.share.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })