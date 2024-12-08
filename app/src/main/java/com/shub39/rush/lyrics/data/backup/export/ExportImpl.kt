package com.shub39.rush.lyrics.data.backup.export

import android.os.Environment
import com.shub39.rush.lyrics.data.mappers.toSongSchema
import kotlinx.coroutines.async
import com.shub39.rush.lyrics.domain.ExportRepo
import com.shub39.rush.lyrics.domain.SongRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime

class ExportImpl(
    private val songRepo: SongRepo
): ExportRepo {
    override suspend fun exportToJson() = coroutineScope {
        val songsData = async {
            withContext(Dispatchers.IO) {
                songRepo.getAllSongs().map { it.toSongSchema() }
            }
        }
        val exportFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Rush"
        )

        if (!exportFolder.exists() || !exportFolder.isDirectory) exportFolder.mkdirs()

        val time = LocalDateTime.now().toString().replace(":", "").replace(" ", "")
        val file = File(exportFolder, "Rush-Export-$time.json")

        val songs = songsData.await()

        file.writeText(
            Json.encodeToString(
                ExportSchema(
                    schemaVersion = 3,
                    songs = songs
                )
            )
        )
    }
}