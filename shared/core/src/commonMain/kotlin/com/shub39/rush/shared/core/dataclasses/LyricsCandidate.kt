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
package com.shub39.rush.shared.core.dataclasses

import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.enums.LyricsQuality

/**
 * Lyrics found by a [LyricsProviderId] for a track.
 *
 * @property title Title of the matched track on the provider.
 * @property artist Artist of the matched track on the provider.
 * @property plain Unsynced lyrics.
 * @property lrc Line synced lyrics, normalized LRC (`[mm:ss.xx]text` lines only).
 * @property ttml Syllable synced lyrics as TTML.
 */
data class LyricsCandidate(
    val provider: LyricsProviderId,
    val title: String,
    val artist: String,
    val album: String? = null,
    val durationMs: Long? = null,
    val plain: String? = null,
    val lrc: String? = null,
    val ttml: String? = null,
) {
    val quality: LyricsQuality
        get() =
            when {
                !ttml.isNullOrBlank() -> LyricsQuality.SYLLABLE
                !lrc.isNullOrBlank() -> LyricsQuality.LINE
                !plain.isNullOrBlank() -> LyricsQuality.PLAIN
                else -> LyricsQuality.NONE
            }
}

/** What we know about the track we are looking lyrics for. */
data class TrackQuery(
    val title: String,
    val artist: String,
    val album: String? = null,
    val durationMs: Long? = null,
)

/** A lyrics source in the user defined cascade order. */
data class LyricsSourceSetting(val provider: LyricsProviderId, val enabled: Boolean)
