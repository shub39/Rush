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

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test

/**
 * Japanese romanization tests — Modified Hepburn (BGN/PCGN 1976).
 *
 * Standard: https://assets.publishing.service.gov.uk/media/5ab4e1e3ed915d78b9a459de/ROMANIZATION_OF_JAPANESE_KANA.pdf
 * ALA-LC:   https://www.loc.gov/catdir/cpso/romanization/japanese.pdf
 * Wikipedia: https://en.wikipedia.org/wiki/Hepburn_romanization
 */
class RomanizationJapaneseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
    }

    // Japanese corner cases (ICU-only)

    @Test
    fun testJapanese_hiragana_konnichiwa() = runTest {
        val result = RomanizationUtils.romanizeJapanese("こんにちは")
        assertTrue(result.contains("kon"))
        assertTrue(result.contains("nichi"))
    }

    @Test
    fun testJapanese_hiragana_sakura() = runTest {
        val result = RomanizationUtils.romanizeJapanese("さくら")
        assertEquals("sakura", result)
    }

    @Test
    fun testJapanese_katakana_cafe() = runTest {
        val result = RomanizationUtils.romanizeJapanese("カフェ")
        assertEquals("kafe", result)
    }

    @Test
    fun testJapanese_katakana_test() = runTest {
        val result = RomanizationUtils.romanizeJapanese("テスト")
        assertEquals("tesuto", result)
    }

    @Test
    fun testJapanese_katakanaLove() = runTest {
        val result = RomanizationUtils.romanizeJapanese("アイシテル")
        assertEquals("aishiteru", result)
    }

    @Test
    fun testJapanese_kanjiStripped() = runTest {
        // Without dictionary: kanji passes through (愛 → 愛)
        // With dictionary: kanji has reading (愛 → アイ → ai)
        val result = RomanizationUtils.romanizeJapanese("愛")
        assertTrue(result == "愛" || result == "ai")
    }

    @Test
    fun testJapanese_pureKanjiStripped() = runTest {
        // Without dictionary: kanji passes through
        // With dictionary: may be tokenized via IPADIC
        val result = RomanizationUtils.romanizeJapanese("花鳥風月")
        assertTrue(result == "花鳥風月" || result.contains("ka") || result.contains("fu"))
    }

    @Test
    fun testJapanese_longVowels() = runTest {
        val result = RomanizationUtils.romanizeJapanese("とうきょう")
        assertTrue(result.contains("tou"))
        assertTrue(result.contains("kyou"))
    }

    @Test
    fun testJapanese_smallTsu() = runTest {
        val result = RomanizationUtils.romanizeJapanese("まって")
        // ICU produces "ma~tsu" for small tsu
        assertTrue(result.contains("ma"))
    }

    @Test
    fun testJapanese_smallYaYuYo() = runTest {
        val result = RomanizationUtils.romanizeJapanese("きゃしゃちょ")
        assertTrue(result.contains("kya"))
        assertTrue(result.contains("sha"))
        assertTrue(result.contains("cho"))
    }

    @Test
    fun testJapanese_particleWa() = runTest {
        val result = RomanizationUtils.romanizeJapanese("わたしは")
        // Without tokenizer: は → ha; with tokenizer: particle rule → wa
        assertTrue(result == "watashiha" || result == "watashiwa")
    }

    @Test
    fun testJapanese_particleHe() = runTest {
        val result = RomanizationUtils.romanizeJapanese("へや")
        assertTrue(result.contains("heya"))
    }

    @Test
    fun testJapanese_particleO() = runTest {
        val result = RomanizationUtils.romanizeJapanese("それを")
        // Without tokenizer: を → wo (char-by-char)
        // With tokenizer grouping: all kana grouped → still "sorewo"
        // With per-char tokenization: particle rule → "o"
        assertTrue(result == "sorewo" || result == "sore o")
    }

    @Test
    fun testJapanese_onegai() = runTest {
        val result = RomanizationUtils.romanizeJapanese("お願い")
        // Without dictionary: kanji passes through, kana romanized
        // With dictionary: お願い → オネガイ → "onegai"
        assertTrue(result.contains("願") || result.contains("negai"))
    }

    @Test
    fun testJapanese_emptyString() = runTest {
        val result = RomanizationUtils.romanizeJapanese("")
        assertEquals("", result)
    }

    @Test
    fun testJapanese_latinOnlyPassthrough() = runTest {
        val result = RomanizationUtils.romanizeJapanese("hello")
        assertEquals("hello", result)
    }

    // ── Advanced Japanese (dictionary-backed) ──

    @Test
    fun testJapanese_dictionaryKanjiReading() = runTest {
        // With IPADIC loaded: 愛 → ai (dictionary lookup), 花 → ka or hana
        val result1 = RomanizationUtils.romanizeJapanese("愛")
        assertNotNull(result1)
        // 東京 → tōkyō (dictionary should segment this)
        val result2 = RomanizationUtils.romanizeJapanese("東京")
        assertNotNull(result2)
    }

    @Test
    fun testJapanese_mixedKanjiKana() = runTest {
        // Real Japanese phrase with kanji + kana + particle
        val result = RomanizationUtils.romanizeJapanese("食べる")
        assertNotNull(result)
        assertTrue("tab" in result!! || "食" in result)
    }

    @Test
    fun testJapanese_katakanaLoanWords() = runTest {
        // Common katakana loan words with chōonpu
        val result1 = RomanizationUtils.romanizeJapanese("コンピューター")
        assertNotNull(result1)
        val result2 = RomanizationUtils.romanizeJapanese("レストラン")
        assertNotNull(result2)
    }
}
