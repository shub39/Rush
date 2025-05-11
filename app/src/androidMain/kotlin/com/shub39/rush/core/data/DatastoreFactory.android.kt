package com.shub39.rush.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual class DatastoreFactory(private val context: Context) {
    actual fun getLyricsPagePreferencesDataStore() : DataStore<Preferences> = createDataStore (
        producePath = { context.filesDir.resolve(LYRICS_DATASTORE).absolutePath }
    )

    actual fun getOtherPreferencesDataStore(): DataStore<Preferences> = createDataStore (
        producePath = { context.filesDir.resolve(OTHER_DATASTORE).absolutePath }
    )

    actual fun getSharePagePreferencesDataStore(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve(SHARE_DATASTORE).absolutePath }
    )
}