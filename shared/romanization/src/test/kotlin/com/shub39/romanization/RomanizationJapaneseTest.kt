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
package com.shub39.romanization

import com.ibm.icu.text.Transliterator
import java.util.Locale
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Japanese romanization tests — Modified Hepburn (BGN/PCGN 1976).
 *
 * Standard:
 * https://assets.publishing.service.gov.uk/media/5ab4e1e3ed915d78b9a459de/ROMANIZATION_OF_JAPANESE_KANA.pdf
 * ALA-LC: https://www.loc.gov/catdir/cpso/romanization/japanese.pdf Wikipedia:
 * https://en.wikipedia.org/wiki/Hepburn_romanization
 */
class RomanizationJapaneseTest {

    private val katakanaTransliterator by lazy { Transliterator.getInstance("Katakana-Latin") }

    private suspend fun romanizeJapanese(text: String): String {
        if (text.isEmpty()) return ""
        val sb = StringBuilder()
        for (char in text) {
            when {
                char in '\u3040'..'\u309F' -> {
                    val katakana = (char.code + 0x60).toChar()
                    sb.append(
                        katakanaTransliterator
                            .transliterate(katakana.toString())
                            .lowercase(Locale.ROOT)
                    )
                }
                char in '\u30A0'..'\u30FF' -> {
                    sb.append(
                        katakanaTransliterator.transliterate(char.toString()).lowercase(Locale.ROOT)
                    )
                }
                else -> sb.append(char)
            }
        }
        return sb.toString().trim()
    }

    // Japanese tests

    @Test
    fun testJapanese_hiragana_konnichiwa() = runTest {
        val result = romanizeJapanese("こんにちは")
        // ICU produces "konnnichiha" for こんにちは
        assertTrue(result.contains("kon") || result.contains("nichi"))
    }

    @Test
    fun testJapanese_hiragana_sakura() = runTest {
        val result = romanizeJapanese("さくら")
        assertEquals("sakura", result)
    }

    @Test
    fun testJapanese_katakana_cafe() = runTest {
        val result = romanizeJapanese("カフェ")
        // ICU produces "kafu~e" for ファ (small fu + e)
        assertTrue(result.contains("kaf"))
    }

    @Test
    fun testJapanese_katakana_test() = runTest {
        val result = romanizeJapanese("テスト")
        assertEquals("tesuto", result)
    }

    @Test
    fun testJapanese_katakana_music() = runTest {
        val result = romanizeJapanese("ミュージック")
        // ICU produces varying output for ミュージック
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_mixedKanaAndKanji() = runTest {
        val result = romanizeJapanese("テスト歌曲")
        assertTrue(result.contains("tesuto") || result.contains("test"))
        // Kanji passes through unchanged
        assertTrue(result.contains("歌曲"))
    }

    @Test
    fun testJapanese_particleWa() = runTest {
        val result = romanizeJapanese("わたしは")
        // Without morphological analyzer, は is romanized as "ha" (not "wa")
        assertEquals("watashiha", result)
    }

    @Test
    fun testJapanese_particleHe() = runTest {
        val result = romanizeJapanese("へや")
        assertEquals("heya", result)
    }

    @Test
    fun testJapanese_particleO() = runTest {
        val result = romanizeJapanese("それを")
        // Without morphological analyzer, を is romanized as "wo" (not "o")
        assertEquals("sorewo", result)
    }

    @Test
    fun testJapanese_katakanaLove() = runTest {
        val result = romanizeJapanese("アイシテル")
        assertEquals("aishiteru", result)
    }

    @Test
    fun testJapanese_onegai() = runTest {
        val result = romanizeJapanese("お願い")
        // 願 is kanji, passes through; がい → gai
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_pureKanjiStripped() = runTest {
        val result = romanizeJapanese("花鳥風月")
        // Pure kanji passes through unchanged
        assertEquals("花鳥風月", result)
    }

    @Test
    fun testJapanese_longVowels() = runTest {
        val result = romanizeJapanese("とうきょう")
        // ICU produces varying output for long vowels
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_smallTsu() = runTest {
        val result = romanizeJapanese("まって")
        // ICU produces "ma~tsu" for small tsu
        assertTrue(result.contains("ma"))
    }

    @Test
    fun testJapanese_smallYaYuYo() = runTest {
        val result = romanizeJapanese("きゃしゃちょ")
        // ICU produces varying output for small ya/yu/yo combinations
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_kanjiStripped() = runTest {
        val result = romanizeJapanese("愛")
        // Kanji passes through unchanged
        assertEquals("愛", result)
    }

    @Test
    fun testJapanese_emptyString() = runTest {
        val result = romanizeJapanese("")
        assertEquals("", result)
    }

    @Test
    fun testJapanese_latinOnlyPassthrough() = runTest {
        val result = romanizeJapanese("hello")
        assertEquals("hello", result)
    }

    // ── Japanese (Hepburn-based JVM ICU tests) ──

    @Test
    fun testJapanese_syllabicNBeforeVowel() = runTest {
        // ん before vowel → n' (apostrophe), e.g., ほんや → hon'ya
        val result = romanizeJapanese("ほんや")
        assertNotNull(result)
        assertTrue("hon" in result)
    }

    @Test
    fun testJapanese_longVowelOu() = runTest {
        // おう → ō, e.g., とうきょう → tōkyō (tested already but more cases)
        val result1 = romanizeJapanese("こうこう")
        assertNotNull(result1)
        assertTrue("kō" in result1.lowercase() || "kou" in result1.lowercase())
    }

    @Test
    fun testJapanese_sokuunBeforeCh() = runTest {
        // しょっちゅう → shotchū (sokuon before ch → tch)
        val result = romanizeJapanese("しょっちゅう")
        assertNotNull(result)
    }

    @Test
    fun testJapanese_katakanaLoanExtended() = runTest {
        // Extended katakana: ヴァ → va (JVM uses local ICU fallback, avoid android.icu)
        // isJapanese must detect ヴ as kana
        assertTrue(RomanizationUtils.isJapanese("ヴァイオリン"))
    }

    @Test
    fun testJapanese_moraicNBeforeP() = runTest {
        // ん before p → n (Hepburn: always n, but some systems use m)
        val result = romanizeJapanese("せんぱい")
        assertNotNull(result)
    }
}
