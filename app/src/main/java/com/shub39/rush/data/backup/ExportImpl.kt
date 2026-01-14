package com.shub39.rush.data.backup

import android.util.Log
import com.shub39.rush.data.mappers.toSongSchema
import com.shub39.rush.domain.backup.ExportRepo
import com.shub39.rush.domain.backup.ExportSchema
import com.shub39.rush.domain.interfaces.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime

@Single(binds = [ExportRepo::class])
class ExportImpl(
    private val songRepo: SongRepository
) : ExportRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun exportToJson(): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val songsData = songRepo.getAllSongs().map { it.toSongSchema() }

            Json.encodeToString(
                ExportSchema(
                    schemaVersion = 3,
                    songs = songsData
                )
            )
        } catch (e: Exception) {
            Log.wtf("ExportImpl", e)

            null
        }
    }
}