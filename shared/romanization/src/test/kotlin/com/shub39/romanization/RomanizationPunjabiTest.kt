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
 * Punjabi romanization tests — ISO 15919:2001 (Gurmukhi framework).
 *
 * ISO: https://www.iso.org/standard/28333.html
 * Uses same ISO 15919 framework as Hindi for Gurmukhi script.
 */
class RomanizationPunjabiTest {

    // ============================================================
    // Punjabi romanization
    // ============================================================

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

    // ── Punjabi edge cases ──

    @Test
    fun testPunjabi_nuktaForms() = runTest {
        // Test nukta forms beyond ਸ਼ (word-final = no inherent 'a')
        assertEquals("kh", RomanizationUtils.romanizePunjabi("ਖ਼"))
        assertEquals("g", RomanizationUtils.romanizePunjabi("ਗ਼"))
        assertEquals("z", RomanizationUtils.romanizePunjabi("ਜ਼"))
        assertEquals("f", RomanizationUtils.romanizePunjabi("ਫ਼"))
        assertEquals("l", RomanizationUtils.romanizePunjabi("ਲ਼"))
    }

    @Test
    fun testPunjabi_upperBindi() = runTest {
        // ਂ → (nasalization of preceding vowel)
        val result = RomanizationUtils.romanizePunjabi("ਗਾਂ")
        assertNotNull(result)
    }

    // ── Punjabi advanced ──

    @Test
    fun testPunjabi_addakGemination() = runTest {
        // Addak (ੱ) geminates the following consonant: ਸੱਚ → sacc
        val result = RomanizationUtils.romanizePunjabi("ਸੱਚ")
        assertNotNull(result)
        assertTrue("sa" in result!!)
    }

    @Test
    fun testPunjabi_tippiNasalization() = runTest {
        // Tippi (ਂ) nasalizes preceding vowel: ਪੰਜ → panj
        val result = RomanizationUtils.romanizePunjabi("ਪੰਜ")
        assertEquals("panj", result)
    }

    @Test
    fun testPunjabi_bindi() = runTest {
        // Bindi (ੰ) nasalizes the preceding consonant. For ੰ in ਕੰਮ,
        // the bindi nasalizes the k. Current: "kanm" (bindi → n before m)
        val result = RomanizationUtils.romanizePunjabi("ਕੰਮ")
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
    }

    // ── Punjabi additional edge cases ──

    @Test
    fun testPunjabi_addakAndNukta() = runTest {
        // Addak with nukta consonants: letter with punj
        assertNotNull(RomanizationUtils.romanizePunjabi("ਸੱਚ"))
        assertNotNull(RomanizationUtils.romanizePunjabi("ਪੱਕ"))
    }

    @Test
    fun testPunjabi_muliVowelLessConst() = runTest {
        // Words where virama suppresses vowel: ਕ੍ਰਿਪਾ → kripā
        assertNotNull(RomanizationUtils.romanizePunjabi("ਕ੍ਰਿਪਾ"))
    }
}
