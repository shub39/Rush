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

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.shub39.rush.shared.core.dataclasses.OverlaySettings
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.StateFlow

/** The floating lyrics window, drawn over other apps with [WindowManager]. */
class LyricsOverlayWindow(
    private val context: Context,
    private val owner: SavedStateRegistryOwner,
    private val lyrics: StateFlow<NowPlayingLyrics>,
    private val texts: OverlayTexts,
    /** Persists position, width and text size once a gesture ends. */
    private val onLayoutChanged: (OverlaySettings) -> Unit,
    private val onClose: () -> Unit,
    private val onOpenApp: () -> Unit,
) {
    private val windowManager = context.getSystemService(WindowManager::class.java)
    private val density = context.resources.displayMetrics.density

    private var view: ComposeView? = null
    private var settings = OverlaySettings()

    // Compose state so the content follows gestures and settings changes
    private var opacity by mutableFloatStateOf(settings.opacity)
    private var textScale by mutableFloatStateOf(settings.textScale)

    @Suppress("DEPRECATION")
    private val params =
        WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT,
            )
            .apply { gravity = Gravity.TOP or Gravity.START }

    val isShown: Boolean
        get() = view != null

    fun show() {
        if (view != null) return

        val composeView =
            ComposeView(context).apply {
                setViewTreeLifecycleOwner(owner as LifecycleOwner)
                setViewTreeSavedStateRegistryOwner(owner)
                setContent {
                    val state by lyrics.collectAsStateWithLifecycle()
                    LyricsOverlayContent(
                        lyrics = state,
                        texts = texts,
                        opacity = opacity,
                        textScale = textScale,
                        onDrag = ::move,
                        onDragEnd = ::persist,
                        onTap = onOpenApp,
                        onResize = ::resize,
                        onResizeEnd = ::persist,
                        onClose = onClose,
                    )
                }
            }

        applySettingsToParams()
        windowManager.addView(composeView, params)
        view = composeView
    }

    fun hide() {
        view?.let { runCatching { windowManager.removeView(it) } }
        view = null
    }

    /** Applies stored settings, e.g. opacity changed from the app. */
    fun update(newSettings: OverlaySettings) {
        settings = newSettings
        opacity = newSettings.opacity
        textScale = newSettings.textScale
        applySettingsToParams()
        view?.let { windowManager.updateViewLayout(it, params) }
    }

    private fun applySettingsToParams() {
        val metrics = context.resources.displayMetrics
        params.width =
            (settings.widthDp * density)
                .roundToInt()
                .coerceIn(
                    (OverlaySettings.MIN_WIDTH_DP * density).roundToInt(),
                    metrics.widthPixels,
                )
        params.x = settings.x.coerceIn(0, (metrics.widthPixels - params.width).coerceAtLeast(0))
        params.y =
            settings.y.coerceIn(
                0,
                (metrics.heightPixels - (64 * density).roundToInt()).coerceAtLeast(0),
            )
    }

    private fun move(dx: Float, dy: Float) {
        val metrics = context.resources.displayMetrics
        params.x =
            (params.x + dx.roundToInt()).coerceIn(
                0,
                (metrics.widthPixels - params.width).coerceAtLeast(0),
            )
        params.y =
            (params.y + dy.roundToInt()).coerceIn(
                0,
                (metrics.heightPixels - (64 * density).roundToInt()).coerceAtLeast(0),
            )
        view?.let { windowManager.updateViewLayout(it, params) }
    }

    /** Horizontal drag changes the width, vertical drag the text size. */
    private fun resize(dx: Float, dy: Float) {
        val metrics = context.resources.displayMetrics
        params.width =
            (params.width + dx.roundToInt()).coerceIn(
                (OverlaySettings.MIN_WIDTH_DP * density).roundToInt(),
                metrics.widthPixels - params.x,
            )
        textScale =
            (textScale + dy / (200 * density)).coerceIn(
                OverlaySettings.MIN_TEXT_SCALE,
                OverlaySettings.MAX_TEXT_SCALE,
            )
        view?.let { windowManager.updateViewLayout(it, params) }
    }

    private fun persist() {
        settings =
            settings.copy(
                x = params.x,
                y = params.y,
                widthDp = (params.width / density).roundToInt(),
                textScale = textScale,
            )
        onLayoutChanged(settings)
    }
}
