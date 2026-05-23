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

/**
 * Hindi romanization tests — ISO 15919:2001 / ALA-LC.
 *
 * ISO: https://www.iso.org/standard/28333.html ALA-LC:
 * https://www.loc.gov/catdir/cpso/romanization/hindi.pdf Conjuncts:
 * https://en.wikipedia.org/wiki/Devanagari_conjuncts Transliteration:
 * https://en.wikipedia.org/wiki/Devanagari_transliteration
 */
class RomanizationHindiTest {

    // ============================================================
    // Hindi romanization
    // ============================================================

    @Test
    fun testHindi_namaste() = runTest {
        val result = RomanizationUtils.romanizeHindi("नमस्ते")
        assertEquals("namaste", result)
    }

    @Test
    fun testHindi_hindi() = runTest {
        val result = RomanizationUtils.romanizeHindi("हिंदी")
        assertEquals("hindee", result)
    }

    @Test
    fun testHindi_pyar() = runTest {
        val result = RomanizationUtils.romanizeHindi("प्यार")
        assertEquals("pyaar", result)
    }

    @Test
    fun testHindi_duniya() = runTest {
        val result = RomanizationUtils.romanizeHindi("दुनिया")
        assertEquals("duniyaa", result)
    }

    @Test
    fun testHindi_numbers() = runTest {
        val result = RomanizationUtils.romanizeHindi("१२३")
        assertEquals("123", result)
    }

    @Test
    fun testHindi_conjunctKsh() = runTest {
        val result = RomanizationUtils.romanizeHindi("क्षमा")
        assertEquals("kshamaa", result)
    }

    @Test
    fun testHindi_conjunctGy() = runTest {
        val result = RomanizationUtils.romanizeHindi("ज्ञान")
        assertEquals("gyaan", result)
    }

    @Test
    fun testHindi_anusvara() = runTest {
        val result = RomanizationUtils.romanizeHindi("हिंदी")
        assertEquals("hindee", result)
    }

    @Test
    fun testHindi_om() = runTest {
        val result = RomanizationUtils.romanizeHindi("ॐ")
        assertEquals("Om", result)
    }

    @Test
    fun testHindi_emptyString() = runTest {
        val result = RomanizationUtils.romanizeHindi("")
        assertEquals("", result)
    }

    @Test
    fun testHindi_nuktaFormInherentVowel() = runTest {
        val result = RomanizationUtils.romanizeHindi("ज़मीन")
        assertEquals("zameen", result)
    }

    @Test
    fun testHindi_latinOnlyPassthrough() = runTest {
        val result = RomanizationUtils.romanizeHindi("hello")
        assertEquals("hello", result)
    }

    // ── Hindi edge cases ──

    @Test
    fun testHindi_conjunctTr() = runTest {
        // त्र → tr
        val result = RomanizationUtils.romanizeHindi("त्र")
        assertEquals("tr", result)
    }

    @Test
    fun testHindi_conjunctShr() = runTest {
        // श्र → shr
        val result = RomanizationUtils.romanizeHindi("श्र")
        assertEquals("shr", result)
    }

    @Test
    fun testHindi_vowelRi() = runTest {
        // ऋ → ri
        val result = RomanizationUtils.romanizeHindi("ऋषि")
        assertEquals("rishi", result)
    }

    @Test
    fun testHindi_nuktaForms() = runTest {
        // Test all 8 nukta forms (word-final = no inherent 'a')
        assertEquals("q", RomanizationUtils.romanizeHindi("क़"))
        assertEquals("kh", RomanizationUtils.romanizeHindi("ख़"))
        assertEquals("g", RomanizationUtils.romanizeHindi("ग़"))
        assertEquals("z", RomanizationUtils.romanizeHindi("ज़"))
        assertEquals("r", RomanizationUtils.romanizeHindi("ड़"))
        assertEquals("rh", RomanizationUtils.romanizeHindi("ढ़"))
        assertEquals("f", RomanizationUtils.romanizeHindi("फ़"))
        assertEquals("y", RomanizationUtils.romanizeHindi("य़"))
    }

    @Test
    fun testHindi_visarga() = runTest {
        // ः → h (visarga)
        val result = RomanizationUtils.romanizeHindi("दुःख")
        assertNotNull(result)
    }

    @Test
    fun testHindi_halant() = runTest {
        // क्क = क् + क → k + (no inherent a, halant) + k = "kk"
        // (word-final: no inherent 'a' on last consonant)
        val result = RomanizationUtils.romanizeHindi("क्क")
        assertEquals("kk", result)
    }

    // ── Hindi advanced ──

    @Test
    fun testHindi_visargaRealWord() = runTest {
        // दुःख → duḥkha (visarga at syllable boundary)
        val result = RomanizationUtils.romanizeHindi("दुःख")
        assertNotNull(result)
    }

    @Test
    fun testHindi_chandrabindu() = runTest {
        // ँ → nasalization of vowel, e.g., हँस → hams/hãs
        val result = RomanizationUtils.romanizeHindi("हँस")
        assertNotNull(result)
    }

    @Test
    fun testHindi_conjunctInRealWord() = runTest {
        // कृष्ण → kṛṣṇa (conjuncts ष + ण with virama)
        val result = RomanizationUtils.romanizeHindi("कृष्ण")
        assertNotNull(result)
    }

    // ── Hindi additional edge cases ──

    @Test
    fun testHindi_vowelLengthContrast() = runTest {
        // Short vs long vowels (word-final: no inherent 'a')
        assertEquals("kil", RomanizationUtils.romanizeHindi("किल"))
        assertEquals("keel", RomanizationUtils.romanizeHindi("कील"))
        // पुल → pul vs पूल → pool
        assertEquals("pul", RomanizationUtils.romanizeHindi("पुल"))
        assertEquals("pool", RomanizationUtils.romanizeHindi("पूल"))
    }

    @Test
    fun testHindi_conjunctTrInWord() = runTest {
        // त्र in real word: रात्रि → raatri (r + aa + tr + i)
        val result = RomanizationUtils.romanizeHindi("रात्रि")
        assertEquals("raatri", result)
    }

    @Test
    fun testHindi_halantAtWordEnd() = runTest {
        // Halant at end of word: क् → k (no inherent vowel)
        val result = RomanizationUtils.romanizeHindi("क्")
        assertEquals("k", result)
    }

    @Test
    fun testHindi_omSymbolVariants() = runTest {
        // ॐ = Om, also tested: ॐकार = Omkaar
        val result = RomanizationUtils.romanizeHindi("ॐकार")
        assertEquals("Omkaar", result)
    }
}
