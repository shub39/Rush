package com.shub39.rush.shared.logic.backup

import android.content.Context
import androidx.core.net.toUri
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.backup.ExportSchema
import com.shub39.rush.shared.core.backup.RestoreFailedException
import com.shub39.rush.shared.core.backup.RestoreRepo
import com.shub39.rush.shared.core.backup.RestoreResult
import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.logic.mappers.toSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File

@Single(binds = [RestoreRepo::class])
actual class RestoreImpl(
    val songRepo: SongRepository,
    val context: Context
) : RestoreRepo {
   override suspend fun restoreSongs(path: String): RestoreResult =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val file = File(context.cacheDir, "temp.json")

                try {
                    context.contentResolver.openInputStream(path.toUri()).use { input ->
                        file.outputStream().use { output -> input?.copyTo(output) }
                    }

                    val json = Json { ignoreUnknownKeys = true }

                    val jsonDeserialized = json.decodeFromString<ExportSchema>(file.readText())

                    jsonDeserialized.songs.map { it.toSong() }.forEach { songRepo.insertSong(it) }
                } finally {
                    file.delete()
                }

                RestoreResult.Success
            } catch (e: IllegalArgumentException) {
                RushLogger.e("RestoreImpl", "Error restoring files", e)

                RestoreResult.Failure(RestoreFailedException.InvalidFile)
            } catch (e: SerializationException) {
                RushLogger.e("RestoreImpl", "Error restoring files", e)

                RestoreResult.Failure(RestoreFailedException.OldSchema)
            }
        }
}