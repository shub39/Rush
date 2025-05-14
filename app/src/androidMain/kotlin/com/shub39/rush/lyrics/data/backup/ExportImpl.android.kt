package com.shub39.rush.lyrics.data.backup

import android.os.Environment
import com.shub39.rush.lyrics.data.mappers.toSongSchema
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.ExportSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import java.io.File

actual class ExportImpl(
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

        val time =
            Clock.System.now().toLocalDateTime(TimeZone.Companion.UTC).toString().replace(":", "")
                .replace(" ", "")
        val file = File(exportFolder, "Rush-Export-$time.json")

        val songs = songsData.await()

        file.writeText(
            Json.Default.encodeToString(
                ExportSchema(
                    schemaVersion = 3,
                    songs = songs
                )
            )
        )
    }
}