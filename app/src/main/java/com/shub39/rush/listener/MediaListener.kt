package com.shub39.rush.listener

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object MediaListener {

    private const val TAG = "MediaListener"

    private var msm: MediaSessionManager? = null
    private var nls: ComponentName? = null
    private var activeMediaController: MediaController? = null
    private val internalCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()
    val songInfoFlow = MutableSharedFlow<Pair<String, String>>()
    val songPositionFlow = MutableSharedFlow<Long>()

    private var initialised = false

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {

        if (!NotificationListener.canAccessNotifications(context)) return

        if (initialised) return

        initialised = true

        msm = context.getSystemService<MediaSessionManager>()
        nls = ComponentName(context, NotificationListener::class.java)

        msm?.let { manager ->
            val activeSessions = manager.getActiveSessions(nls!!)
            val activeSession = activeSessions.find { isActive(it.playbackState) }
            activeMediaController = activeSession ?: activeSessions.firstOrNull()
            onActiveSessionsChanged(activeSessions)
            Log.d(TAG, "init $manager")
        } ?: Log.e(TAG, "MediaSessionManager is null")
    }

    fun destroy() {

        if (!initialised) return
        internalCallbacks.forEach { (_, callback) ->
            activeMediaController?.unregisterCallback(callback)
        }
        internalCallbacks.clear()
        initialised = false

    }

    private fun onActiveSessionsChanged(controllers: List<MediaController?>?) {
        val callbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()
        controllers?.filterNotNull()?.forEach { controller ->
            Log.d(TAG, "Session: $controller (${controller.sessionToken})")

            if (internalCallbacks.containsKey(controller.sessionToken)) {
                callbacks[controller.sessionToken] = internalCallbacks[controller.sessionToken]!!
            } else {
                val callback = object : MediaController.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackState?) {
                        this@MediaListener.onPlaybackStateChanged(controller, state)
                        this@MediaListener.updateTitle(controller, controller.metadata)
                    }

                    override fun onMetadataChanged(metadata: MediaMetadata?) {
                        this@MediaListener.updateTitle(controller, metadata)
                    }
                }

                controller.registerCallback(callback)
                callbacks[controller.sessionToken] = callback
            }
        }

        internalCallbacks.clear()
        internalCallbacks.putAll(callbacks)
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
        if (isActive(state)) setActiveMediaSession(controller)
    }

    private fun setActiveMediaSession(newActive: MediaController) {
        activeMediaController = newActive
    }

    private fun updateTitle(controller: MediaController, metadata: MediaMetadata?) {
        if (controller.sessionToken != activeMediaController?.sessionToken) return
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: ""

        coroutineScope.launch {
            songInfoFlow.emit(Pair(getMainTitle(title), artist))
            controller.playbackState?.position?.let { songPositionFlow.emit(it) }
        }
    }

    private fun getMainTitle(songTitle: String): String {
        val regex = Regex("\\s*\\(.*?\\)\\s*$")
        return songTitle.replace(regex, "").trim()
    }

}