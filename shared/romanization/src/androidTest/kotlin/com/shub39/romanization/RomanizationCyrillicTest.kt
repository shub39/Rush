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
 * Cyrillic romanization tests — BGN/PCGN per language.
 *
 * BGN/PCGN: https://geonames.nga.mil/geonames/GNSSearch/GNSDocs/romanization/ Russian genitive rule
 * documented in BGN/PCGN agreements. Serbian uses Unicode diacritics (ž, č, š, lj, nj, dž) per
 * standard.
 */
class RomanizationCyrillicTest {

    private lateinit var romanizationUtils: RomanizationUtils

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        romanizationUtils = RomanizationUtils(context)
        romanizationUtils.loadReadingDictionary(context)
    }

    // Cyrillic corner cases

    @Test
    fun testRussian_ryba() = runTest {
        val result = romanizationUtils.romanizeCyrillic("рыба", "Russian")
        assertEquals("ryba", result)
    }

    @Test
    fun testRussian_moskva() = runTest {
        val result = romanizationUtils.romanizeCyrillic("москва", "Russian")
        assertEquals("moskva", result)
    }

    @Test
    fun testRussian_ego_evo() = runTest {
        val result = romanizationUtils.romanizeCyrillic("его", "Russian")
        assertEquals("evo", result)
    }

    @Test
    fun testRussian_ogo_ovo() = runTest {
        val result = romanizationUtils.romanizeCyrillic("ого", "Russian")
        assertEquals("ovo", result)
    }

    @Test
    fun testRussian_capitalizedOgo() = runTest {
        val result = romanizationUtils.romanizeCyrillic("Ого", "Russian")
        assertEquals("Ovo", result)
    }

    @Test
    fun testRussian_capitalizedEgo() = runTest {
        val result = romanizationUtils.romanizeCyrillic("Его", "Russian")
        assertEquals("Evo", result)
    }

    @Test
    fun testUkrainian_kyiv() = runTest {
        val result = romanizationUtils.romanizeCyrillic("київ", "Ukrainian")
        assertEquals("kyiv", result)
    }

    @Test
    fun testUkrainian_hryvnia() = runTest {
        val result = romanizationUtils.romanizeCyrillic("гривня", "Ukrainian")
        assertEquals("hryvnya", result)
    }

    @Test
    fun testSerbian_cirilica() = runTest {
        val result = romanizationUtils.romanizeCyrillic("ћирилица", "Serbian")
        assertEquals("ćirilica", result)
    }

    @Test
    fun testSerbian_jToJ() = runTest {
        val result = romanizationUtils.romanizeCyrillic("југ", "Serbian")
        assertEquals("jug", result)
    }

    @Test
    fun testBulgarian_balgariya() = runTest {
        val result = romanizationUtils.romanizeCyrillic("българия", "Bulgarian")
        assertEquals("balgariya", result)
    }

    @Test
    fun testBulgarian_sht() = runTest {
        val result = romanizationUtils.romanizeCyrillic("щастие", "Bulgarian")!!
        assertTrue(result.contains("sht"))
    }

    @Test
    fun testBulgarian_shwaVowel() = runTest {
        val result = romanizationUtils.romanizeCyrillic("къща", "Bulgarian")
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
        assertEquals("privet", result)
    }

    @Test
    fun testCyrillic_mixedWithLatin() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("hello мир", "Russian")!!
        assertTrue(result.contains("hello"))
        assertTrue(result.contains("mir"))
    }

    // ── Advanced Cyrillic ──

    @Test
    fun testCyrillic_bulgarianFullWord() = runTest {
        assertEquals("zdraveyte", RomanizationUtils.romanizeCyrillic("здравейте", "Bulgarian"))
        assertEquals("balgarski", RomanizationUtils.romanizeCyrillic("български", "Bulgarian"))
    }

    @Test
    fun testCyrillic_ukrainianAllUnique() = runTest {
        assertEquals("h", RomanizationUtils.romanizeCyrillic("г", "Ukrainian"))
        assertEquals("g", RomanizationUtils.romanizeCyrillic("ґ", "Ukrainian"))
        assertEquals("ye", RomanizationUtils.romanizeCyrillic("є", "Ukrainian"))
        assertEquals("y", RomanizationUtils.romanizeCyrillic("и", "Ukrainian"))
        assertEquals("i", RomanizationUtils.romanizeCyrillic("і", "Ukrainian"))
        assertEquals("yi", RomanizationUtils.romanizeCyrillic("ї", "Ukrainian"))
        assertEquals("yu", RomanizationUtils.romanizeCyrillic("ю", "Ukrainian"))
        assertEquals("ya", RomanizationUtils.romanizeCyrillic("я", "Ukrainian"))
    }

    @Test
    fun testCyrillic_russianAllCapsGenitive() = runTest {
        assertEquals("OVO", RomanizationUtils.romanizeCyrillic("ОГО", "Russian"))
        assertEquals("EVO", RomanizationUtils.romanizeCyrillic("ЕГО", "Russian"))
    }

    @Test
    fun testCyrillic_serbianWithDiacritics() = runTest {
        assertEquals("\u017E", RomanizationUtils.romanizeCyrillic("ж", "Serbian"))
        assertEquals("\u010D", RomanizationUtils.romanizeCyrillic("ч", "Serbian"))
        assertEquals("\u0161", RomanizationUtils.romanizeCyrillic("ш", "Serbian"))
        assertEquals("d\u017E", RomanizationUtils.romanizeCyrillic("џ", "Serbian"))
    }
}
