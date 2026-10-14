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
import com.shub39.rush.shared.core.dataclasses.LyricsCandidate
import com.shub39.rush.shared.core.dataclasses.LyricsSourceSetting
import com.shub39.rush.shared.core.enums.LyricsProviderId
import com.shub39.rush.shared.core.enums.LyricsProviderId.LRCLIB
import com.shub39.rush.shared.core.enums.LyricsProviderId.MUSIXMATCH
import com.shub39.rush.shared.core.enums.LyricsProviderId.NETEASE
import com.shub39.rush.shared.core.enums.LyricsProviderId.QQ_MUSIC
import com.shub39.rush.shared.logic.datastore.LyricsSourcePreferencesImpl
import com.shub39.rush.shared.logic.network.lyrics.LyricsCascade
import com.shub39.rush.shared.logic.network.lyrics.MusixmatchProvider
import com.shub39.rush.shared.logic.network.lyrics.NeteaseProvider
import com.shub39.rush.shared.logic.network.lyrics.QQMusicProvider
import com.shub39.rush.shared.logic.network.lyrics.str
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

class LyricsProvidersTest {
    private fun candidate(
        provider: LyricsProviderId,
        plain: String? = null,
        lrc: String? = null,
        ttml: String? = null,
    ) = LyricsCandidate(provider, "Title", "Artist", plain = plain, lrc = lrc, ttml = ttml)

    /**
     * Runs [LyricsCascade.pickBest] with sources completing in [completionOrder], returns the best
     * candidate and the sources whose result was never consumed.
     */
    private fun pick(
        order: List<LyricsProviderId>,
        completionOrder: List<LyricsProviderId>,
        results: Map<LyricsProviderId, LyricsCandidate?>,
    ): Pair<LyricsCandidate?, List<LyricsProviderId>> = runBlocking {
        val channel = Channel<Pair<Int, LyricsCandidate?>>(Channel.UNLIMITED)
        completionOrder.forEach { channel.trySend(order.indexOf(it) to results[it]) }

        val best = LyricsCascade.pickBest(order, channel)
        val pending =
            generateSequence { channel.tryReceive().getOrNull() }.map { order[it.first] }.toList()
        best to pending
    }

    @Test
    fun cascadePrefersQualityThenOrder() {
        val results =
            mapOf(
                LRCLIB to candidate(LRCLIB, lrc = "[00:01.00]a"),
                NETEASE to null,
                MUSIXMATCH to candidate(MUSIXMATCH, ttml = "<tt/>"),
                QQ_MUSIC to candidate(QQ_MUSIC, lrc = "[00:01.00]b"),
            )

        val (best, pending) =
            pick(
                order = listOf(LRCLIB, NETEASE, MUSIXMATCH, QQ_MUSIC),
                completionOrder = listOf(MUSIXMATCH, LRCLIB, NETEASE, QQ_MUSIC),
                results = results,
            )

        assertEquals(MUSIXMATCH, best?.provider)
        // QQ Music can not beat syllable lyrics, its result is not waited for
        assertEquals(listOf(QQ_MUSIC), pending)
    }

    @Test
    fun cascadeTieGoesToFirstSource() {
        val results =
            mapOf(
                QQ_MUSIC to candidate(QQ_MUSIC, lrc = "[00:01.00]qq"),
                LRCLIB to candidate(LRCLIB, lrc = "[00:01.00]lrclib"),
                NETEASE to candidate(NETEASE, plain = "plain"),
            )

        val (best, pending) =
            pick(
                order = listOf(QQ_MUSIC, LRCLIB, NETEASE),
                completionOrder = listOf(LRCLIB, QQ_MUSIC, NETEASE),
                results = results,
            )

        assertEquals(QQ_MUSIC, best?.provider)
        assertEquals(emptyList(), pending)
    }

    @Test
    fun cascadeStopsAsSoonAsTheBestIsKnown() {
        val (best, pending) =
            pick(
                order = listOf(NETEASE, MUSIXMATCH, LRCLIB),
                completionOrder = listOf(NETEASE, MUSIXMATCH, LRCLIB),
                results = mapOf(NETEASE to candidate(NETEASE, ttml = "<tt/>")),
            )

        assertEquals(NETEASE, best?.provider)
        assertEquals(listOf(MUSIXMATCH, LRCLIB), pending)

        val (none, _) = pick(listOf(LRCLIB, QQ_MUSIC), listOf(QQ_MUSIC, LRCLIB), emptyMap())
        assertNull(none)
    }

    @Test
    fun sourcePreferencesDecoding() {
        assertEquals(
            LyricsSourcePreferencesImpl.defaultSources,
            LyricsSourcePreferencesImpl.decode(null),
        )

        val decoded = LyricsSourcePreferencesImpl.decode("QQ_MUSIC:0,UNKNOWN:1,LRCLIB:1,QQ_MUSIC:1")
        assertEquals(
            listOf(
                LyricsSourceSetting(QQ_MUSIC, false),
                LyricsSourceSetting(LRCLIB, true),
                LyricsSourceSetting(NETEASE, true),
                LyricsSourceSetting(MUSIXMATCH, true),
            ),
            decoded,
        )
        assertEquals(
            decoded,
            LyricsSourcePreferencesImpl.decode(LyricsSourcePreferencesImpl.encode(decoded)),
        )
    }

    @Test
    fun neteaseEapiSigning() {
        // Reference computed with md5sum + openssl enc -aes-128-ecb
        assertEquals(
            "04AE33D34A93FE3EC22DA8FA305D290AB337D0FE5F36D211DE0D338CC6AA89D096148678023FC35CD3DCB7B98CFE" +
                "188417E7BF35FCA95203FD9020D1DEFE11C5260DD89415244B44C6CC548635B65A579B24A230BB4C3052DC607" +
                "51B561DE5AA",
            NeteaseProvider.eapiParams("/api/song/lyric/v1", """{"id":1}"""),
        )
    }

    @Test
    fun qqMusicDecoding() {
        assertEquals("[00:01.00]Hello", QQMusicProvider.decodeLyric("WzAwOjAxLjAwXUhlbGxv"))
        assertEquals(
            "[ti:A]\n[00:01.00]It's",
            QQMusicProvider.decodeLyric("[ti&#58;A]&#10;[00&#58;01&#46;00]It&#39;s"),
        )
        assertEquals(
            "abc",
            QQMusicProvider.parseJson("""MusicJsonCallback({"lyric":"abc"})""").str("lyric"),
        )
        assertEquals("abc", QQMusicProvider.parseJson("""{"lyric":"abc"}""").str("lyric"))
    }

    @Test
    fun musixmatchPlainLyricsCleanup() {
        assertEquals(
            "Line one\nLine two",
            MusixmatchProvider.cleanPlainLyrics(
                "Line one\nLine two\n\n******* This Lyrics is NOT for Commercial use *******\n(1409623839273)"
            ),
        )
        assertNull(
            MusixmatchProvider.cleanPlainLyrics(
                "\n******* This Lyrics is NOT for Commercial use *******"
            )
        )
    }
}
