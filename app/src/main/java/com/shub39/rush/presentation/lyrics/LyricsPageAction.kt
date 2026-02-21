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
package com.shub39.rush.presentation.lyrics

import com.shub39.rush.domain.dataclasses.SongDetails
import com.shub39.rush.domain.enums.CardColors
import com.shub39.rush.domain.enums.LyricsAlignment
import com.shub39.rush.domain.enums.LyricsBackground
import com.shub39.rush.domain.enums.Sources

sealed interface LyricsPageAction {
    data class OnBlurSyncedChange(val pref: Boolean) : LyricsPageAction

    data class OnChangeLyricsBackground(val background: LyricsBackground) : LyricsPageAction

    data class OnMaxLinesChange(val lines: Int) : LyricsPageAction

    data class OnFullscreenChange(val pref: Boolean) : LyricsPageAction

    data object OnCustomisationReset : LyricsPageAction

    data class OnLetterSpacingChange(val spacing: Float) : LyricsPageAction

    data class OnLineHeightChange(val height: Float) : LyricsPageAction

    data class OnFontSizeChange(val size: Float) : LyricsPageAction

    data class OnAlignmentChange(val alignment: LyricsAlignment) : LyricsPageAction

    data class OnChangeSelectedLines(val lines: Map<Int, String>) : LyricsPageAction

    data class OnScrapeGeniusLyrics(val id: Long, val url: String) : LyricsPageAction

    data class OnLyricsCorrect(val show: Boolean) : LyricsPageAction

    data class OnSyncAvailable(val sync: Boolean) : LyricsPageAction

    data class OnToggleColorPref(val pref: Boolean) : LyricsPageAction

    data class OnUpdatemBackground(val color: Int) : LyricsPageAction

    data class OnUpdatemContent(val color: Int) : LyricsPageAction

    data class OnUpdateColorType(val color: CardColors) : LyricsPageAction

    data class OnSync(val sync: Boolean) : LyricsPageAction

    data class OnSourceChange(val source: Sources) : LyricsPageAction

    data object OnToggleAutoChange : LyricsPageAction

    data object OnPauseOrResume : LyricsPageAction

    data object OnPlayNext : LyricsPageAction

    data object OnPlayPrevious : LyricsPageAction

    data class OnSeek(val position: Long) : LyricsPageAction

    data class OnUpdateShareLines(val songDetails: SongDetails) : LyricsPageAction

    data object OnToggleSearchSheet : LyricsPageAction

    data class UpdateExtractedColors(val url: String) : LyricsPageAction

    data class OnLrcSearch(val track: String, val artist: String) : LyricsPageAction

    data class OnUpdateSongLyrics(
        val id: Long,
        val plainLyrics: String,
        val syncedLyrics: String?,
    ) : LyricsPageAction
}
