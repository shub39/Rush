package com.shub39.rush.shared.logic.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import org.koin.core.annotation.Single

@Single
actual class DatabaseFactory (private val context: Context) {
    actual fun create(): RoomDatabase.Builder<SongDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(SongDatabase.DB_NAME)

        return Room.databaseBuilder(appContext, dbFile.absolutePath)
    }
}