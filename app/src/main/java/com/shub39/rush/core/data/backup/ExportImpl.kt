package com.shub39.rush.core.data.backup

import android.util.Log
import com.shub39.rush.core.data.mappers.toSongSchema
import com.shub39.rush.core.domain.SongRepo
import com.shub39.rush.core.domain.backup.ExportRepo
import com.shub39.rush.core.domain.backup.ExportSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

class ExportImpl(
    private val songRepo: SongRepo
) : ExportRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun exportToJson(): String? = withContext(Dispatchers.IO) {
        try {
            val songsData = songRepo.getAllSongs().map { it.toSongSchema() }
            return@withContext Json.Default.encodeToString(
                ExportSchema(
                    schemaVersion = 3,
                    songs = songsData
                )
            )
        } catch (e: Exception) {
            Log.wtf("ExportImpl", e)

            return@withContext null
        }
    }
}