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

import com.shub39.romanization.RomanizationUtils
import kotlin.test.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RomanizationUtilsTest {

    // Japanese tests
    @Test
    fun testJapanese_basic() = runTest {
        val result = RomanizationUtils.romanizeJapanese("こんにちは")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_katakana() = runTest {
        val result = RomanizationUtils.romanizeJapanese("カフェ")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_mixed() = runTest {
        val result = RomanizationUtils.romanizeJapanese("テスト歌曲")
        assertTrue(result.isNotEmpty())
    }

    // Korean tests
    @Test
    fun testKorean_basic() = runTest {
        val result = RomanizationUtils.romanizeKorean("안녕하세요")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testKorean_hangul() = runTest {
        val result = RomanizationUtils.romanizeKorean("한글")
        assertTrue(result.isNotEmpty())
    }

    // Chinese tests
    @Test
    fun testChinese_basic() = runTest {
        val result = RomanizationUtils.romanizeChinese("你好")
        println(result)
        assertTrue(result.contains("nǐ") || result.contains("hǎo"))
    }

    @Test
    fun testChinese_mixed() = runTest {
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
        // Russian text with Russian-specific letters (Ы, Э, Ё)
        assertTrue(RomanizationUtils.isRussian("рыба"))
        assertTrue(RomanizationUtils.isRussian("эксперт"))
        assertTrue(RomanizationUtils.isRussian("ёлка"))
        // Russian text with unique letters should NOT match other Cyrillic languages
        assertFalse(RomanizationUtils.isUkrainian("рыба"))
        assertFalse(RomanizationUtils.isSerbian("рыба"))
        assertFalse(RomanizationUtils.isBulgarian("рыба"))
        assertFalse(RomanizationUtils.isBelarusian("рыба"))
        assertFalse(RomanizationUtils.isKyrgyz("рыба"))
        assertFalse(RomanizationUtils.isMacedonian("рыба"))
    }

    @Test
    fun testIsUkrainian() {
        // Ukrainian is identified by its unique letters (Ґ, Є, Ї)
        assertTrue(RomanizationUtils.isUkrainian("ґіденс"))
        assertTrue(RomanizationUtils.isUkrainian("європа"))
        assertTrue(RomanizationUtils.isUkrainian("їжак"))
        assertFalse(RomanizationUtils.isUkrainian("hello"))
        // Text without unique Ukrainian letters is not identified as Ukrainian
        assertFalse(RomanizationUtils.isUkrainian("привет"))
    }

    @Test
    fun testIsSerbian() {
        // Serbian is identified by its unique letters (Ђ, Ћ, Џ)
        assertTrue(RomanizationUtils.isSerbian("ђурђевак"))
        assertTrue(RomanizationUtils.isSerbian("ћирилица"))
        assertTrue(RomanizationUtils.isSerbian("џак"))
        assertFalse(RomanizationUtils.isSerbian("hello"))
        // Text without unique Serbian letters is not identified as Serbian
        assertFalse(RomanizationUtils.isSerbian("привет"))
    }

    @Test
    fun testIsBulgarian() {
        assertTrue(RomanizationUtils.isBulgarian("здравейте"))
        assertTrue(RomanizationUtils.isBulgarian("българия"))
        // Bulgarian text should NOT be misidentified as Russian
        assertFalse(RomanizationUtils.isRussian("здравейте"))
        assertFalse(RomanizationUtils.isRussian("българия"))
    }

    @Test
    fun testIsBelarusian() {
        // Belarusian is identified by its unique letter (Ў)
        assertTrue(RomanizationUtils.isBelarusian("ўрад"))
        assertFalse(RomanizationUtils.isBelarusian("hello"))
        // Text without unique Belarusian letters is not identified as Belarusian
        assertFalse(RomanizationUtils.isBelarusian("привет"))
    }

    @Test
    fun testIsKyrgyz() {
        // Kyrgyz is identified by its unique letters (Ң, Ү)
        assertTrue(RomanizationUtils.isKyrgyz("үч"))
        assertFalse(RomanizationUtils.isKyrgyz("hello"))
        // Text without unique Kyrgyz letters is not identified as Kyrgyz
        assertFalse(RomanizationUtils.isKyrgyz("кыргызстан"))
    }

    @Test
    fun testIsMacedonian() {
        // Macedonian is identified by its unique letters (Ѓ, Ѕ, Ќ)
        assertTrue(RomanizationUtils.isMacedonian("ѓорѓи"))
        assertTrue(RomanizationUtils.isMacedonian("ѕвезда"))
        assertTrue(RomanizationUtils.isMacedonian("ќе"))
        assertFalse(RomanizationUtils.isMacedonian("hello"))
        // Text without unique Macedonian letters is not identified as Macedonian
        assertFalse(RomanizationUtils.isMacedonian("привет"))
    }

    @Test
    fun testCyrillicDisambiguation() {
        // Serbian text with Ћ should NOT be detected as Russian
        assertFalse(RomanizationUtils.isRussian("ћирилица"))
        assertTrue(RomanizationUtils.isSerbian("ћирилица"))

        // Ukrainian text with Є should NOT be detected as Russian
        assertFalse(RomanizationUtils.isRussian("європа"))
        assertTrue(RomanizationUtils.isUkrainian("європа"))

        // Belarusian text with Ў should NOT be detected as Russian
        assertFalse(RomanizationUtils.isRussian("ўрад"))
        assertTrue(RomanizationUtils.isBelarusian("ўрад"))

        // Kyrgyz text with Ү should NOT be detected as Russian
        assertFalse(RomanizationUtils.isRussian("үч"))
        assertTrue(RomanizationUtils.isKyrgyz("үч"))

        // Macedonian text with Ќ should NOT be detected as Russian
        assertFalse(RomanizationUtils.isRussian("ќе"))
        assertTrue(RomanizationUtils.isMacedonian("ќе"))

        // Bulgarian text without Russian-specific letters should NOT be detected as Russian
        assertFalse(RomanizationUtils.isRussian("здравейте"))
        assertTrue(RomanizationUtils.isBulgarian("здравейте"))
    }

    @Test
    fun testRomanize_autoDetectJapanese() = runTest {
        val result =
            RomanizationUtils.romanize(
                "こんにちは",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectKorean() = runTest {
        val result =
            RomanizationUtils.romanize(
                "안녕하세요",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectChinese() = runTest {
        val result =
            RomanizationUtils.romanize(
                "你好",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectRussian() = runTest {
        val result =
            RomanizationUtils.romanize("рыба", enabledLanguages = listOf("Russian", "Japanese"))
        assertNotNull(result)
        assertTrue(result.contains("ryba"))
    }

    @Test
    fun testIsPunjabi() {
        assertTrue(RomanizationUtils.isPunjabi("ਸਤ ਸ੍ਰੀ ਅਕਾਲ"))
        assertTrue(RomanizationUtils.isPunjabi("ਪੰਜਾਬ"))
        assertFalse(RomanizationUtils.isPunjabi("hello"))
        assertFalse(RomanizationUtils.isPunjabi("नमस्ते"))
    }

    @Test
    fun testRomanize_autoDetectHindi() = runTest {
        val result = RomanizationUtils.romanize("नमस्ते", enabledLanguages = listOf("Hindi"))
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testRomanize_autoDetectPunjabi() = runTest {
        val result =
            RomanizationUtils.romanize("ਸਤ ਸ੍ਰੀ ਅਕਾਲ", enabledLanguages = listOf("Punjabi"))
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testRomanize_noMatch() = runTest {
        val result =
            RomanizationUtils.romanize(
                "hello world",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese", "Russian"),
            )
        assertNull(result)
    }

    @Test
    fun testRomanize_emptyString() = runTest {
        val result = RomanizationUtils.romanize("", enabledLanguages = listOf("Japanese"))
        assertNull(result)
    }

    @Test
    fun testRomanize_disabledLanguage() = runTest {
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
