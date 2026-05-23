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
 * romanize() auto-detect routing tests — exercises language detection and dispatch.
 *
 * References: Korean — RR: https://www.korean.go.kr/front_eng/roman/roman_01.do Japanese — Hepburn:
 * https://assets.publishing.service.gov.uk/media/5ab4e1e3ed915d78b9a459de/ROMANIZATION_OF_JAPANESE_KANA.pdf
 * Chinese — ISO 7098: https://www.loc.gov/catdir/cpso/romanization/chinese.pdf Hindi/Punjabi — ISO
 * 15919: https://www.iso.org/standard/28333.html Cyrillic — BGN/PCGN:
 * https://geonames.nga.mil/geonames/GNSSearch/GNSDocs/romanization/
 */
class RomanizationAutoDetectTest {

    // ============================================================
    // romanize() auto-detect — pure Kotlin, no ICU needed
    // ============================================================

    @Test
    fun testRomanize_noMatch() = runTest {
        val result =
            RomanizationUtils.romanize(
                "hello world",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese", "Russian"),
            )
        assertNull(result)
    }

    @Test
    fun testRomanize_emptyString() = runTest {
        val result = RomanizationUtils.romanize("", enabledLanguages = listOf("Japanese"))
        assertNull(result)
    }

    @Test
    fun testRomanize_disabledLanguage() = runTest {
        val result = RomanizationUtils.romanize("こんにちは", enabledLanguages = emptyList())
        assertNull(result)
    }

    @Test
    fun testRomanize_autoDetectKorean() = runTest {
        val result =
            RomanizationUtils.romanize(
                "안녕하세요",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertNotNull(result)
        assertEquals("annyeonghaseyo", result)
    }

    @Test
    fun testRomanize_autoDetectRussian() = runTest {
        val result =
            RomanizationUtils.romanize("рыба", enabledLanguages = listOf("Russian", "Japanese"))
        assertNotNull(result)
        assertEquals("ryba", result)
    }

    @Test
    fun testRomanize_autoDetectHindi() = runTest {
        val result = RomanizationUtils.romanize("नमस्ते", enabledLanguages = listOf("Hindi"))
        assertNotNull(result)
        assertEquals("namaste", result)
    }

    @Test
    fun testRomanize_autoDetectPunjabi() = runTest {
        val result =
            RomanizationUtils.romanize("ਸਤ ਸ੍ਰੀ ਅਕਾਲ", enabledLanguages = listOf("Punjabi"))
        assertNotNull(result)
        assertEquals("sat sree akaal", result)
    }

    @Test
    fun testRomanize_whitespaceOnly() = runTest {
        val result = RomanizationUtils.romanize("   ", enabledLanguages = listOf("Japanese"))
        assertNull(result)
    }

    @Test
    fun testRomanize_specialCharactersOnly() = runTest {
        val result =
            RomanizationUtils.romanize("!@#$%", enabledLanguages = listOf("Japanese", "Russian"))
        assertNull(result)
    }

    // ── romanize() auto-detect for missing Cyrillic languages ──

    @Test
    fun testRomanize_autoDetectUkrainian() = runTest {
        val result = RomanizationUtils.romanize("київ", enabledLanguages = listOf("Ukrainian"))
        assertNotNull(result)
        assertEquals("kyyiv", result)
    }

    @Test
    fun testRomanize_autoDetectSerbian() = runTest {
        val result = RomanizationUtils.romanize("ћилирика", enabledLanguages = listOf("Serbian"))
        assertNotNull(result)
    }

    // ── Edge cases ──

    @Test
    fun testRomanize_mixedDevanagariGurmukhi() = runTest {
        // Text with both Hindi and Punjabi characters — should route to one
        // This tests detection priority when text is ambiguous
        val result =
            RomanizationUtils.romanize(
                "नमस्ते ਸਤ ਸ੍ਰੀ ਅਕਾਲ",
                enabledLanguages = listOf("Hindi", "Punjabi"),
            )
        // One of the two should match
        assertNotNull(result)
    }

    @Test
    fun testCyrillic_fallbackRoundTrip() = runTest {
        // General Cyrillic fallback path (no specific language)
        val result = RomanizationUtils.romanize("здравейте", enabledLanguages = listOf("Cyrillic"))
        assertNull(result) // "Cyrillic" is not a valid enabledLanguages key
    }
}
