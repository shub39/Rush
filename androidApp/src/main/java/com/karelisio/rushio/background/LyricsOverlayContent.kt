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

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karelisio.rushio.R
import kotlin.math.hypot

/** Texts shown by the overlay when there is no synced line, already translated. */
data class OverlayTexts(
    val waiting: String,
    val loading: String,
    val noLyrics: String,
    val notSynced: String,
)

/** Floating lyrics: current line, next line, close button and resize handle. */
@Composable
fun LyricsOverlayContent(
    lyrics: NowPlayingLyrics,
    texts: OverlayTexts,
    opacity: Float,
    textScale: Float,
    onDrag: (dx: Float, dy: Float) -> Unit,
    onDragEnd: () -> Unit,
    onTap: () -> Unit,
    onResize: (dx: Float, dy: Float) -> Unit,
    onResizeEnd: () -> Unit,
    onClose: () -> Unit,
) {
    OverlayTheme {
        val textShadow = Shadow(color = Color.Black.copy(alpha = 0.5f), blurRadius = 6f)

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = opacity),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Row(modifier = Modifier.padding(start = 16.dp, top = 6.dp, end = 4.dp, bottom = 6.dp)) {
                Column(
                    modifier =
                        Modifier.weight(1f)
                            .padding(vertical = 6.dp)
                            .rawDragGestures(
                                onDrag = onDrag,
                                onEnd = { moved -> if (moved) onDragEnd() else onTap() },
                            )
                            .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (lyrics.title.isNotBlank()) {
                        Text(
                            text =
                                listOf(lyrics.title, lyrics.artist)
                                    .filter { it.isNotBlank() }
                                    .joinToString(" · "),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(0.7f),
                        )
                    }

                    val (main, secondary) =
                        when (lyrics.status) {
                            NowPlayingLyrics.Status.IDLE -> texts.waiting to ""
                            NowPlayingLyrics.Status.LOADING -> texts.loading to ""
                            NowPlayingLyrics.Status.NO_LYRICS -> texts.noLyrics to ""
                            NowPlayingLyrics.Status.NOT_SYNCED -> texts.notSynced to ""
                            NowPlayingLyrics.Status.SYNCED ->
                                lyrics.currentLine.ifBlank { "♪" } to lyrics.nextLine
                        }

                    Text(
                        text = main,
                        style =
                            TextStyle(
                                fontSize = (18 * textScale).sp,
                                lineHeight = (22 * textScale).sp,
                                fontWeight = FontWeight.Bold,
                                shadow = textShadow,
                            ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().alpha(if (lyrics.isPlaying) 1f else 0.7f),
                    )

                    if (secondary.isNotBlank()) {
                        Text(
                            text = secondary,
                            style =
                                TextStyle(
                                    fontSize = (13 * textScale).sp,
                                    lineHeight = (16 * textScale).sp,
                                    shadow = textShadow,
                                ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(0.65f),
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_resize_handle),
                        contentDescription = null,
                        modifier =
                            Modifier.padding(4.dp)
                                .size(20.dp)
                                .alpha(0.6f)
                                .rawDragGestures(onDrag = onResize, onEnd = { onResizeEnd() }),
                    )
                }
            }
        }
    }
}

/** Material You colors, always dark: lyrics over any app read best on a dark card. */
@Composable
private fun OverlayTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val colorScheme = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(context)
        else darkColorScheme()
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

/**
 * Drag tracking with screen (raw) coordinates: the window moves under the finger, local coordinates
 * would shift with it. [onEnd] tells whether the pointer moved past the touch slop.
 */
@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
private fun Modifier.rawDragGestures(
    onDrag: (dx: Float, dy: Float) -> Unit,
    onEnd: (moved: Boolean) -> Unit,
): Modifier {
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val tracker = remember { DragTracker() }

    return pointerInteropFilter { event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                tracker.down(event.rawX, event.rawY)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!tracker.moved && tracker.distance(event.rawX, event.rawY) > touchSlop) {
                    tracker.moved = true
                }
                if (tracker.moved) {
                    onDrag(event.rawX - tracker.lastX, event.rawY - tracker.lastY)
                    tracker.lastX = event.rawX
                    tracker.lastY = event.rawY
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                onEnd(tracker.moved)
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                if (tracker.moved) onEnd(true)
                true
            }
            else -> false
        }
    }
}

private class DragTracker {
    var downX = 0f
    var downY = 0f
    var lastX = 0f
    var lastY = 0f
    var moved = false

    fun down(x: Float, y: Float) {
        downX = x
        downY = y
        lastX = x
        lastY = y
        moved = false
    }

    fun distance(x: Float, y: Float): Float = hypot(x - downX, y - downY)
}
