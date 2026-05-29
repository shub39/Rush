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
 * Language detection tests — covers all supported scripts.
 *
 * References: Korean — RR: https://www.korean.go.kr/front_eng/roman/roman_01.do Japanese — Hepburn:
 * https://assets.publishing.service.gov.uk/media/5ab4e1e3ed915d78b9a459de/ROMANIZATION_OF_JAPANESE_KANA.pdf
 * Chinese — ISO 7098: https://www.loc.gov/catdir/cpso/romanization/chinese.pdf Hindi/Punjabi — ISO
 * 15919: https://www.iso.org/standard/28333.html Cyrillic — BGN/PCGN:
 * https://geonames.nga.mil/geonames/GNSSearch/GNSDocs/romanization/
 */
class RomanizationDetectionTest {

    private lateinit var romanizationUtils: RomanizationUtils

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        romanizationUtils = RomanizationUtils(context)
        romanizationUtils.loadReadingDictionary(context)
    }

    // Language detection tests

    @Test
    fun testIsJapanese() {
        assertTrue(romanizationUtils.isJapanese("こんにちは"))
        assertTrue(romanizationUtils.isJapanese("カタカナ"))
        assertTrue(romanizationUtils.isJapanese("食べる"))
        assertFalse(romanizationUtils.isJapanese("hello"))
        assertFalse(romanizationUtils.isJapanese("안녕하세요"))
        assertFalse(romanizationUtils.isJapanese("你好"))
    }

    @Test
    fun testIsKorean() {
        assertTrue(romanizationUtils.isKorean("안녕하세요"))
        assertTrue(romanizationUtils.isKorean("한글"))
        assertFalse(romanizationUtils.isKorean("hello"))
        assertFalse(romanizationUtils.isKorean("こんにちは"))
    }

    @Test
    fun testIsChinese() {
        assertTrue(romanizationUtils.isChinese("你好"))
        assertTrue(romanizationUtils.isChinese("世界"))
        assertFalse(romanizationUtils.isChinese("hello"))
        assertFalse(romanizationUtils.isChinese("안녕하세요"))
    }

    @Test
    fun testIsChinese_excludesJapanese() {
        assertFalse(romanizationUtils.isChinese("こんにちは"))
        assertFalse(romanizationUtils.isChinese("食べる"))
        assertTrue(romanizationUtils.isChinese("你好"))
    }

    @Test
    fun testIsHindi() {
        assertTrue(romanizationUtils.isHindi("नमस्ते"))
        assertTrue(romanizationUtils.isHindi("हिंदी"))
        assertFalse(romanizationUtils.isHindi("hello"))
    }

    @Test
    fun testIsPunjabi() {
        assertTrue(romanizationUtils.isPunjabi("ਸਤ ਸ੍ਰੀ ਅਕਾਲ"))
        assertTrue(romanizationUtils.isPunjabi("ਪੰਜਾਬ"))
        assertFalse(romanizationUtils.isPunjabi("hello"))
        assertFalse(romanizationUtils.isPunjabi("नमस्ते"))
    }

    @Test
    fun testIsCyrillic() {
        assertTrue(romanizationUtils.isCyrillic("привет"))
        assertTrue(romanizationUtils.isCyrillic("здравствуйте"))
        assertFalse(romanizationUtils.isCyrillic("hello"))
        assertFalse(romanizationUtils.isCyrillic("こんにちは"))
    }

    @Test
    fun testIsRussian() {
        assertTrue(romanizationUtils.isRussian("рыба"))
        assertTrue(romanizationUtils.isRussian("эксперт"))
        assertTrue(romanizationUtils.isRussian("ёлка"))
        assertFalse(romanizationUtils.isUkrainian("рыба"))
        assertFalse(romanizationUtils.isSerbian("рыба"))
        assertFalse(romanizationUtils.isBulgarian("рыба"))
        assertFalse(romanizationUtils.isBelarusian("рыба"))
        assertFalse(romanizationUtils.isKyrgyz("рыба"))
        assertFalse(romanizationUtils.isMacedonian("рыба"))
    }

    @Test
    fun testIsUkrainian() {
        assertTrue(romanizationUtils.isUkrainian("ґіденс"))
        assertTrue(romanizationUtils.isUkrainian("європа"))
        assertTrue(romanizationUtils.isUkrainian("їжак"))
        assertFalse(romanizationUtils.isUkrainian("hello"))
        assertFalse(romanizationUtils.isUkrainian("привет"))
    }

    @Test
    fun testIsSerbian() {
        assertTrue(romanizationUtils.isSerbian("ђурђевак"))
        assertTrue(romanizationUtils.isSerbian("ћирилица"))
        assertTrue(romanizationUtils.isSerbian("џак"))
        assertFalse(romanizationUtils.isSerbian("hello"))
        assertFalse(romanizationUtils.isSerbian("привет"))
    }

    @Test
    fun testIsBulgarian() {
        assertTrue(romanizationUtils.isBulgarian("здравейте"))
        assertTrue(romanizationUtils.isBulgarian("българия"))
        assertFalse(romanizationUtils.isRussian("здравейте"))
        assertFalse(romanizationUtils.isRussian("българия"))
    }

    @Test
    fun testIsBelarusian() {
        assertTrue(romanizationUtils.isBelarusian("ўрад"))
        assertFalse(romanizationUtils.isBelarusian("hello"))
        assertFalse(romanizationUtils.isBelarusian("привет"))
    }

    @Test
    fun testIsKyrgyz() {
        assertTrue(romanizationUtils.isKyrgyz("үч"))
        assertFalse(romanizationUtils.isKyrgyz("hello"))
        assertFalse(romanizationUtils.isKyrgyz("кыргызстан"))
    }

    @Test
    fun testIsMacedonian() {
        assertTrue(romanizationUtils.isMacedonian("ѓорѓи"))
        assertTrue(romanizationUtils.isMacedonian("ѕвезда"))
        assertTrue(romanizationUtils.isMacedonian("ќе"))
        assertFalse(romanizationUtils.isMacedonian("hello"))
        assertFalse(romanizationUtils.isMacedonian("привет"))
    }

    @Test
    fun testCyrillicDisambiguation() {
        assertFalse(romanizationUtils.isRussian("ћирилица"))
        assertTrue(romanizationUtils.isSerbian("ћирилица"))
        assertFalse(romanizationUtils.isRussian("європа"))
        assertTrue(romanizationUtils.isUkrainian("європа"))
        assertFalse(romanizationUtils.isRussian("ўрад"))
        assertTrue(romanizationUtils.isBelarusian("ўрад"))
        assertFalse(romanizationUtils.isRussian("үч"))
        assertTrue(romanizationUtils.isKyrgyz("үч"))
        assertFalse(romanizationUtils.isRussian("ќе"))
        assertTrue(romanizationUtils.isMacedonian("ќе"))
        assertFalse(romanizationUtils.isRussian("здравейте"))
        assertTrue(romanizationUtils.isBulgarian("здравейте"))
    }

    @Test
    fun testIsJapanese_emptyString() {
        assertFalse(romanizationUtils.isJapanese(""))
    }

    @Test
    fun testIsKorean_emptyString() {
        assertFalse(romanizationUtils.isKorean(""))
    }

    @Test
    fun testIsChinese_emptyString() {
        assertFalse(romanizationUtils.isChinese(""))
    }

    @Test
    fun testIsHindi_emptyString() {
        assertFalse(romanizationUtils.isHindi(""))
    }

    @Test
    fun testIsPunjabi_emptyString() {
        assertFalse(romanizationUtils.isPunjabi(""))
    }

    @Test
    fun testIsCyrillic_emptyString() {
        assertFalse(romanizationUtils.isCyrillic(""))
    }

    @Test
    fun testIsRussian_emptyString() {
        assertFalse(romanizationUtils.isRussian(""))
    }

    @Test
    fun testIsRussian_noSpecificLetters() {
        assertFalse(romanizationUtils.isRussian("привет"))
    }

    // ── Auto-detect ──

    @Test
    fun testKorean_isDetectedOnlyForHangul() = runTest {
        assertFalse(romanizationUtils.isKorean("こんにちは"))
        assertFalse(romanizationUtils.isKorean("你好"))
    }

    @Test
    fun testJapanese_isDetectedForKanaOnly() = runTest {
        assertTrue(romanizationUtils.isJapanese("こんにちは"))
        assertFalse(romanizationUtils.isJapanese("世界"))
    }
}
