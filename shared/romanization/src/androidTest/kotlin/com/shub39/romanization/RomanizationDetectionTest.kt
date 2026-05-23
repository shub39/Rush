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
 * Language detection tests — covers all supported scripts.
 *
 * References: Korean — RR: https://www.korean.go.kr/front_eng/roman/roman_01.do Japanese — Hepburn:
 * https://assets.publishing.service.gov.uk/media/5ab4e1e3ed915d78b9a459de/ROMANIZATION_OF_JAPANESE_KANA.pdf
 * Chinese — ISO 7098: https://www.loc.gov/catdir/cpso/romanization/chinese.pdf Hindi/Punjabi — ISO
 * 15919: https://www.iso.org/standard/28333.html Cyrillic — BGN/PCGN:
 * https://geonames.nga.mil/geonames/GNSSearch/GNSDocs/romanization/
 */
class RomanizationDetectionTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
    }

    // Language detection tests

    @Test
    fun testIsJapanese() {
        assertTrue(RomanizationUtils.isJapanese("こんにちは"))
        assertTrue(RomanizationUtils.isJapanese("カタカナ"))
        assertTrue(RomanizationUtils.isJapanese("食べる"))
        assertFalse(RomanizationUtils.isJapanese("hello"))
        assertFalse(RomanizationUtils.isJapanese("안녕하세요"))
        assertFalse(RomanizationUtils.isJapanese("你好"))
    }

    @Test
    fun testIsKorean() {
        assertTrue(RomanizationUtils.isKorean("안녕하세요"))
        assertTrue(RomanizationUtils.isKorean("한글"))
        assertFalse(RomanizationUtils.isKorean("hello"))
        assertFalse(RomanizationUtils.isKorean("こんにちは"))
    }

    @Test
    fun testIsChinese() {
        assertTrue(RomanizationUtils.isChinese("你好"))
        assertTrue(RomanizationUtils.isChinese("世界"))
        assertFalse(RomanizationUtils.isChinese("hello"))
        assertFalse(RomanizationUtils.isChinese("안녕하세요"))
    }

    @Test
    fun testIsChinese_excludesJapanese() {
        assertFalse(RomanizationUtils.isChinese("こんにちは"))
        assertFalse(RomanizationUtils.isChinese("食べる"))
        assertTrue(RomanizationUtils.isChinese("你好"))
    }

    @Test
    fun testIsHindi() {
        assertTrue(RomanizationUtils.isHindi("नमस्ते"))
        assertTrue(RomanizationUtils.isHindi("हिंदी"))
        assertFalse(RomanizationUtils.isHindi("hello"))
    }

    @Test
    fun testIsPunjabi() {
        assertTrue(RomanizationUtils.isPunjabi("ਸਤ ਸ੍ਰੀ ਅਕਾਲ"))
        assertTrue(RomanizationUtils.isPunjabi("ਪੰਜਾਬ"))
        assertFalse(RomanizationUtils.isPunjabi("hello"))
        assertFalse(RomanizationUtils.isPunjabi("नमस्ते"))
    }

    @Test
    fun testIsCyrillic() {
        assertTrue(RomanizationUtils.isCyrillic("привет"))
        assertTrue(RomanizationUtils.isCyrillic("здравствуйте"))
        assertFalse(RomanizationUtils.isCyrillic("hello"))
        assertFalse(RomanizationUtils.isCyrillic("こんにちは"))
    }

    @Test
    fun testIsRussian() {
        assertTrue(RomanizationUtils.isRussian("рыба"))
        assertTrue(RomanizationUtils.isRussian("эксперт"))
        assertTrue(RomanizationUtils.isRussian("ёлка"))
        assertFalse(RomanizationUtils.isUkrainian("рыба"))
        assertFalse(RomanizationUtils.isSerbian("рыба"))
        assertFalse(RomanizationUtils.isBulgarian("рыба"))
        assertFalse(RomanizationUtils.isBelarusian("рыба"))
        assertFalse(RomanizationUtils.isKyrgyz("рыба"))
        assertFalse(RomanizationUtils.isMacedonian("рыба"))
    }

    @Test
    fun testIsUkrainian() {
        assertTrue(RomanizationUtils.isUkrainian("ґіденс"))
        assertTrue(RomanizationUtils.isUkrainian("європа"))
        assertTrue(RomanizationUtils.isUkrainian("їжак"))
        assertFalse(RomanizationUtils.isUkrainian("hello"))
        assertFalse(RomanizationUtils.isUkrainian("привет"))
    }

    @Test
    fun testIsSerbian() {
        assertTrue(RomanizationUtils.isSerbian("ђурђевак"))
        assertTrue(RomanizationUtils.isSerbian("ћирилица"))
        assertTrue(RomanizationUtils.isSerbian("џак"))
        assertFalse(RomanizationUtils.isSerbian("hello"))
        assertFalse(RomanizationUtils.isSerbian("привет"))
    }

    @Test
    fun testIsBulgarian() {
        assertTrue(RomanizationUtils.isBulgarian("здравейте"))
        assertTrue(RomanizationUtils.isBulgarian("българия"))
        assertFalse(RomanizationUtils.isRussian("здравейте"))
        assertFalse(RomanizationUtils.isRussian("българия"))
    }

    @Test
    fun testIsBelarusian() {
        assertTrue(RomanizationUtils.isBelarusian("ўрад"))
        assertFalse(RomanizationUtils.isBelarusian("hello"))
        assertFalse(RomanizationUtils.isBelarusian("привет"))
    }

    @Test
    fun testIsKyrgyz() {
        assertTrue(RomanizationUtils.isKyrgyz("үч"))
        assertFalse(RomanizationUtils.isKyrgyz("hello"))
        assertFalse(RomanizationUtils.isKyrgyz("кыргызстан"))
    }

    @Test
    fun testIsMacedonian() {
        assertTrue(RomanizationUtils.isMacedonian("ѓорѓи"))
        assertTrue(RomanizationUtils.isMacedonian("ѕвезда"))
        assertTrue(RomanizationUtils.isMacedonian("ќе"))
        assertFalse(RomanizationUtils.isMacedonian("hello"))
        assertFalse(RomanizationUtils.isMacedonian("привет"))
    }

    @Test
    fun testCyrillicDisambiguation() {
        assertFalse(RomanizationUtils.isRussian("ћирилица"))
        assertTrue(RomanizationUtils.isSerbian("ћирилица"))
        assertFalse(RomanizationUtils.isRussian("європа"))
        assertTrue(RomanizationUtils.isUkrainian("європа"))
        assertFalse(RomanizationUtils.isRussian("ўрад"))
        assertTrue(RomanizationUtils.isBelarusian("ўрад"))
        assertFalse(RomanizationUtils.isRussian("үч"))
        assertTrue(RomanizationUtils.isKyrgyz("үч"))
        assertFalse(RomanizationUtils.isRussian("ќе"))
        assertTrue(RomanizationUtils.isMacedonian("ќе"))
        assertFalse(RomanizationUtils.isRussian("здравейте"))
        assertTrue(RomanizationUtils.isBulgarian("здравейте"))
    }

    @Test
    fun testIsJapanese_emptyString() {
        assertFalse(RomanizationUtils.isJapanese(""))
    }

    @Test
    fun testIsKorean_emptyString() {
        assertFalse(RomanizationUtils.isKorean(""))
    }

    @Test
    fun testIsChinese_emptyString() {
        assertFalse(RomanizationUtils.isChinese(""))
    }

    @Test
    fun testIsHindi_emptyString() {
        assertFalse(RomanizationUtils.isHindi(""))
    }

    @Test
    fun testIsPunjabi_emptyString() {
        assertFalse(RomanizationUtils.isPunjabi(""))
    }

    @Test
    fun testIsCyrillic_emptyString() {
        assertFalse(RomanizationUtils.isCyrillic(""))
    }

    @Test
    fun testIsRussian_emptyString() {
        assertFalse(RomanizationUtils.isRussian(""))
    }

    @Test
    fun testIsRussian_noSpecificLetters() {
        assertFalse(RomanizationUtils.isRussian("привет"))
    }

    // ── Auto-detect ──

    @Test
    fun testKorean_isDetectedOnlyForHangul() = runTest {
        assertFalse(RomanizationUtils.isKorean("こんにちは"))
        assertFalse(RomanizationUtils.isKorean("你好"))
    }

    @Test
    fun testJapanese_isDetectedForKanaOnly() = runTest {
        assertTrue(RomanizationUtils.isJapanese("こんにちは"))
        assertFalse(RomanizationUtils.isJapanese("世界"))
    }
}
