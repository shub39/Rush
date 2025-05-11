package com.shub39.rush.lyrics.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<SongDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(SongDatabase.DB_NAME)

        return Room.databaseBuilder(appContext, dbFile.absolutePath)
    }
}