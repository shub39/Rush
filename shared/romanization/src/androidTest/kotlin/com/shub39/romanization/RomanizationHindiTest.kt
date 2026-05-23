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
import org.junit.Before
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

    private lateinit var romanizationUtils: RomanizationUtils

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        romanizationUtils = RomanizationUtils(context)
        romanizationUtils.loadReadingDictionary(context)
    }

    // Hindi corner cases

    @Test
    fun testHindi_namaste() = runTest {
        val result = romanizationUtils.romanizeHindi("नमस्ते")
        assertEquals("namaste", result)
    }

    @Test
    fun testHindi_hindi() = runTest {
        val result = romanizationUtils.romanizeHindi("हिंदी")
        assertEquals("hindee", result)
    }

    @Test
    fun testHindi_pyar() = runTest {
        val result = romanizationUtils.romanizeHindi("प्यार")
        assertEquals("pyaar", result)
    }

    @Test
    fun testHindi_duniya() = runTest {
        val result = romanizationUtils.romanizeHindi("दुनिया")
        assertEquals("duniyaa", result)
    }

    @Test
    fun testHindi_numbers() = runTest {
        val result = romanizationUtils.romanizeHindi("१२३")
        assertEquals("123", result)
    }

    @Test
    fun testHindi_conjunctKsh() = runTest {
        val result = romanizationUtils.romanizeHindi("क्षमा")
        assertEquals("kshamaa", result)
    }

    @Test
    fun testHindi_conjunctGy() = runTest {
        val result = romanizationUtils.romanizeHindi("ज्ञान")
        assertEquals("gyaan", result)
    }

    @Test
    fun testHindi_anusvara() = runTest {
        val result = romanizationUtils.romanizeHindi("हिंदी")
        assertEquals("hindee", result)
    }

    @Test
    fun testHindi_om() = runTest {
        val result = romanizationUtils.romanizeHindi("ॐ")
        assertEquals("Om", result)
    }

    @Test
    fun testHindi_emptyString() = runTest {
        val result = romanizationUtils.romanizeHindi("")
        assertEquals("", result)
    }

    @Test
    fun testHindi_nuktaFormInherentVowel() = runTest {
        val result = romanizationUtils.romanizeHindi("ज़मीन")
        assertEquals("zameen", result)
    }

    @Test
    fun testHindi_latinOnlyPassthrough() = runTest {
        val result = romanizationUtils.romanizeHindi("hello")
        assertEquals("hello", result)
    }

    // ── Advanced Hindi/Punjabi ──

    @Test
    fun testHindi_vowelLengthContrast() = runTest {
        assertEquals("kil", romanizationUtils.romanizeHindi("किल"))
        assertEquals("keel", romanizationUtils.romanizeHindi("कील"))
        assertEquals("pul", romanizationUtils.romanizeHindi("पुल"))
        assertEquals("pool", romanizationUtils.romanizeHindi("पूल"))
    }

    @Test
    fun testHindi_conjunctTrShr() = runTest {
        assertEquals("tr", romanizationUtils.romanizeHindi("त्र"))
        assertEquals("shr", romanizationUtils.romanizeHindi("श्र"))
        assertEquals("gy", romanizationUtils.romanizeHindi("ज्ञ"))
    }
}
