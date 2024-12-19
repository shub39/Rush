package com.shub39.rush.lyrics.data.backup.restore

import android.content.Context
import android.net.Uri
import com.shub39.rush.lyrics.data.backup.export.ExportSchema
import com.shub39.rush.lyrics.data.mappers.toSong
import com.shub39.rush.lyrics.domain.backup.RestoreFailedException
import com.shub39.rush.lyrics.domain.backup.RestoreRepo
import com.shub39.rush.lyrics.domain.backup.RestoreResult
import com.shub39.rush.lyrics.domain.SongRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.io.path.outputStream
import kotlin.io.path.readText

class RestoreImpl(
    private val songRepo: SongRepo
): RestoreRepo {
    override suspend fun restoreSongs(uri: Uri, context: Context): RestoreResult {
        return try {
            val file = kotlin.io.path.createTempFile()

            context.contentResolver.openInputStream(uri).use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }

            val json = Json {
                ignoreUnknownKeys = true
            }

            val jsonDeserialized = json.decodeFromString<ExportSchema>(file.readText())

            withContext(Dispatchers.IO) {
                awaitAll(
                    async {
                        val songs = jsonDeserialized.songs.map { it.toSong() }

                        songs.forEach {
                            songRepo.insertSong(it)
                        }
                    }
                )
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