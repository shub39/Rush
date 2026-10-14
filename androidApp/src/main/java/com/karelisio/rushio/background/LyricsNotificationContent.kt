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

/** Texts of the lyrics notification, empty strings are left out. */
data class LyricsNotificationContent(val title: String, val text: String, val subText: String)

/**
 * The line being sung as title and the next one as text, like the floating window. Without synced
 * lyrics, the song as title and its status as text.
 */
fun lyricsNotificationContent(
    lyrics: NowPlayingLyrics,
    texts: OverlayTexts,
): LyricsNotificationContent {
    val song = listOf(lyrics.title, lyrics.artist).filter { it.isNotBlank() }.joinToString(" · ")

    fun songStatus(status: String) =
        if (song.isBlank()) LyricsNotificationContent(status, "", "")
        else LyricsNotificationContent(title = song, text = status, subText = "")

    return when (lyrics.status) {
        NowPlayingLyrics.Status.IDLE -> LyricsNotificationContent(texts.waiting, "", "")
        NowPlayingLyrics.Status.LOADING -> songStatus(texts.loading)
        NowPlayingLyrics.Status.NO_LYRICS -> songStatus(texts.noLyrics)
        NowPlayingLyrics.Status.NOT_SYNCED -> songStatus(texts.notSynced)
        NowPlayingLyrics.Status.SYNCED ->
            LyricsNotificationContent(
                title = lyrics.currentLine.ifBlank { "♪" },
                text = lyrics.nextLine,
                subText = song,
            )
    }
}
