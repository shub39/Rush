package com.shub39.rush.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual class DatastoreFactory {
    private val os = System.getProperty("os.name").lowercase()
    val userHome: String? = System.getProperty("user.home")
    val appDataDir = when {
        os.contains("win") -> File(System.getenv("APPDATA"), "Rush")
        os.contains("mac") -> File(userHome, "Library/Application Support/Rush")
        else -> File(userHome, ".local/share/Rush")
    }


    actual fun getLyricsPagePreferencesDataStore() : DataStore<Preferences> {
        if (!appDataDir.exists()) appDataDir.mkdirs()

        val dbFile = File(appDataDir, LYRICS_DATASTORE)
        return createDataStore(
            producePath = { dbFile.absolutePath }
        )
    }

    actual fun getOtherPreferencesDataStore(): DataStore<Preferences> {
        if (!appDataDir.exists()) appDataDir.mkdirs()

        val dbFile = File(appDataDir, OTHER_DATASTORE)
        return createDataStore(
            producePath = { dbFile.absolutePath }
        )
    }

    actual fun getSharePagePreferencesDataStore(): DataStore<Preferences> {
        if (!appDataDir.exists()) appDataDir.mkdirs()

        val dbFile = File(appDataDir, SHARE_DATASTORE)
        return createDataStore(
            producePath = { dbFile.absolutePath }
        )
    }
}