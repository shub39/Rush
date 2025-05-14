package com.shub39.rush.lyrics.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<SongDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val useHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Rush")
            os.contains("mac") -> File(useHome, "Library/Application Support/Rush")
            else -> File(useHome, ".local/share/Rush")
        }
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        val dbFile = File(appDataDir, SongDatabase.DB_NAME)

        return Room.databaseBuilder(dbFile.absolutePath)
    }
}