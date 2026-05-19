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

class RomanizationHindiTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
    }

    // Hindi corner cases

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

    // ── Advanced Hindi/Punjabi ──

    @Test
    fun testHindi_vowelLengthContrast() = runTest {
        assertEquals("kil", RomanizationUtils.romanizeHindi("किल"))
        assertEquals("keel", RomanizationUtils.romanizeHindi("कील"))
        assertEquals("pul", RomanizationUtils.romanizeHindi("पुल"))
        assertEquals("pool", RomanizationUtils.romanizeHindi("पूल"))
    }

    @Test
    fun testHindi_conjunctTrShr() = runTest {
        assertEquals("tr", RomanizationUtils.romanizeHindi("त्र"))
        assertEquals("shr", RomanizationUtils.romanizeHindi("श्र"))
        assertEquals("gy", RomanizationUtils.romanizeHindi("ज्ञ"))
    }
}
