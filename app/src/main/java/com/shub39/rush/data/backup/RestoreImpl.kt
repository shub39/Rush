package com.shub39.rush.data.backup

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.shub39.rush.data.mappers.toSong
import com.shub39.rush.domain.backup.ExportSchema
import com.shub39.rush.domain.backup.RestoreFailedException
import com.shub39.rush.domain.backup.RestoreRepo
import com.shub39.rush.domain.backup.RestoreResult
import com.shub39.rush.domain.interfaces.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream
import kotlin.io.path.readText

class RestoreImpl(
    private val songRepo: SongRepository,
    private val context: Context
) : RestoreRepo {

    companion object {
        private const val TAG = "RestoreImpl"
    }

    override suspend fun restoreSongs(path: String): RestoreResult = withContext(Dispatchers.IO) {
        return@withContext try {
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

            RestoreResult.Success
        } catch (e: IllegalArgumentException) {
            Log.wtf(TAG, e)

            RestoreResult.Failure(RestoreFailedException.InvalidFile)
        } catch (e: SerializationException) {
            Log.wtf(TAG, e)

            RestoreResult.Failure(RestoreFailedException.OldSchema)
        }
    }
}