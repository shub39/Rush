package com.shub39.rush.shared.logic.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.annotation.Single
import java.io.File

@Single
actual class DatastoreFactory {
    private val os = System.getProperty("os.name").lowercase()
    private val userHome = System.getProperty("user.home")
    private val appDataDir =
        when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Dharmik")
            os.contains("mac") -> File(userHome, "Library/Application Support/Dharmik")
            else -> File(userHome, ".local/share/Dharmik")
        }

    init {
        if (!appDataDir.exists()) appDataDir.mkdirs()
    }


    actual fun getLyricsPagePreferencesDataStore(): DataStore<Preferences> {
        val dbFile = File(appDataDir, LYRICS_DATASTORE)
        return createDataStore(producePath = { dbFile.absolutePath })
    }

    actual fun getOtherPreferencesDataStore(): DataStore<Preferences> {
        val dbFile = File(appDataDir, OTHER_DATASTORE)
        return createDataStore(producePath = { dbFile.absolutePath })
    }

    actual fun getSharePagePreferencesDataStore(): DataStore<Preferences> {
        val dbFile = File(appDataDir, SHARE_DATASTORE)
        return createDataStore(producePath = { dbFile.absolutePath })
    }
}