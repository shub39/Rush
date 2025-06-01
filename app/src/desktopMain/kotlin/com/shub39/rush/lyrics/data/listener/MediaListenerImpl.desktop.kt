package com.shub39.rush.lyrics.data.listener

import com.fleeksoft.io.BufferedReader
import com.shub39.rush.lyrics.domain.MediaInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.InputStreamReader

actual class MediaListenerImpl: MediaInterface {

    private var pollingJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override val playbackSpeedFlow = MutableSharedFlow<Float>()
    override val songInfoFlow = MutableSharedFlow<Pair<String, String>>()
    override val songPositionFlow = MutableSharedFlow<Long>()

    init {
        pollingJob?.cancel()
        pollingJob = coroutineScope.launch {
            while (isActive) {
                updateMediaInfo()
            }
        }
    }

    override fun destroy() {
        pollingJob?.cancel()
    }

    override fun seek(timestamp: Long) {
        coroutineScope.launch {
            try {
                executeCommand("playerctl position ${(timestamp/1000).toInt()}")
                songPositionFlow.emit(timestamp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun pauseOrResume(resume: Boolean) {
        coroutineScope.launch {
            try {
                if (resume) {
                    executeCommand("playerctl play")
                } else {
                    executeCommand("playerctl pause")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun updateMediaInfo() {
        try {
            val status = executeCommand("playerctl status")
            val position = executeCommand("playerctl position")
            val metadataTitle = executeCommand("playerctl metadata xesam:title")
            val metadataArtist = executeCommand("playerctl metadata xesam:artist")

            //parse the output and emit updates
            playbackSpeedFlow.emit(
                when (status?.trim()) {
                    "Playing" -> 1f
                    else -> 0f
                }
            )

            val title = metadataTitle?.substringAfter("xesam:title ")?.trim() ?: ""
            val artist = metadataArtist?.substringAfter("xesam:artist ")?.trim() ?: ""
            songInfoFlow.emit(Pair(title, artist))

            val positionLong = position?.trim()?.toFloatOrNull()?.toLong() ?: 0L
            songPositionFlow.emit(positionLong * 1000)

        } catch (e: Exception) {
            e.printStackTrace()
            //handle potential errors
        }
    }

    private fun executeCommand(command: String): String? {
        return try {
            val process = ProcessBuilder(*command.split(" ").toTypedArray())
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.use { it.readText() }
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}