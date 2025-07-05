package com.shub39.rush.lyrics.data.backup

import android.os.Environment
import com.shub39.rush.lyrics.data.mappers.toSongSchema
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.ExportSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

actual class ExportImpl(
    private val songRepo: SongRepo
): ExportRepo {
    @OptIn(ExperimentalTime::class)
    override suspend fun exportToJson() = withContext(Dispatchers.IO) {
        val songsData = songRepo.getAllSongs().map { it.toSongSchema() }
        val exportFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Rush"
        )

        if (!exportFolder.exists() || !exportFolder.isDirectory) exportFolder.mkdirs()

        val time =
            Clock.System.now().toLocalDateTime(TimeZone.Companion.UTC).toString().replace(":", "")
                .replace(" ", "")
        val file = File(exportFolder, "Rush-Export-$time.json")

        file.writeText(
            Json.Default.encodeToString(
                ExportSchema(
                    schemaVersion = 3,
                    songs = songsData
                )
            )
        )
    }
}