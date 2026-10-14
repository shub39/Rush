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

import org.junit.Assert.assertEquals
import org.junit.Test

class LyricsNotificationContentTest {
    private val texts =
        OverlayTexts(
            waiting = "Waiting",
            loading = "Loading",
            noLyrics = "No lyrics",
            notSynced = "Not synced",
        )

    private val song =
        NowPlayingLyrics(
            status = NowPlayingLyrics.Status.SYNCED,
            title = "Song",
            artist = "Artist",
            currentLine = "Current line",
            nextLine = "Next line",
        )

    @Test
    fun syncedLyricsShowTheCurrentLineAsTitleAndTheNextOneAsText() {
        assertEquals(
            LyricsNotificationContent("Current line", "Next line", "Song · Artist"),
            lyricsNotificationContent(song, texts),
        )
    }

    @Test
    fun instrumentalBreaksShowAMusicNote() {
        assertEquals(
            LyricsNotificationContent("♪", "Next line", "Song · Artist"),
            lyricsNotificationContent(song.copy(currentLine = ""), texts),
        )
    }

    @Test
    fun withoutSyncedLyricsTheSongAndItsStatusAreShown() {
        assertEquals(
            LyricsNotificationContent("Song · Artist", "Loading", ""),
            lyricsNotificationContent(song.copy(status = NowPlayingLyrics.Status.LOADING), texts),
        )
        assertEquals(
            LyricsNotificationContent("Song", "Not synced", ""),
            lyricsNotificationContent(
                song.copy(status = NowPlayingLyrics.Status.NOT_SYNCED, artist = ""),
                texts,
            ),
        )
    }

    @Test
    fun nothingPlayingShowsTheWaitingText() {
        assertEquals(
            LyricsNotificationContent("Waiting", "", ""),
            lyricsNotificationContent(NowPlayingLyrics(), texts),
        )
    }
}
