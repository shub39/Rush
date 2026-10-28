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
import com.shub39.rush.shared.core.dataclasses.SearchResult
import com.shub39.rush.shared.core.dataclasses.SongMeta
import com.shub39.rush.shared.core.matching.SongMatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SongMatcherTest {
    private fun result(title: String, artist: String, album: String? = null, id: Long = 1L) =
        SearchResult(id = id, title = title, artist = artist, album = album, artUrl = "", url = "")

    @Test
    fun `exact title and artist match`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "Blinding Lights", artist = "The Weeknd"),
                results = listOf(result("Blinding Lights", "The Weeknd")),
            )

        assertNotNull(match)
    }

    @Test
    fun `featured credit in the title is ignored`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "Stay (feat. Justin Bieber)", artist = "The Kid LAROI"),
                results = listOf(result("Stay", "The Kid LAROI, Justin Bieber")),
            )

        assertNotNull(match)
        assertEquals("Stay", match.title)
    }

    @Test
    fun `remaster suffix is ignored`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "Bohemian Rhapsody - Remastered 2011", artist = "Queen"),
                results = listOf(result("Bohemian Rhapsody", "Queen")),
            )

        assertNotNull(match)
    }

    @Test
    fun `same title by a different artist is rejected`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "Hello", artist = "Adele"),
                results = listOf(result("Hello", "Lionel Richie")),
            )

        assertNull(match)
    }

    @Test
    fun `title alone is enough when the player reports no artist`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "Blinding Lights", artist = null),
                results = listOf(result("Blinding Lights", "The Weeknd")),
            )

        assertNotNull(match)
    }

    @Test
    fun `unrelated candidates are rejected`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "Completely Different Thing", artist = "Someone"),
                results = listOf(result("Unrelated Song", "Someone")),
            )

        assertNull(match)
    }

    @Test
    fun `blank playing title never matches`() {
        val match =
            SongMatcher.best(
                meta = SongMeta(title = "   ", artist = "Someone"),
                results = listOf(result("Unrelated Song", "Someone")),
            )

        assertNull(match)
    }

    @Test
    fun `empty candidate list returns null`() {
        assertNull(SongMatcher.best(SongMeta(title = "Anything"), emptyList()))
    }

    @Test
    fun `album agreement breaks a tie between similar candidates`() {
        val meta = SongMeta(title = "Intro", artist = "Band", album = "Second Album")
        val candidates =
            listOf(
                result("Intro", "Band", album = "First Album", id = 1L),
                result("Intro", "Band", album = "Second Album", id = 2L),
            )

        assertEquals(2L, SongMatcher.best(meta, candidates)?.id)
    }

    @Test
    fun `normalize strips noise and case folds`() {
        assertEquals("song", SongMatcher.normalize("SONG (feat. Nobody) [Live] - Remastered 2011"))
    }

    @Test
    fun `with or without you`() {
        val test = "with or without you"
        assertEquals(test, SongMatcher.normalize(test))
    }
}
