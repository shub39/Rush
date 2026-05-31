package com.shub39.rush.shared.logic.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<SongDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val useHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Kovert")
            os.contains("mac") -> File(useHome, "Library/Application Support/Kovert")
            else -> File(useHome, ".local/share/Kovert")
        }
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        val dbFile = File(appDataDir, SongDatabase.DB_NAME)

        return Room.databaseBuilder(dbFile.absolutePath)
    }
}