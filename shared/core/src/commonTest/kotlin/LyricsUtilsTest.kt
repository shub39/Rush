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
import com.shub39.rush.shared.core.dataclasses.Lyric
import com.shub39.rush.shared.core.dataclasses.TrackQuery
import com.shub39.rush.shared.core.util.LrcUtils
import com.shub39.rush.shared.core.util.RichsyncParser
import com.shub39.rush.shared.core.util.TTMLParser
import com.shub39.rush.shared.core.util.TrackMatcher
import com.shub39.rush.shared.core.util.TtmlBuilder
import com.shub39.rush.shared.core.util.YrcParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LyricsUtilsTest {
    @Test
    fun lrcParseSkipsMetadataAndExpandsTimestamps() {
        val lrc =
            """
            [ti:Song]
            [ar:Artist]
            [offset:500]
            {"t":0,"c":[{"tx":"credits"}]}
            [00:01.00][00:10.50]Chorus
            [00:05.123]<00:05.12>Word <00:05.60>by word
            not a lyric line
            [00:08]Short tag
            """
                .trimIndent()

        assertEquals(
            listOf(
                Lyric(500, "Chorus"),
                Lyric(4623, "Word by word"),
                Lyric(7500, "Short tag"),
                Lyric(10000, "Chorus"),
            ),
            LrcUtils.parse(lrc),
        )
    }

    @Test
    fun lrcNormalizeAndPlain() {
        assertNull(LrcUtils.normalize("Just plain text\nwithout timestamps"))
        assertNull(LrcUtils.normalize("[ar:Artist]\n[00:01.00]"))

        val normalized =
            LrcUtils.normalize("[ar:Artist]\n[00:01.5]Hello\n[01:02.25]World\n[01:05.00]")
        assertEquals("[00:01.50]Hello\n[01:02.25]World\n[01:05.00]", normalized)
        assertEquals("Hello\nWorld", LrcUtils.toPlain(normalized!!))
    }

    @Test
    fun yrcToTtmlRoundTrip() {
        val yrc =
            """
            {"t":0,"c":[{"tx":"作词: "},{"tx":"Someone"}]}
            [1000,2000](1000,300,0)Hel(1300,200,0)lo (1500,500,0)world
            [4000,1000](4000,500,0)还(4500,500,0)没
            """
                .trimIndent()

        val lines = YrcParser.parse(yrc)
        assertEquals(2, lines.size)
        assertEquals("Hello world", lines[0].text)
        assertEquals("还没", lines[1].text)

        val parsed = TTMLParser.parseTTML(TtmlBuilder.build(lines)!!)
        val first = parsed.first { it.text.isNotEmpty() }
        assertEquals(listOf("Hello", "world"), first.words.map { it.text })
        assertEquals(1.0, first.words[0].startTime)
        assertEquals(1.5, first.words[0].endTime)
        assertEquals(1.5, first.words[1].startTime)
        assertEquals(2.0, first.words[1].endTime)

        assertEquals("[00:01.00]Hello world\n[00:04.00]还没", LrcUtils.fromTimedLines(lines))
    }

    @Test
    fun richsyncToTimedLines() {
        val body =
            """
            [{"ts":12.5,"te":15.0,"l":[{"c":"Rock","o":0},{"c":" ","o":0.4},{"c":"&","o":0.5},
            {"c":" ","o":0.7},{"c":"roll","o":0.8}],"x":"Rock & roll"}]
            """
                .trimIndent()

        val lines = RichsyncParser.parse(body)
        assertEquals(1, lines.size)
        assertEquals("Rock & roll", lines[0].text)
        assertEquals(12500, lines[0].words[0].startMs)
        assertEquals(12900, lines[0].words[0].endMs)
        assertEquals(15000, lines[0].words.last().endMs)

        val ttml = TtmlBuilder.build(lines)!!
        assertTrue("&amp;" in ttml)
        assertEquals(
            listOf("Rock", "&", "roll"),
            TTMLParser.parseTTML(ttml).last().words.map { it.text },
        )
        assertTrue(RichsyncParser.parse("not json").isEmpty())
    }

    @Test
    fun trackMatcherScores() {
        val query = TrackQuery(title = "Déjà Vu", artist = "Olivia Rodrigo", durationMs = 215_000)

        assertTrue(TrackMatcher.score(query, "Deja Vu", "Olivia Rodrigo", 214_000) > 0.9)
        assertTrue(
            TrackMatcher.score(query, "deja vu (Live)", "Olivia Rodrigo, Someone", null) >=
                TrackMatcher.MIN_SCORE
        )
        assertEquals(0.0, TrackMatcher.score(query, "Deja Vu", "Beyoncé", null))
        assertTrue(TrackMatcher.score(query, "Deja Vu", "Olivia Rodrigo", 400_000) < 0.75)

        val best =
            TrackMatcher.best(
                query,
                listOf("Other Song" to "Olivia Rodrigo", "Deja Vu" to "Olivia Rodrigo"),
                { it.first },
                { it.second },
            )
        assertEquals("Deja Vu", best?.first)
    }
}
