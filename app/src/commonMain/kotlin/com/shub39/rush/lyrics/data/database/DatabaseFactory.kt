package com.shub39.rush.lyrics.data.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<SongDatabase>
}