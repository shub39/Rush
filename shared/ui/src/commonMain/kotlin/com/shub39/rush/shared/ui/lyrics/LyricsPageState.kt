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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shub39.rush.shared.core.dataclasses.ExtractedColors
import com.shub39.rush.shared.core.dataclasses.LyricsPrefs
import com.shub39.rush.shared.core.dataclasses.SongUi
import com.shub39.rush.shared.core.enums.CardColors
import com.shub39.rush.shared.core.enums.LyricsAlignment
import com.shub39.rush.shared.core.enums.LyricsBackground
import com.shub39.rush.shared.core.enums.Sources
import com.shub39.rush.shared.core.interfaces.CorrectionSearchResult
import com.shub39.rush.shared.ui.rushmode.RushPhase
import com.shub39.rush.shared.ui.rushmode.RushState
import com.shub39.rush.shared.ui.session.SessionState
import org.jetbrains.compose.resources.StringResource

/**
 * Read only projection of the lyrics screen, assembled by `LyricsVM` from the flows it owns:
 * [SessionState] (the loaded song), [RushState] (rush mode) and [LyricsPrefs] (persisted
 * preferences).
 *
 * Nothing writes into this object, which is why the same values are never copied around between
 * view models any more.
 */
@Stable
@Immutable
data class LyricsPageState(
    val session: SessionState = SessionState(),
    val rush: RushState = RushState(),
    val prefs: LyricsPrefs = LyricsPrefs(),
    val lyricsState: LyricsState = LyricsState.Idle,
    val lrcCorrect: LrcCorrect = LrcCorrect(),
    val lyricsCorrect: Boolean = false,
) {
    val rushEnabled: Boolean
        get() = rush.enabled

    /** Rush mode progress, translated to the chip that the page already knows how to render. */
    val searchState: SearchState
        get() =
            when (val phase = rush.phase) {
                is RushPhase.Searching -> SearchState.Searching(phase.query)
                RushPhase.NotFound -> SearchState.UserPrompt
                else -> SearchState.Idle
            }

    val fetchingGenius: Pair<Boolean, LyricsState.LyricsError?>
        get() {
            val error = session.geniusError

            return session.geniusLoading to
                error?.let { LyricsState.LyricsError(errorCode = it.code, debugMessage = it.debug) }
        }

    val selectedLines: Map<Int, String>
        get() = session.selectedLines

    val source: Sources
        get() = session.source

    val sync: Boolean
        get() = session.sync

    val syncedAvailable: Boolean
        get() = session.syncedAvailable

    val extractedColors: ExtractedColors
        get() = session.extractedColors

    val expressiveSyllables: Boolean
        get() = prefs.expressiveSyllables

    val blurSyncedLyrics: Boolean
        get() = prefs.blurSyncedLyrics

    val textPrefs: TextPrefs
        get() =
            TextPrefs(
                fontSize = prefs.fontSize,
                lineHeight = prefs.lineHeight,
                letterSpacing = prefs.letterSpacing,
                lyricsAlignment = prefs.alignment,
            )

    val cardColors: CardColors
        get() = prefs.cardColors

    val lyricsBackground: LyricsBackground
        get() = prefs.lyricsBackground

    val maxLines: Int
        get() = prefs.maxLines

    val mCardBackground: Int
        get() = prefs.cardBackground

    val mCardContent: Int
        get() = prefs.cardContent

    val fullscreen: Boolean
        get() = prefs.fullscreen

    val romanizationEnabled: Boolean
        get() = prefs.romanizationEnabled

    val hideUI: Boolean
        get() = prefs.hideUI
}

sealed interface LyricsState {
    data object Idle : LyricsState

    data class Loaded(val song: SongUi) : LyricsState

    data class Fetching(val name: String) : LyricsState

    data class LyricsError(val errorCode: StringResource, val debugMessage: String? = null) :
        LyricsState
}

sealed interface SearchState {
    data object Idle : SearchState

    data class Searching(val query: String) : SearchState

    data object UserPrompt : SearchState
}

@Stable
@Immutable
data class TextPrefs(
    val fontSize: Float = 28f,
    val lineHeight: Float = 32f,
    val letterSpacing: Float = 0f,
    val lyricsAlignment: LyricsAlignment = LyricsAlignment.CENTER,
)

@Stable @Immutable data class PlaybackInfo(val position: Long = 0, val speed: Float = 0f)

@Stable
@Immutable
data class LrcCorrect(
    val searchResults: List<CorrectionSearchResult> = emptyList(),
    val searching: Boolean = false,
    val error: StringResource? = null,
)
