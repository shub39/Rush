package com.shub39.rush.lyrics.data.backup

import com.shub39.rush.lyrics.data.mappers.toSong
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.domain.backup.ExportSchema
import com.shub39.rush.lyrics.domain.backup.RestoreFailedException
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

actual class RestoreImpl(
    private val songRepo: SongRepo
) : RestoreRepo {
    override suspend fun restoreSongs(path: String): RestoreResult {
        return try {
            val json = Json {
                ignoreUnknownKeys = true
            }

            val file = File(path)

            val jsonDeserialized = withContext(Dispatchers.IO) {
                json.decodeFromString<ExportSchema>(file.readText())
            }

            withContext(Dispatchers.IO) {
                jsonDeserialized.songs
                    .map { it.toSong() }
                    .forEach {
                        songRepo.insertSong(it)
                    }
            }
            RestoreResult.Success
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            RestoreResult.Failure(RestoreFailedException.InvalidFile)
        } catch (e: SerializationException) {
            e.printStackTrace()
            RestoreResult.Failure(RestoreFailedException.OldSchema)
        }
    }
}