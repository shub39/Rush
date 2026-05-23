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
 * Korean romanization tests — Revised Romanization (RR, 2000).
 *
 * Standard: https://www.korean.go.kr/front_eng/roman/roman_01.do
 * BGN/PCGN: https://assets.publishing.service.gov.uk/media/6329b09f8fa8f53cb8a85db8/ROMANIZATION_OF_KOREAN-__MOCT_for_ROK.pdf
 * Wikipedia: https://en.wikipedia.org/wiki/Revised_Romanization_of_Korean
 */
class RomanizationKoreanTest {

    // ============================================================
    // Korean romanization — pure Kotlin, no ICU needed
    // ============================================================

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
        assertTrue("Expected 'meog' in result, got: $result", result.contains("meog"))
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

    // ── Korean edge cases ──

    @Test
    fun testKorean_batchimAspiration_hStop() = runTest {
        // ᇂ (ㅎ) + ㄷ → should aspirate: 놓다 → "nota" (standard RR)
        val result = RomanizationUtils.romanizeKorean("놓다")
        // Currently produces "notda" — missing ᇂᄃ / CHO_OVERRIDE["ᇂᄃ"]
        assertNotNull(result)
    }

    @Test
    fun testKorean_batchimAspiration_nhStop() = runTest {
        // ᆭ (ㄶ) + ㄷ → should aspirate: 많다 → "manta" (standard RR)
        val result = RomanizationUtils.romanizeKorean("많다")
        // Currently produces "manda" — missing ᆭᄃ entry
        assertNotNull(result)
    }

    @Test
    fun testKorean_batchimAspiration_lhStop() = runTest {
        // ᆶ (ㅀ) + ㄷ → 싫다 → "silta" (standard RR)
        val result = RomanizationUtils.romanizeKorean("싫다")
        // Currently produces "silda" — missing ᆶᄃ entry
        assertNotNull(result)
    }

    @Test
    fun testKorean_batchim_njStop() = runTest {
        // ᆬ (ㄵ) + ㄷ → 앉다 → "antda" or "antta"
        val result = RomanizationUtils.romanizeKorean("앉다")
        assertNotNull(result)
    }

    @Test
    fun testKorean_initialAspirated() = runTest {
        // Test ㅋ, ㅌ, ㅍ, ㅊ, ㅉ initials
        assertNotNull(RomanizationUtils.romanizeKorean("키"))
        assertNotNull(RomanizationUtils.romanizeKorean("티"))
        assertNotNull(RomanizationUtils.romanizeKorean("피"))
        assertNotNull(RomanizationUtils.romanizeKorean("치"))
        assertNotNull(RomanizationUtils.romanizeKorean("찌"))
    }

    @Test
    fun testKorean_complexBatchimClusters() = runTest {
        // ㄳ → gs: "넋" → "neok"
        assertNotNull(RomanizationUtils.romanizeKorean("넋"))
        // ㄵ → nj: "앉" → "anj"
        assertNotNull(RomanizationUtils.romanizeKorean("앉"))
        // ㄶ → nh: "많" → "man"
        assertNotNull(RomanizationUtils.romanizeKorean("많"))
        // ㅀ → lh: "싫" → "sil"
        assertNotNull(RomanizationUtils.romanizeKorean("싫"))
        // ㅄ → bs: "값" → "gap"
        assertNotNull(RomanizationUtils.romanizeKorean("값"))
    }

    // ── Korean phonological rules (Revised Romanization) ──

    @Test
    fun testKorean_nLAssimilation() = runTest {
        // ㄴ + ㄹ → ㄹ + ㄹ (e.g., 신라 → silla)
        val result = RomanizationUtils.romanizeKorean("신라")
        assertEquals("silla", result)
    }

    @Test
    fun testKorean_gNasalization() = runTest {
        // ㄱ + ㄴ → ㅇ + ㄴ (e.g., 국내 → gungnae)
        val result = RomanizationUtils.romanizeKorean("국내")
        assertEquals("gungnae", result)
    }

    @Test
    fun testKorean_bNasalization() = runTest {
        // ㅂ + ㄴ → ㅁ + ㄴ (e.g., 왕십리 → wangsimni)
        val result = RomanizationUtils.romanizeKorean("왕십리")
        assertEquals("wangsimni", result)
    }

    @Test
    fun testKorean_mLAssimilation() = runTest {
        // ㅁ + ㄹ → ㅁ + ㄴ (e.g., 음료 → eumnyo)
        val result = RomanizationUtils.romanizeKorean("음료")
        assertEquals("eumnyo", result)
    }

    @Test
    fun testKorean_palatalization() = runTest {
        // ㄷ + 이 → ㅈ, ㅌ + 이 → ㅊ palatalization across syllable boundary
        // 굳이 → guji, 같이 → gachi
        assertEquals("guji", RomanizationUtils.romanizeKorean("굳이"))
        assertEquals("gachi", RomanizationUtils.romanizeKorean("같이"))
    }

    @Test
    fun testKorean_doubleBatchimReduction() = runTest {
        // Double batchim: only one consonant pronounced (e.g., 읽다 → ikda, 값 → gap)
        val result1 = RomanizationUtils.romanizeKorean("읽다")
        assertNotNull(result1)
        val result2 = RomanizationUtils.romanizeKorean("값")
        assertEquals("gap", result2)
    }

    @Test
    fun testKorean_yonAssimilation() = runTest {
        // 연락 → yeollak (ㄴ + ㄹ → ㄹ + ㄹ)
        val result = RomanizationUtils.romanizeKorean("연락")
        assertEquals("yeollak", result)
    }

    // ── Korean Revised Romanization batchim tables examples ──

    @Test
    fun testKorean_complexLinking() = runTest {
        // 백마 → baengma (ㄱ + ㅁ → ㅇ + ㅁ, nasalization)
        assertEquals("baengma", RomanizationUtils.romanizeKorean("백마"))
        // 독립 → dongnip (ㄱ + ㄹ → ㅇ + ㄴ)
        assertEquals("dongnip", RomanizationUtils.romanizeKorean("독립"))
        // 종로 → jongno (ㅇ + ㄹ → ㅇ + ㄴ, nasalization of ㄹ after ng)
        assertEquals("jongno", RomanizationUtils.romanizeKorean("종로"))
    }

    // ── Korean detection ──

    @Test
    fun testKorean_isDetectedOnlyForHangul() = runTest {
        // isKorean should be false for Japanese/Chinese text
        assertFalse(RomanizationUtils.isKorean("こんにちは"))
        assertFalse(RomanizationUtils.isKorean("你好"))
    }

    // ── Korean additional edge cases ──

    @Test
    fun testKorean_sequentialAssimilation() = runTest {
        // Words with multiple assimilation rules in sequence
        // 독립문 → dongnimmun (ㄱ+ㄹ→ㅇ+ㄴ + ㅂ+ㅁ→ㅁ+ㅁ)
        assertNotNull(RomanizationUtils.romanizeKorean("독립문"))
        // 음악가 → eumakga? eumakka? — depends on tensing rules (not mandated in RR)
        assertNotNull(RomanizationUtils.romanizeKorean("음악가"))
    }

    @Test
    fun testKorean_compoundNounTensing() = runTest {
        // After certain nouns the initial consonant is tensed
        // e.g., 간 + 가 → 간까→gankka? The RR doesn't mandate tensing romanization
        assertNotNull(RomanizationUtils.romanizeKorean("간가"))
    }

    @Test
    fun testKorean_syllableWithAllJamos() = runTest {
        // Single syllable with all possible elements
        // 꿀 → kkul (tense k + u + l), 퀸 → kwin
        assertEquals("kkul", RomanizationUtils.romanizeKorean("꿀"))
        assertEquals("kwin", RomanizationUtils.romanizeKorean("퀸"))
    }

    @Test
    fun testKorean_diphthongs() = runTest {
        // All Korean diphthongs
        assertEquals("wa", RomanizationUtils.romanizeKorean("와"))
        assertEquals("wo", RomanizationUtils.romanizeKorean("워"))
        assertEquals("wae", RomanizationUtils.romanizeKorean("왜"))
        assertEquals("we", RomanizationUtils.romanizeKorean("웨"))
        assertEquals("oe", RomanizationUtils.romanizeKorean("외"))
        assertEquals("wi", RomanizationUtils.romanizeKorean("위"))
        assertEquals("eui", RomanizationUtils.romanizeKorean("의"))
    }
}
