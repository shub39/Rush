/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.core.listener

import com.shub39.rush.shared.core.RushLogger
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual object MediaListener {

    private var pollingJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    actual val playbackSpeedFlow: MutableSharedFlow<Float> = MutableSharedFlow()
    actual val songInfoFlow: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow()
    actual val songPositionFlow: MutableSharedFlow<Long> = MutableSharedFlow()

    init {
        startListening(null)
    }

    actual fun startListening(context: Any?) {
        pollingJob?.cancel()
        pollingJob =
            coroutineScope.launch {
                while (isActive) {
                    updateMediaInfo()
                    delay(1000)
                }
            }
    }

    actual fun onSeekEagerly() {}

    actual fun seek(timeStamp: Long) {
        coroutineScope.launch {
            try {
                executeCommand("playerctl position ${(timeStamp/1000).toInt()}")
                songPositionFlow.emit(timeStamp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    actual fun pauseOrResume(resume: Boolean) {
        coroutineScope.launch {
            try {
                if (resume) {
                    executeCommand("playerctl play")
                } else {
                    executeCommand("playerctl pause")
                }
            } catch (e: Exception) {
                RushLogger.e("MediaListener", "Error toggle pause-resume", e)
                e.printStackTrace()
            }
        }
    }

    actual fun playNext() {
        coroutineScope.launch {
            try {
                executeCommand("playerctl next")
            } catch (e: Exception) {
                RushLogger.e("MediaListener", "Error playing next", e)
            }
        }
    }

    actual fun playPrevious() {
        coroutineScope.launch {
            try {
                executeCommand("playerctl previous")
            } catch (e: Exception) {
                RushLogger.e("MediaListener", "Error playing previous", e)
            }
        }
    }

    private suspend fun updateMediaInfo() {
        try {
            val status = executeCommand("playerctl status")
            val position = executeCommand("playerctl position")
            val metadataTitle = executeCommand("playerctl metadata xesam:title")
            val metadataArtist = executeCommand("playerctl metadata xesam:artist")

            // parse the output and emit updates
            playbackSpeedFlow.emit(
                when (status?.trim()) {
                    "Playing" -> 1f
                    else -> 0f
                }
            )

            val title = cleanOutput(metadataTitle)?.substringAfter("xesam:title ")?.trim() ?: ""
            val artist = cleanOutput(metadataArtist)?.substringAfter("xesam:artist ")?.trim() ?: ""
            songInfoFlow.emit(Pair(title, artist))

            val positionLong = position?.trim()?.toFloatOrNull()?.toLong() ?: 0L
            songPositionFlow.emit(positionLong * 1000)
        } catch (e: Exception) {
            RushLogger.e("MediaListener", "Error updating Media Info", e)
        }
    }

    private fun executeCommand(command: String): String? {
        return try {
            val process =
                ProcessBuilder(*command.split(" ").toTypedArray()).redirectErrorStream(true).start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.use { it.readText() }
            process.waitFor()
            output.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun cleanOutput(output: String?): String? {
        return output?.let {
            when (output) {
                "No players found" -> null
                "No player could handle this command" -> null
                else -> output
            }
        }
    }
}
