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

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import com.shub39.rush.shared.core.RushLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual object MediaListener {
    private var msm: MediaSessionManager? = null
    private var nls: ComponentName? = null
    private var activeMediaController: MediaController? = null
    private val internalCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()
    private var initialised = false
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var positionUpdateJob: Job? = null

    actual val playbackSpeedFlow: MutableSharedFlow<Float> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    actual val songInfoFlow: MutableSharedFlow<Pair<String, String>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    actual val songPositionFlow: MutableSharedFlow<Long> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    actual fun startListening(context: Any?) {
        if (context !is Context) return

        try {
            if (!initialised) {
                val manager = context.getSystemService(MediaSessionManager::class.java)
                val component = ComponentName(context, NotificationListener::class.java)

                manager?.let {
                    it.addOnActiveSessionsChangedListener({ controllers -> onActiveSessionsChanged(controllers) }, component)

                    val activeSessions = it.getActiveSessions(component)
                    val activeSession = activeSessions.find { session -> isActive(session.playbackState) }
                    activeMediaController = activeSession ?: activeSessions.firstOrNull()

                    msm = it
                    nls = component

                    onActiveSessionsChanged(activeSessions)
                    RushLogger.d("MediaListener", "init $it")
                    initialised = true
                } ?: RushLogger.e("MediaListener", "MediaSessionManager is null")
            }
        } catch (e: Exception) {
            RushLogger.e("MediaListener", "Exception $e")
        }
    }

    actual fun onSeekEagerly() {
        if (!initialised) return

        activeMediaController?.let { updateMetadata(it, it.metadata) }
    }

    actual fun seek(timeStamp: Long) {
        activeMediaController?.transportControls?.seekTo(timeStamp)
        activeMediaController?.transportControls?.play()
        coroutineScope.launch { songPositionFlow.emit(timeStamp) }
    }

    actual fun pauseOrResume(resume: Boolean) {
        if (resume) {
            activeMediaController?.transportControls?.play()
        } else {
            activeMediaController?.transportControls?.pause()
        }
    }

    actual fun playNext() {
        activeMediaController?.transportControls?.skipToNext()
    }

    actual fun playPrevious() {
        activeMediaController?.transportControls?.skipToPrevious()
    }

    private fun onActiveSessionsChanged(controllers: List<MediaController?>?) {
        val newCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()

        controllers?.filterNotNull()?.forEach { controller ->
            RushLogger.d("MediaListener", "Session: $controller (${controller.sessionToken})")

            // Workaround for spotify, dunno if this is the most elegant solution but works :)
            if (controller.packageName.contains("spotify")) {
                coroutineScope.launch {
                    delay(2000)
                    setActiveMediaSession(controller)
                }
            } else if (isActive(controller.playbackState)) {
                setActiveMediaSession(controller)
            }

            if (internalCallbacks.containsKey(controller.sessionToken)) {
                newCallbacks[controller.sessionToken] = internalCallbacks[controller.sessionToken]!!
            } else {
                val callback =
                    object : MediaController.Callback() {
                        override fun onPlaybackStateChanged(state: PlaybackState?) {
                            onPlaybackStateChanged(controller, state)
                        }

                        override fun onMetadataChanged(metadata: MediaMetadata?) {
                            updateMetadata(controller, metadata)
                        }
                    }

                controller.registerCallback(callback)
                newCallbacks[controller.sessionToken] = callback
            }
        }

        internalCallbacks.clear()
        internalCallbacks.putAll(newCallbacks)
    }

    private fun isActive(playbackState: PlaybackState?): Boolean {
        if (playbackState == null) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            playbackState.isActive
        } else {
            when (playbackState.state) {
                PlaybackState.STATE_FAST_FORWARDING,
                PlaybackState.STATE_REWINDING,
                PlaybackState.STATE_SKIPPING_TO_PREVIOUS,
                PlaybackState.STATE_SKIPPING_TO_NEXT,
                PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM,
                PlaybackState.STATE_BUFFERING,
                PlaybackState.STATE_CONNECTING,
                PlaybackState.STATE_PLAYING -> true

                else -> false
            }
        }
    }

    private fun onPlaybackStateChanged(controller: MediaController, state: PlaybackState?) {
        if (isActive(state)) {
            setActiveMediaSession(controller)
        } else {
            stopPositionUpdates()
            coroutineScope.launch {
                playbackSpeedFlow.emit(0f)
                state?.position?.let { songPositionFlow.emit(it) }
            }
        }
    }

    private fun setActiveMediaSession(newActive: MediaController) {
        activeMediaController = newActive
        updateMetadata(newActive, newActive.metadata)
        startPositionUpdates()
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob =
            coroutineScope.launch {
                while (isActive) {
                    activeMediaController?.let { controller ->
                        if (isActive(controller.playbackState)) {
                            val position = controller.playbackState?.position ?: 0L
                            val speed = controller.playbackState?.playbackSpeed ?: 1f
                            songPositionFlow.emit(position)
                            playbackSpeedFlow.emit(speed)
                        }
                    }
                    delay(1000)
                }
            }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private fun updateMetadata(controller: MediaController, metadata: MediaMetadata?) {
        if (controller.sessionToken != activeMediaController?.sessionToken) return

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist =
            metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?: ""

        coroutineScope.launch {
            if (controller.playbackState?.let { isActive(it) } == true) {
                songInfoFlow.emit(Pair(title, artist))
                playbackSpeedFlow.emit(controller.playbackState?.playbackSpeed ?: 1f)
                controller.playbackState?.position?.let { songPositionFlow.emit(it) }
            }
        }
    }
}
