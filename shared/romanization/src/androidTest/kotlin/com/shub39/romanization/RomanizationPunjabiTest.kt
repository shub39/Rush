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
 * Punjabi romanization tests — ISO 15919:2001 (Gurmukhi framework).
 *
 * ISO: https://www.iso.org/standard/28333.html
 * Uses same ISO 15919 framework as Hindi for Gurmukhi script.
 */
class RomanizationPunjabiTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
    }

    // Punjabi corner cases

    @Test
    fun testPunjabi_satSriAkal() = runTest {
        val result = RomanizationUtils.romanizePunjabi("ਸਤ ਸ੍ਰੀ ਅਕਾਲ")
        assertEquals("sat sree akaal", result)
    }

    @Test
    fun testPunjabi_punjab() = runTest {
        val result = RomanizationUtils.romanizePunjabi("ਪੰਜਾਬ")
        assertEquals("panjaab", result)
    }

    @Test
    fun testPunjabi_numbers() = runTest {
        val result = RomanizationUtils.romanizePunjabi("੧੨੩")
        assertEquals("123", result)
    }

    @Test
    fun testPunjabi_ekOnkar() = runTest {
        val result = RomanizationUtils.romanizePunjabi("ੴ")
        assertEquals("Ek Onkar", result)
    }

    @Test
    fun testPunjabi_emptyString() = runTest {
        val result = RomanizationUtils.romanizePunjabi("")
        assertEquals("", result)
    }

    @Test
    fun testPunjabi_nuktaFormInherentVowel() = runTest {
        val result = RomanizationUtils.romanizePunjabi("ਸ਼ਕਲ")
        assertEquals("shakal", result)
    }

    @Test
    fun testPunjabi_addakInherentVowel() = runTest {
        val result = RomanizationUtils.romanizePunjabi("ਸੱਚ")
        // Addak (ੱ) geminates the next consonant; inherent 'a' must not be dropped
        assertTrue(result.contains("sa"))
    }

    // ── Advanced Hindi/Punjabi ──

    @Test
    fun testPunjabi_tippiAndAddak() = runTest {
        assertEquals("panj", RomanizationUtils.romanizePunjabi("ਪੰਜ"))
        assertNotNull(RomanizationUtils.romanizePunjabi("ਸੱਚ"))
    }
}
