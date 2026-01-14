package com.shub39.rush.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseFactory(
    private val context: Context
) {
    fun create(): RoomDatabase.Builder<SongDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(SongDatabase.DB_NAME)

        return Room.databaseBuilder(appContext, dbFile.absolutePath)
    }
}