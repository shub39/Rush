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
 * Korean romanization tests — Revised Romanization (RR, 2000).
 *
 * Standard: https://www.korean.go.kr/front_eng/roman/roman_01.do BGN/PCGN:
 * https://assets.publishing.service.gov.uk/media/6329b09f8fa8f53cb8a85db8/ROMANIZATION_OF_KOREAN-__MOCT_for_ROK.pdf
 * Wikipedia: https://en.wikipedia.org/wiki/Revised_Romanization_of_Korean
 */
class RomanizationKoreanTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
    }

    // Korean corner cases

    @Test
    fun testKorean_annyeonghaseyo() = runTest {
        val result = RomanizationUtils.romanizeKorean("안녕하세요")
        assertEquals("annyeonghaseyo", result)
    }

    @Test
    fun testKorean_hangeul() = runTest {
        val result = RomanizationUtils.romanizeKorean("한글")
        assertEquals("hangeul", result)
    }

    @Test
    fun testKorean_sarang() = runTest {
        val result = RomanizationUtils.romanizeKorean("사랑")
        assertEquals("sarang", result)
    }

    @Test
    fun testKorean_gamsahamnida() = runTest {
        val result = RomanizationUtils.romanizeKorean("감사합니다")
        assertEquals("gamsahamnida", result)
    }

    @Test
    fun testKorean_naneun() = runTest {
        val result = RomanizationUtils.romanizeKorean("나는")
        assertEquals("naneun", result)
    }

    @Test
    fun testKorean_mixedWithLatin() = runTest {
        val result = RomanizationUtils.romanizeKorean("OK안녕")
        assertTrue(result.contains("OK"))
        assertTrue(result.contains("annyeong"))
    }

    @Test
    fun testKorean_batchimBeforeNonHangul() = runTest {
        val result = RomanizationUtils.romanizeKorean("을 다")
        assertTrue(result.contains("eul"))
        assertTrue(result.contains("da"))
    }

    @Test
    fun testKorean_doubleConsonantBatchim() = runTest {
        val result = RomanizationUtils.romanizeKorean("삶은")
        assertEquals("salmeun", result)
    }

    @Test
    fun testKorean_llAssimilation() = runTest {
        val result = RomanizationUtils.romanizeKorean("설날")
        assertEquals("seollal", result)
    }

    @Test
    fun testKorean_tenseConsonants() = runTest {
        val result = RomanizationUtils.romanizeKorean("빵")
        assertEquals("ppang", result)
    }

    @Test
    fun testKorean_compoundVowels() = runTest {
        val result = RomanizationUtils.romanizeKorean("왜")
        assertEquals("wae", result)
    }

    @Test
    fun testKorean_euiVowel() = runTest {
        val result = RomanizationUtils.romanizeKorean("의")
        assertEquals("eui", result)
    }

    @Test
    fun testKorean_batchim_gBeforeHangul() = runTest {
        val result = RomanizationUtils.romanizeKorean("먹어")
        assertTrue(result.contains("meog"))
        assertTrue(result.contains("eo"))
    }

    @Test
    fun testKorean_batchim_hBeforeHangul() = runTest {
        val result = RomanizationUtils.romanizeKorean("좋아")
        assertEquals("joha", result)
    }

    @Test
    fun testKorean_jongNhBeforeH() = runTest {
        val result = RomanizationUtils.romanizeKorean("앉히다")
        assertEquals("anchida", result)
    }

    @Test
    fun testKorean_jongNhBeforeVowel() = runTest {
        val result = RomanizationUtils.romanizeKorean("앉아")
        assertEquals("anja", result)
    }

    @Test
    fun testKorean_punctuationPassthrough() = runTest {
        val result = RomanizationUtils.romanizeKorean("안녕!")
        assertTrue(result.contains("annyeong"))
        assertTrue(result.contains("!"))
    }

    @Test
    fun testKorean_emptyString() = runTest {
        val result = RomanizationUtils.romanizeKorean("")
        assertEquals("", result)
    }

    @Test
    fun testKorean_latinOnlyPassthrough() = runTest {
        val result = RomanizationUtils.romanizeKorean("hello")
        assertEquals("hello", result)
    }

    // ── Advanced Korean (RR) ──

    @Test
    fun testKorean_palatalization() = runTest {
        assertEquals("guji", RomanizationUtils.romanizeKorean("굳이"))
        assertEquals("gachi", RomanizationUtils.romanizeKorean("같이"))
    }

    @Test
    fun testKorean_nasalCodaLToN() = runTest {
        assertEquals("jongno", RomanizationUtils.romanizeKorean("종로"))
        assertEquals("eumnyo", RomanizationUtils.romanizeKorean("음료"))
        assertEquals("baengma", RomanizationUtils.romanizeKorean("백마"))
        assertEquals("wangsimni", RomanizationUtils.romanizeKorean("왕십리"))
    }

    @Test
    fun testKorean_diphthongs() = runTest {
        assertEquals("wa", RomanizationUtils.romanizeKorean("와"))
        assertEquals("wo", RomanizationUtils.romanizeKorean("워"))
        assertEquals("wae", RomanizationUtils.romanizeKorean("왜"))
        assertEquals("we", RomanizationUtils.romanizeKorean("웨"))
        assertEquals("oe", RomanizationUtils.romanizeKorean("외"))
        assertEquals("wi", RomanizationUtils.romanizeKorean("위"))
        assertEquals("eui", RomanizationUtils.romanizeKorean("의"))
    }
}
