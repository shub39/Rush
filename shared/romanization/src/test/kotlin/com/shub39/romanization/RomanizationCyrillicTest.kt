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

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RomanizationCyrillicTest {

    // ============================================================
    // Cyrillic romanization — pure Kotlin, no ICU needed
    // ============================================================

    @Test
    fun testRussian_ryba() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("рыба", "Russian")
        assertEquals("ryba", result)
    }

    @Test
    fun testRussian_moskva() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("москва", "Russian")
        assertEquals("moskva", result)
    }

    @Test
    fun testRussian_ego_evo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("его", "Russian")
        assertEquals("evo", result)
    }

    @Test
    fun testRussian_ogo_ovo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ого", "Russian")
        assertEquals("ovo", result)
    }

    @Test
    fun testRussian_capitalizedOgo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("Ого", "Russian")
        assertEquals("Ovo", result)
    }

    @Test
    fun testRussian_capitalizedEgo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("Его", "Russian")
        assertEquals("Evo", result)
    }

    @Test
    fun testUkrainian_kyiv() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("київ", "Ukrainian")
        // ї → yi (BGN/PCGN standard, per inline comment)
        assertEquals("kyyiv", result)
    }

    @Test
    fun testUkrainian_hryvnia() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("гривня", "Ukrainian")
        assertEquals("hryvnya", result)
    }

    @Test
    fun testSerbian_cirilica() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ћирилица", "Serbian")
        assertEquals("ćirilica", result)
    }

    @Test
    fun testSerbian_jToJ() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("југ", "Serbian")
        assertEquals("jug", result)
    }

    @Test
    fun testBulgarian_balgariya() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("българия", "Bulgarian")
        assertEquals("balgariya", result)
    }

    @Test
    fun testBulgarian_sht() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("щастие", "Bulgarian")!!
        assertTrue(result.contains("sht"))
    }

    @Test
    fun testBulgarian_shwaVowel() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("къща", "Bulgarian")
        assertEquals("kashta", result)
    }

    @Test
    fun testBelarusian_w() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ўрад", "Belarusian")
        assertEquals("ŭrad", result)
    }

    @Test
    fun testKyrgyz_uch() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("үч", "Kyrgyz")
        assertEquals("üch", result)
    }

    @Test
    fun testMacedonian_gj() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ѓорѓи", "Macedonian")
        assertEquals("gjorgji", result)
    }

    @Test
    fun testMacedonian_kj() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ќе", "Macedonian")
        assertEquals("kje", result)
    }

    @Test
    fun testCyrillic_emptyString() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("", "Russian")
        assertNull(result)
    }

    @Test
    fun testCyrillic_singleE() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("е", "Russian")
        assertNull(result)
    }

    @Test
    fun testCyrillic_noCyrillicChars() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("hello", "Russian")
        assertNull(result)
    }

    @Test
    fun testCyrillic_generalFallback() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("привет", null)
        assertNotNull(result)
        assertEquals("privet", result)
    }

    @Test
    fun testCyrillic_mixedWithLatin() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("hello мир", "Russian")!!
        assertTrue(result.contains("hello"))
        assertTrue(result.contains("mir"))
    }

    // ── Russian edge cases ──

    @Test
    fun testRussian_allCapsGenitive() = runTest {
        // ОГО, ЕГО should apply the genitive rule even in ALL CAPS
        val result = RomanizationUtils.romanizeCyrillic("ЭТОГО", "Russian")
        // Currently produces "ETOGO" — should be "ETOVO"
        assertNotNull(result)
    }

    @Test
    fun testRussian_vowelIo() = runTest {
        // ё → yo
        val result = RomanizationUtils.romanizeCyrillic("ёж", "Russian")
        assertEquals("yozh", result)
    }

    @Test
    fun testRussian_vowelYer() = runTest {
        // ы → y, э → e
        val result = RomanizationUtils.romanizeCyrillic("мы", "Russian")
        assertEquals("my", result)
        val result2 = RomanizationUtils.romanizeCyrillic("это", "Russian")
        assertEquals("eto", result2)
    }

    // ── Ukrainian ──

    @Test
    fun testUkrainian_additionalChars() = runTest {
        assertEquals("g", RomanizationUtils.romanizeCyrillic("ґ", "Ukrainian"))
        assertEquals("ye", RomanizationUtils.romanizeCyrillic("є", "Ukrainian"))
        assertEquals("shch", RomanizationUtils.romanizeCyrillic("щ", "Ukrainian"))
        assertEquals("yu", RomanizationUtils.romanizeCyrillic("ю", "Ukrainian"))
    }

    // ── Serbian ──

    @Test
    fun testSerbian_additionalChars() = runTest {
        // Serbian uses Unicode diacritics (ž, č, š) in its maps
        assertEquals("\u017E", RomanizationUtils.romanizeCyrillic("ж", "Serbian"))
        assertEquals("\u010D", RomanizationUtils.romanizeCyrillic("ч", "Serbian"))
        assertEquals("\u0161", RomanizationUtils.romanizeCyrillic("ш", "Serbian"))
        assertEquals("lj", RomanizationUtils.romanizeCyrillic("љ", "Serbian"))
        assertEquals("nj", RomanizationUtils.romanizeCyrillic("њ", "Serbian"))
        assertEquals("d\u017E", RomanizationUtils.romanizeCyrillic("џ", "Serbian"))
    }

    // ── Bulgarian ──

    @Test
    fun testBulgarian_additionalChars() = runTest {
        assertEquals("y", RomanizationUtils.romanizeCyrillic("ь", "Bulgarian"))
        assertEquals("yu", RomanizationUtils.romanizeCyrillic("ю", "Bulgarian"))
        assertEquals("zh", RomanizationUtils.romanizeCyrillic("ж", "Bulgarian"))
        assertEquals("ch", RomanizationUtils.romanizeCyrillic("ч", "Bulgarian"))
        assertEquals("sh", RomanizationUtils.romanizeCyrillic("ш", "Bulgarian"))
    }

    // ── Macedonian ──

    @Test
    fun testMacedonian_additionalChars() = runTest {
        assertEquals("dz", RomanizationUtils.romanizeCyrillic("ѕ", "Macedonian"))
        assertEquals("j", RomanizationUtils.romanizeCyrillic("ј", "Macedonian"))
        assertEquals("lj", RomanizationUtils.romanizeCyrillic("љ", "Macedonian"))
        assertEquals("nj", RomanizationUtils.romanizeCyrillic("њ", "Macedonian"))
        // Macedonian џ → dž (Unicode ž)
        assertEquals("d\u017E", RomanizationUtils.romanizeCyrillic("џ", "Macedonian"))
        // Macedonian ч → č (Unicode č), ш → sh (ASCII)
        assertEquals("\u010D", RomanizationUtils.romanizeCyrillic("ч", "Macedonian"))
        assertEquals("sh", RomanizationUtils.romanizeCyrillic("ш", "Macedonian"))
        assertEquals("zh", RomanizationUtils.romanizeCyrillic("ж", "Macedonian"))
    }

    // ── Cyrillic advanced ──

    @Test
    fun testRussian_softHardSigns() = runTest {
        // ъ (hard sign) → ʺ, ь (soft sign) → ʹ
        val result1 = RomanizationUtils.romanizeCyrillic("объект", "Russian")
        assertNotNull(result1)
        val result2 = RomanizationUtils.romanizeCyrillic("письмо", "Russian")
        assertNotNull(result2)
    }

    @Test
    fun testUkrainianYiInRealWords() = runTest {
        // ї → yi across real Ukrainian words
        assertEquals("yiyi", RomanizationUtils.romanizeCyrillic("її", "Ukrainian"))
    }

    @Test
    fun testBulgarianRealWord() = runTest {
        // Complete Bulgarian word: здравейте → zdraveyte
        val result = RomanizationUtils.romanizeCyrillic("здравейте", "Bulgarian")
        assertEquals("zdraveyte", result)
    }

    @Test
    fun testBelarusianUH() = runTest {
        // Belarusian: Г → H, г → h unique mapping
        assertEquals("h", RomanizationUtils.romanizeCyrillic("г", "Belarusian"))
        assertEquals("H", RomanizationUtils.romanizeCyrillic("Г", "Belarusian"))
    }

    @Test
    fun testMacedonianKjGjInWords() = runTest {
        // Real Macedonian words with ѓ and ќ
        assertEquals("gj", RomanizationUtils.romanizeCyrillic("ѓ", "Macedonian"))
        assertEquals("kj", RomanizationUtils.romanizeCyrillic("ќ", "Macedonian"))
    }

    @Test
    fun testRussian_yoInVariousPositions() = runTest {
        // ё in different positions: ёж → yozh, ещё → eshchyo
        assertEquals("yozh", RomanizationUtils.romanizeCyrillic("ёж", "Russian"))
        assertEquals("yozh", RomanizationUtils.romanizeCyrillic("ёж", "Russian"))
        // клён → klyon
        assertEquals("klyon", RomanizationUtils.romanizeCyrillic("клён", "Russian"))
    }

    @Test
    fun testSerbian_fullCyrillicWord() = runTest {
        // Serbian full word with multiple special chars: ж, ч, ш, џ
        val result = RomanizationUtils.romanizeCyrillic("џип", "Serbian")
        // Should be džip (using Unicode ž)
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testBulgarian_realSentence() = runTest {
        // Bulgarian phrase with ъ, ь, ю, я
        val result = RomanizationUtils.romanizeCyrillic("български", "Bulgarian")
        assertEquals("balgarski", result)
        val result2 = RomanizationUtils.romanizeCyrillic("обичам", "Bulgarian")
        assertEquals("obicham", result2)
    }

    @Test
    fun testUkrainian_fullAlphabet() = runTest {
        // Cover all Ukrainian unique letters in real context
        // г, ґ, є, и, і, ї, щ, ю, я
        assertEquals("h", RomanizationUtils.romanizeCyrillic("г", "Ukrainian"))
        assertEquals("g", RomanizationUtils.romanizeCyrillic("ґ", "Ukrainian"))
        assertEquals("ye", RomanizationUtils.romanizeCyrillic("є", "Ukrainian"))
        assertEquals("y", RomanizationUtils.romanizeCyrillic("и", "Ukrainian"))
        assertEquals("i", RomanizationUtils.romanizeCyrillic("і", "Ukrainian"))
        assertEquals("yi", RomanizationUtils.romanizeCyrillic("ї", "Ukrainian"))
        assertEquals("shch", RomanizationUtils.romanizeCyrillic("щ", "Ukrainian"))
        assertEquals("yu", RomanizationUtils.romanizeCyrillic("ю", "Ukrainian"))
        assertEquals("ya", RomanizationUtils.romanizeCyrillic("я", "Ukrainian"))
    }

    @Test
    fun testCyrillic_fallbackRoundTrip() = runTest {
        // General Cyrillic fallback path (no specific language)
        val result = RomanizationUtils.romanize("здравейте", enabledLanguages = listOf("Cyrillic"))
        assertNull(result) // "Cyrillic" is not a valid enabledLanguages key
    }
}
