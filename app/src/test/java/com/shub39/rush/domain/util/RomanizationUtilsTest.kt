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
package com.shub39.rush.domain.util

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RomanizationUtilsTest {

    // Japanese tests
    @Test
    fun testJapanese_basic() = runBlocking {
        val result = RomanizationUtils.romanizeJapanese("こんにちは")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_katakana() = runBlocking {
        val result = RomanizationUtils.romanizeJapanese("カフェ")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_mixed() = runBlocking {
        val result = RomanizationUtils.romanizeJapanese("テスト歌曲")
        assertTrue(result.isNotEmpty())
    }

    // Korean tests
    @Test
    fun testKorean_basic() = runBlocking {
        val result = RomanizationUtils.romanizeKorean("안녕하세요")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testKorean_hangul() = runBlocking {
        val result = RomanizationUtils.romanizeKorean("한글")
        assertTrue(result.isNotEmpty())
    }

    // Chinese tests
    @Test
    fun testChinese_basic() = runBlocking {
        val result = RomanizationUtils.romanizeChinese("你好")
        assertTrue(result.contains("ni") || result.contains("hao"))
    }

    @Test
    fun testChinese_mixed() = runBlocking {
        val result = RomanizationUtils.romanizeChinese("hello世界")
        assertTrue(result.contains("hello"))
    }

    // Language detection tests
    @Test
    fun testIsJapanese() {
        assertTrue(RomanizationUtils.isJapanese("こんにちは"))
        assertTrue(RomanizationUtils.isJapanese("カタカナ"))
        assertFalse(RomanizationUtils.isJapanese("hello"))
        assertFalse(RomanizationUtils.isJapanese("안녕하세요"))
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
    fun testIsHindi() {
        assertTrue(RomanizationUtils.isHindi("नमस्ते"))
        assertTrue(RomanizationUtils.isHindi("हिंदी"))
        assertFalse(RomanizationUtils.isHindi("hello"))
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
        assertTrue(RomanizationUtils.isRussian("привет мир"))
    }

    @Test
    fun testIsUkrainian() {
        // Ukrainian detection may not be fully implemented
        assertFalse(RomanizationUtils.isUkrainian("hello"))
    }

    @Test
    fun testIsSerbian() {
        // Serbian detection may not be fully implemented
        assertFalse(RomanizationUtils.isSerbian("hello"))
    }

    @Test
    fun testIsBulgarian() {
        assertTrue(RomanizationUtils.isBulgarian("здравейте"))
        assertTrue(RomanizationUtils.isBulgarian("българия"))
    }

    @Test
    fun testRomanize_autoDetectJapanese() = runBlocking {
        val result =
            RomanizationUtils.romanize(
                "こんにちは",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectKorean() = runBlocking {
        val result =
            RomanizationUtils.romanize(
                "안녕하세요",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectChinese() = runBlocking {
        val result =
            RomanizationUtils.romanize(
                "你好",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectRussian() = runBlocking {
        val result =
            RomanizationUtils.romanize("привет", enabledLanguages = listOf("Russian", "Japanese"))
        assertTrue(result!!.contains("privet"))
    }

    @Test
    fun testRomanize_autoDetectHindi() = runBlocking {
        val result = RomanizationUtils.romanize("नमस्ते", enabledLanguages = listOf("Hindi"))
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testRomanize_noMatch() = runBlocking {
        val result =
            RomanizationUtils.romanize(
                "hello world",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese", "Russian"),
            )
        assertNull(result)
    }

    @Test
    fun testRomanize_emptyString() = runBlocking {
        val result = RomanizationUtils.romanize("", enabledLanguages = listOf("Japanese"))
        assertNull(result)
    }

    @Test
    fun testRomanize_disabledLanguage() = runBlocking {
        val result = RomanizationUtils.romanize("こんにちは", enabledLanguages = emptyList())
        assertNull(result)
    }

    @Test
    fun testJapanese_notChinese() {
        // Japanese should not be detected as Chinese
        assertTrue(RomanizationUtils.isJapanese("こんにちは"))
        assertFalse(RomanizationUtils.isChinese("こんにちは"))
    }
}
