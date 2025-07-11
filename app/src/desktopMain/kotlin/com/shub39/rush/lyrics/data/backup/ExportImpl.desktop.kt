package com.shub39.rush.lyrics.data.backup

import com.shub39.rush.lyrics.data.mappers.toSongSchema
import com.shub39.rush.lyrics.domain.SongRepo
import com.shub39.rush.lyrics.domain.backup.ExportRepo
import com.shub39.rush.lyrics.domain.backup.ExportSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    override suspend fun exportToJson() = coroutineScope {
        val songsData = async {
            withContext(Dispatchers.IO) {
                songRepo.getAllSongs().map { it.toSongSchema() }
            }
        }.await()

        val exportFolder = File(
            System.getProperty("user.home") + File.separator + "Downloads",
            "Rush"
        )

        if (!exportFolder.exists() || !exportFolder.isDirectory) exportFolder.mkdirs()

        val time =
            Clock.System.now().toLocalDateTime(TimeZone.Companion.UTC).toString().replace(":", "")
                .replace(" ", "")
        val file = File(exportFolder, "Rush-Export-$time.json")

        withContext(Dispatchers.IO) {
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
}