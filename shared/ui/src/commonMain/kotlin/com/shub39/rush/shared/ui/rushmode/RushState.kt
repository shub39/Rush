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
package com.shub39.rush.shared.ui.rushmode

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.shared.core.dataclasses.SongMeta
import org.jetbrains.compose.resources.StringResource

/**
 * State of the automatic "rush mode" pipeline: follow whatever the media session is playing,
 * resolve it to a song and load its lyrics.
 */
@Stable
@Immutable
data class RushState(
    val enabled: Boolean = false,
    val mediaAccess: Boolean = false,
    val nowPlaying: SongMeta? = null,
    val phase: RushPhase = RushPhase.Idle,
)

@Stable
@Immutable
sealed interface RushPhase {
    data object Idle : RushPhase

    data class Searching(val query: String) : RushPhase

    data class Fetching(val label: String) : RushPhase

    data class Loaded(val songId: Long) : RushPhase

    data object NotFound : RushPhase

    data class Error(val code: StringResource, val debug: String? = null) : RushPhase
}

/** One shot navigation requests emitted by the rush pipeline. */
sealed interface RushNavEvent {
    data object OpenLyrics : RushNavEvent
}
