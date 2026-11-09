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
package com.shub39.rush.island

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameerasw.island.sdk.HostInfo
import com.sameerasw.island.sdk.IslandAnchor
import com.sameerasw.island.sdk.IslandAttachmentPayload
import com.sameerasw.island.sdk.IslandHostHandle
import com.sameerasw.island.sdk.IslandPayload
import com.sameerasw.island.sdk.IslandSurfaceKey
import com.sameerasw.island.sdk.compose.ComposeIslandProviderService
import com.sameerasw.island.sdk.compose.islandSnapshot
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.listener.MediaListener
import com.shub39.rush.shared.ui.lyrics.interpolatedPlaybackPosition
import com.shub39.rush.shared.ui.rushmode.RushController
import com.shub39.rush.shared.ui.session.SessionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Island provider for the Essentials island: shows the current synced lyric line in a shape below the
 * island's expanded media player.
 *
 * Lyrics come from the same pipeline as rush mode ([RushController] resolves the playing song into
 * [SessionController]), so rush mode has to be on. The attachment carries the song title, and Essentials
 * only shows it under a track with a matching title.
 */
class RushIslandService : ComposeIslandProviderService(), KoinComponent {
    private val session: SessionController by inject()
    private val rush: RushController by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var publishJob: Job? = null

    /** Synced lines of the loaded song, empty when it has none. */
    private val lines: StateFlow<List<TimedLine>> by lazy {
        session.state
            .map { it.song?.timedLines().orEmpty() }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, emptyList())
    }

    private val position: StateFlow<Long> by lazy {
        interpolatedPlaybackPosition().stateIn(scope, SharingStarted.WhileSubscribed(5_000), 0L)
    }

    override fun onAttached(host: IslandHostHandle, info: HostInfo) {
        // Same start-up the app does from MainActivity, so lyrics resolve without opening Rush
        MediaListener.startListening(applicationContext)
        rush.start()

        // Only the publishing job is per host; lines and position keep running for the surface
        publishJob?.cancel()
        publishJob = scope.launch {
            session.state
                .map { state -> state.song?.takeIf { it.timedLines().isNotEmpty() }?.title }
                .distinctUntilChanged()
                .collect { title -> publish(host, info, title) }
        }
    }

    override fun onHostDetached() {
        publishJob?.cancel()
        publishJob = null
        super.onHostDetached()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun publish(host: IslandHostHandle, info: HostInfo, title: String?) {
        // Older Essentials builds have no attachments; publishing nothing keeps them unaffected
        if (!info.supportsAttachments) return
        val attachments =
            title?.let {
                listOf(
                    IslandAttachmentPayload(
                        key = ATTACHMENT_KEY,
                        anchor = IslandAnchor.MEDIA,
                        heightDp = 72,
                        matchTitle = it,
                    )
                )
            }
        try {
            host.publish(IslandPayload(attachments = attachments.orEmpty()))
        } catch (_: Exception) {
            // Host went away between attach and publish; the next attach publishes again
        }
    }

    override fun hasExpandedContent(itemKey: String) =
        IslandSurfaceKey.isAttachment(itemKey) &&
            IslandSurfaceKey.attachmentKey(itemKey) == ATTACHMENT_KEY

    @Composable
    override fun ExpandedContent(itemKey: String) {
        val density = LocalDensity.current
        val timed by lines.collectAsState()
        val now by position.collectAsState()
        val index = timed.indexOfLast { it.timeMs <= now }
        val current = timed.getOrNull(index)?.text
        val next = timed.getOrNull(index + 1)?.text

        var heightPx by remember { mutableIntStateOf(0) }
        // Essentials clips attachments to min(28dp, height / 2); match it so the edges line up
        val corner = with(density) { if (heightPx > 0) minOf(28.dp, (heightPx / 2f).toDp()) else 28.dp }
        val shape = RoundedCornerShape(corner)

        Box(
            Modifier.fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .onSizeChanged {
                    heightPx = it.height
                    host?.resize(itemKey, it.height)
                }
                .islandSnapshot { host?.snapshot(itemKey, it) }
                .clip(shape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    val forward = targetState >= initialState
                    (slideInVertically { if (forward) it / 2 else -it / 2 } + fadeIn()) togetherWith
                        (slideOutVertically { if (forward) -it / 2 else it / 2 } + fadeOut())
                },
                label = "islandLyric",
            ) { shown ->
                // Read by index so the outgoing line keeps its own text while it animates out
                val line = timed.getOrNull(shown)?.text ?: current
                val upcoming = timed.getOrNull(shown + 1)?.text ?: next
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = line?.ifBlank { "♪" } ?: "♪",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = LyricShadow,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (!upcoming.isNullOrBlank()) {
                        Text(
                            text = upcoming,
                            color = Color.White.copy(alpha = 0.45f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = LyricShadow,
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        )
                    }
                }
            }
        }
    }

    private data class TimedLine(val timeMs: Long, val text: String)

    /** TTML when present (word synced source, background vocals skipped), otherwise LRC lines. */
    private fun SongUi.timedLines(): List<TimedLine> =
        ttmlLyrics
            ?.filterNot { it.isBackground }
            ?.map { TimedLine((it.startTime * 1000).toLong(), it.text) }
            ?.takeIf { it.isNotEmpty() }
            ?: syncedLyrics?.map { TimedLine(it.time, it.text) }.orEmpty()

    private companion object {
        const val ATTACHMENT_KEY = "lyrics"

        // Soft shadow so the lines stay readable over the host's light pools
        val LyricShadow = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(0f, 1f), blurRadius = 8f))
    }
}
