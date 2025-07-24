package com.shub39.rush.core.data.backup

import android.content.Context
import androidx.core.net.toUri
import com.shub39.rush.core.data.mappers.toSong
import com.shub39.rush.core.domain.SongRepo
import com.shub39.rush.core.domain.backup.ExportSchema
import com.shub39.rush.core.domain.backup.RestoreFailedException
import com.shub39.rush.core.domain.backup.RestoreRepo
import com.shub39.rush.core.domain.backup.RestoreResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream
import kotlin.io.path.readText

class RestoreImpl(
    private val songRepo: SongRepo,
    private val context: Context
) : RestoreRepo {
    override suspend fun restoreSongs(path: String): RestoreResult {
        return try {
            withContext(Dispatchers.IO) {
                val file = createTempFile()

                try {
                    context.contentResolver.openInputStream(path.toUri()).use { input ->
                        file.outputStream().use { output ->
                            input?.copyTo(output)
                        }
                    }

                    val json = Json {
                        ignoreUnknownKeys = true
                    }

                    val jsonDeserialized = json.decodeFromString<ExportSchema>(file.readText())

                    jsonDeserialized.songs
                        .map { it.toSong() }
                        .forEach { songRepo.insertSong(it) }
                } finally {
                    file.deleteIfExists()
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