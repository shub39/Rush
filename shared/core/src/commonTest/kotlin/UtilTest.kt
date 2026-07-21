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
import com.shub39.rush.shared.core.getMainArtist
import com.shub39.rush.shared.core.getMainTitle
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {

    @Test
    fun testGetMainTitle() {
        assertEquals("Song Title", getMainTitle("Song Title"))
        assertEquals("Song Title", getMainTitle("Song Title (Official Video)"))
        assertEquals("Song Title", getMainTitle("Song Title [Remix]"))
        assertEquals("Song Title", getMainTitle("Song Title 【MV】"))
        assertEquals("Song", getMainTitle("Song (Ver) [Live]"))
        assertEquals("Song", getMainTitle("Song | something"))
        assertEquals("Song", getMainTitle("Song - Official Video"))
        assertEquals("Song", getMainTitle("Song feat. Someone"))
        assertEquals("Song", getMainTitle("Song ft. Someone"))
    }

    @Test
    fun testGetMainArtist() {
        assertEquals("Artist Name", getMainArtist("Artist Name"))
        assertEquals("Artist Name", getMainArtist("Artist Name (Producer)"))
        assertEquals("Artist A", getMainArtist("Artist A & Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A and Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A, Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A x Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A X Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A feat. Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A [Info] & Artist B"))
        assertEquals("Artist A", getMainArtist("Artist A (Info) & Artist B"))
    }
}
