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
import com.shub39.rush.database.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object MediaListener {

    private const val TAG = "MediaListener"

    private lateinit var msm: MediaSessionManager
    private lateinit var nls: ComponentName
    private var activeMediaController: MediaController? = null
    private val internalCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()

    private var initialised = false

    fun init(context: Context) {

        if (initialised) return

        initialised = true

        msm = context.getSystemService()!!
        nls = ComponentName(context, NotificationListener::class.java)

        val activeSessions = msm.getActiveSessions(nls)
        val activeSession = activeSessions.find { isActive(it.playbackState) }
        activeMediaController = activeSession ?: activeSessions.firstOrNull()
        onActiveSessionsChanged(context, activeSessions)

        Log.d("MediaListener", "init $msm")
    }

    fun destroy(context: Context) {

        if (!initialised) return
        internalCallbacks.forEach { (_, callback) ->
            activeMediaController?.unregisterCallback(callback)
        }
        internalCallbacks.clear()
        initialised = false
        updateTitle(context)

    }

    private fun onActiveSessionsChanged(context: Context, controllers: List<MediaController?>?) {
        val callbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()
        controllers?.filterNotNull()?.forEach {
            Log.d(TAG, "Session: $it (${it.sessionToken})")

            if (internalCallbacks.containsKey(it.sessionToken)) {
                callbacks[it.sessionToken] = internalCallbacks[it.sessionToken]!!
            } else {
                val callback = object : MediaController.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackState?) =
                        updateTitle(context, it.metadata)

                    override fun onMetadataChanged(metadata: MediaMetadata?) =
                        updateTitle(context, metadata)
                }

                it.registerCallback(callback)
                callbacks[it.sessionToken] = callback
            }
        }

        internalCallbacks.clear()
        internalCallbacks.putAll(callbacks)
    }

    private fun isActive(playbackState: PlaybackState?): Boolean {
        if (playbackState == null)
            return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return playbackState.isActive

        return when (playbackState.state) {
            PlaybackState.STATE_FAST_FORWARDING, PlaybackState.STATE_REWINDING, PlaybackState.STATE_SKIPPING_TO_PREVIOUS, PlaybackState.STATE_SKIPPING_TO_NEXT, PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM, PlaybackState.STATE_BUFFERING, PlaybackState.STATE_CONNECTING, PlaybackState.STATE_PLAYING -> true
            else -> false
        }
    }

    private fun updateTitle(context: Context, metadata: MediaMetadata?) {
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: ""
        val searchTerm = "${getMainTitle(title)} \n$artist"
        CoroutineScope(Dispatchers.IO).launch {
            SettingsDataStore.updateCurrentPlayingSong(context, searchTerm)
            Log.d(TAG, "searchTerm: $searchTerm")
        }
    }

    private fun updateTitle(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            SettingsDataStore.updateCurrentPlayingSong(context, "")
            SettingsDataStore.updateSongAutofill(context, false)
        }
    }

    private fun getMainTitle(songTitle: String): String {
        val regex = Regex("\\s*\\(.*?\\)\\s*$")
        return songTitle.replace(regex, "").trim()
    }

}