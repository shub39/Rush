/*
 * Copyright (C) 2026  Karelisio
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
package com.karelisio.rushio.background

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.karelisio.rushio.MainActivity
import com.karelisio.rushio.R
import com.shub39.rush.shared.core.RushLogger
import com.shub39.rush.shared.core.enums.BackgroundLyricsMode
import com.shub39.rush.shared.core.interfaces.BackgroundLyricsPreferences
import com.shub39.rush.shared.core.listener.MediaListener
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * Foreground service following the song played by any app and showing its lyrics in a floating
 * window over other apps, or in its notification.
 */
class BackgroundLyricsService : Service(), SavedStateRegistryOwner, KoinComponent {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateController.savedStateRegistry

    private val preferences: BackgroundLyricsPreferences by inject()
    private val engine by lazy { NowPlayingLyricsEngine(get(), get(), get()) }
    private var overlay: LyricsOverlayWindow? = null
    private val mode = MutableStateFlow(BackgroundLyricsMode.OFF)
    private val texts by lazy {
        OverlayTexts(
            waiting = getString(R.string.overlay_waiting),
            loading = getString(R.string.overlay_loading),
            noLyrics = getString(R.string.overlay_no_lyrics),
            notSynced = getString(R.string.overlay_not_synced),
        )
    }

    override fun onCreate() {
        super.onCreate()
        savedStateController.performAttach()
        savedStateController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        BackgroundLyricsNotification.createChannels(this)

        MediaListener.startListening(applicationContext)
        engine.start(lifecycleScope)
        _running.value = true

        lifecycleScope.launch {
            combine(preferences.getModeFlow(), preferences.getOverlaySettingsFlow(), ::Pair)
                .collect { (newMode, settings) ->
                    mode.value = newMode
                    when (newMode) {
                        BackgroundLyricsMode.OFF -> stopSelf()
                        BackgroundLyricsMode.OVERLAY -> {
                            if (Settings.canDrawOverlays(this@BackgroundLyricsService)) {
                                overlayWindow().apply {
                                    update(settings)
                                    show()
                                }
                            } else {
                                // The permission was revoked, nothing can be shown anymore
                                preferences.setMode(BackgroundLyricsMode.OFF)
                            }
                        }
                        BackgroundLyricsMode.NOTIFICATION -> overlay?.hide()
                    }
                }
        }

        // Keep the notification up to date: every line in notification mode, the song otherwise
        lifecycleScope.launch {
            combine(mode, engine.state) { mode, lyrics ->
                    mode to
                        if (mode == BackgroundLyricsMode.NOTIFICATION)
                            lyrics.copy(isPlaying = false)
                        else lyrics.copy(currentLine = "", nextLine = "", isPlaying = false)
                }
                .distinctUntilChanged()
                .conflate()
                .collect { (mode, lyrics) ->
                    if (mode != BackgroundLyricsMode.OFF) notify(notification(mode, lyrics))
                    // Android drops the updates of an app posting more than ~5 per second
                    delay(MIN_UPDATE_INTERVAL)
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent
            ?.getStringExtra(EXTRA_MODE)
            ?.let { name -> BackgroundLyricsMode.entries.firstOrNull { it.name == name } }
            ?.let { mode.value = it }
        // Every startForegroundService call must be followed by startForeground
        startInForeground(notification(mode.value, engine.state.value))
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        if (intent?.action == ACTION_STOP) {
            lifecycleScope.launch { preferences.setMode(BackgroundLyricsMode.OFF) }
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        overlay?.hide()
        overlay = null
        _running.value = false
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun overlayWindow(): LyricsOverlayWindow =
        overlay
            ?: LyricsOverlayWindow(
                    context = this,
                    owner = this,
                    lyrics = engine.state,
                    texts = texts,
                    onLayoutChanged = { settings ->
                        lifecycleScope.launch {
                            preferences.updateOverlaySettings {
                                it.copy(
                                    x = settings.x,
                                    y = settings.y,
                                    widthDp = settings.widthDp,
                                    textScale = settings.textScale,
                                )
                            }
                        }
                    },
                    onClose = {
                        lifecycleScope.launch { preferences.setMode(BackgroundLyricsMode.OFF) }
                    },
                    onOpenApp = {
                        // Allowed from the background: the floating window is visible
                        val intent =
                            Intent(this, MainActivity::class.java)
                                .addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                )
                        runCatching { startActivity(intent) }
                            .onFailure { RushLogger.e(TAG, "Could not open the app", it) }
                    },
                )
                .also { overlay = it }

    private fun notification(mode: BackgroundLyricsMode, lyrics: NowPlayingLyrics): Notification =
        if (mode == BackgroundLyricsMode.NOTIFICATION) {
            BackgroundLyricsNotification.lyrics(this, lyrics, texts)
        } else BackgroundLyricsNotification.overlay(this, lyrics)

    private fun startInForeground(notification: Notification) {
        try {
            ServiceCompat.startForeground(
                this,
                BackgroundLyricsNotification.ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else 0,
            )
        } catch (e: Exception) {
            RushLogger.e(TAG, "Could not start in foreground", e)
            stopSelf()
        }
    }

    private fun notify(notification: Notification) {
        try {
            NotificationManagerCompat.from(this)
                .notify(BackgroundLyricsNotification.ID, notification)
        } catch (_: SecurityException) {
            // Notifications not allowed, the service keeps running without visible updates
        }
    }

    companion object {
        private const val TAG = "BackgroundLyricsService"
        const val ACTION_STOP = "com.karelisio.rushio.action.STOP_BACKGROUND_LYRICS"

        /** Name of the [BackgroundLyricsMode] to start in, to show the right notification. */
        const val EXTRA_MODE = "com.karelisio.rushio.extra.BACKGROUND_LYRICS_MODE"
        private val MIN_UPDATE_INTERVAL = 250.milliseconds

        private val _running = MutableStateFlow(false)
        val running: StateFlow<Boolean> = _running.asStateFlow()
    }
}
