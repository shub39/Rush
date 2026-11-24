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
package com.shub39.rush.shared.ui.lyrics

import com.shub39.rush.shared.core.listener.MediaListener
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

fun interpolatedPlaybackPosition(tick: Duration = 100.milliseconds): Flow<Long> = channelFlow {
    var last = -1L
    combine(MediaListener.songPositionFlow, MediaListener.playbackSpeedFlow, ::Pair).collectLatest {
        (position, speed) ->
        val start = TimeSource.Monotonic.markNow()
        do {
            val estimate = position + (speed * start.elapsedNow().inWholeMilliseconds).toLong()
            val next =
                if (speed >= 0f && last in (estimate + 1)..(estimate + SEEK_THRESHOLD_MS)) last
                else estimate
            last = next
            send(next)
            delay(tick)
        } while (speed > 0f)
    }
}

private const val SEEK_THRESHOLD_MS = 1_500L
