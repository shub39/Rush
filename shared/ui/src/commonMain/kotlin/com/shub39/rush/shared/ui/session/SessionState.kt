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
package com.shub39.rush.shared.ui.session

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.Sources
import org.jetbrains.compose.resources.StringResource

/** Failure while loading or correcting a song, already resolved to a user facing message. */
@Stable @Immutable data class SessionError(val code: StringResource, val debug: String? = null)

/**
 * The song currently loaded into the app, independent of which screen is showing it.
 *
 * Owned exclusively by [SessionController]; every screen reads it and dispatches actions, nobody
 * copies values into it.
 */
@Stable
@Immutable
data class SessionState(
    val song: SongUi? = null,
    val isLoading: Boolean = false,
    val loadingLabel: String = "",
    val error: SessionError? = null,
    val geniusLoading: Boolean = false,
    val geniusError: SessionError? = null,
    val source: Sources = Sources.LRCLIB,
    val sync: Boolean = false,
    val syncedAvailable: Boolean = false,
    val selectedLines: Map<Int, String> = emptyMap(),
    val extractedColors: ExtractedColors = ExtractedColors(),
)
