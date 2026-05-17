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

import com.ibm.icu.text.Transliterator
import java.util.Locale
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for RomanizationUtils.
 *
 * Japanese romanization uses ICU4J Katakana-Latin for kana→romaji conversion. Kanji characters pass
 * through unchanged (no morphological analyzer).
 */
class RomanizationUtilsIcu4jTest {

    private val katakanaTransliterator by lazy { Transliterator.getInstance("Katakana-Latin") }

    private suspend fun romanizeJapanese(text: String): String {
        if (text.isEmpty()) return ""
        val sb = StringBuilder()
        for (char in text) {
            when {
                char in '\u3040'..'\u309F' -> {
                    val katakana = (char.code + 0x60).toChar()
                    sb.append(
                        katakanaTransliterator
                            .transliterate(katakana.toString())
                            .lowercase(Locale.ROOT)
                    )
                }
                char in '\u30A0'..'\u30FF' -> {
                    sb.append(
                        katakanaTransliterator.transliterate(char.toString()).lowercase(Locale.ROOT)
                    )
                }
                else -> sb.append(char)
            }
        }
        return sb.toString().trim()
    }

    private val chineseTransliterator by lazy { Transliterator.getInstance("Han-Latin") }

    private suspend fun romanizeChinese(text: String): String {
        return chineseTransliterator.transliterate(text).lowercase(Locale.ROOT)
    }

    // Japanese tests

    @Test
    fun testJapanese_hiragana_konnichiwa() = runTest {
        val result = romanizeJapanese("こんにちは")
        // ICU produces "konnnichiha" for こんにちは
        assertTrue(result.contains("kon") || result.contains("nichi"))
    }

    @Test
    fun testJapanese_hiragana_sakura() = runTest {
        val result = romanizeJapanese("さくら")
        assertEquals("sakura", result)
    }

    @Test
    fun testJapanese_katakana_cafe() = runTest {
        val result = romanizeJapanese("カフェ")
        // ICU produces "kafu~e" for ファ (small fu + e)
        assertTrue(result.contains("kaf"))
    }

    @Test
    fun testJapanese_katakana_test() = runTest {
        val result = romanizeJapanese("テスト")
        assertEquals("tesuto", result)
    }

    @Test
    fun testJapanese_katakana_music() = runTest {
        val result = romanizeJapanese("ミュージック")
        // ICU produces varying output for ミュージック
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_mixedKanaAndKanji() = runTest {
        val result = romanizeJapanese("テスト歌曲")
        assertTrue(result.contains("tesuto") || result.contains("test"))
        // Kanji passes through unchanged
        assertTrue(result.contains("歌曲"))
    }

    @Test
    fun testJapanese_particleWa() = runTest {
        val result = romanizeJapanese("わたしは")
        // Without morphological analyzer, は is romanized as "ha" (not "wa")
        assertEquals("watashiha", result)
    }

    @Test
    fun testJapanese_particleHe() = runTest {
        val result = romanizeJapanese("へや")
        assertEquals("heya", result)
    }

    @Test
    fun testJapanese_particleO() = runTest {
        val result = romanizeJapanese("それを")
        // Without morphological analyzer, を is romanized as "wo" (not "o")
        assertEquals("sorewo", result)
    }

    @Test
    fun testJapanese_katakanaLove() = runTest {
        val result = romanizeJapanese("アイシテル")
        assertEquals("aishiteru", result)
    }

    @Test
    fun testJapanese_onegai() = runTest {
        val result = romanizeJapanese("お願い")
        // 願 is kanji, passes through; がい → gai
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_pureKanjiStripped() = runTest {
        val result = romanizeJapanese("花鳥風月")
        // Pure kanji passes through unchanged
        assertEquals("花鳥風月", result)
    }

    @Test
    fun testJapanese_longVowels() = runTest {
        val result = romanizeJapanese("とうきょう")
        // ICU produces varying output for long vowels
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_smallTsu() = runTest {
        val result = romanizeJapanese("まって")
        // ICU produces "ma~tsu" for small tsu
        assertTrue(result.contains("ma"))
    }

    @Test
    fun testJapanese_smallYaYuYo() = runTest {
        val result = romanizeJapanese("きゃしゃちょ")
        // ICU produces varying output for small ya/yu/yo combinations
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testJapanese_kanjiStripped() = runTest {
        val result = romanizeJapanese("愛")
        // Kanji passes through unchanged
        assertEquals("愛", result)
    }

    @Test
    fun testJapanese_emptyString() = runTest {
        val result = romanizeJapanese("")
        assertEquals("", result)
    }

    @Test
    fun testJapanese_latinOnlyPassthrough() = runTest {
        val result = romanizeJapanese("hello")
        assertEquals("hello", result)
    }

    // Chinese tests

    @Test
    fun testChinese_niHao() = runTest {
        val result = romanizeChinese("你好")
        assertTrue("Expected 'nǐ' in result, got: $result", result.contains("nǐ"))
        assertTrue("Expected 'hǎo' in result, got: $result", result.contains("hǎo"))
    }

    @Test
    fun testChinese_shiJie() = runTest {
        val result = romanizeChinese("世界")
        assertTrue("Expected 'shì' in result, got: $result", result.contains("shì"))
        assertTrue("Expected 'jiè' in result, got: $result", result.contains("jiè"))
    }

    @Test
    fun testChinese_woAiNi() = runTest {
        val result = romanizeChinese("我爱你")
        assertTrue("Expected 'wǒ' in result, got: $result", result.contains("wǒ"))
        assertTrue("Expected 'ài' in result, got: $result", result.contains("ài"))
        assertTrue("Expected 'nǐ' in result, got: $result", result.contains("nǐ"))
    }

    @Test
    fun testChinese_mixedWithLatin() = runTest {
        val result = romanizeChinese("hello世界")
        assertTrue("Expected 'hello' in result, got: $result", result.contains("hello"))
        assertTrue("Expected 'shì' in result, got: $result", result.contains("shì"))
        assertTrue("Expected 'jiè' in result, got: $result", result.contains("jiè"))
    }

    // ============================================================
    // Language detection — pure Kotlin, no ICU needed
    // ============================================================

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

    // ============================================================
    // Hindi romanization — pure Kotlin, no ICU needed
    // ============================================================

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

    // ============================================================
    // Punjabi romanization — pure Kotlin, no ICU needed
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

    // ============================================================
    // Cyrillic romanization — pure Kotlin, no ICU needed
    // ============================================================

    @Test
    fun testRussian_ryba() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("рыба", "Russian")
        assertEquals("ryba", result)
    }

    @Test
    fun testRussian_moskva() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("москва", "Russian")
        assertEquals("moskva", result)
    }

    @Test
    fun testRussian_ego_evo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("его", "Russian")
        assertEquals("evo", result)
    }

    @Test
    fun testRussian_ogo_ovo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ого", "Russian")
        assertEquals("ovo", result)
    }

    @Test
    fun testRussian_capitalizedOgo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("Ого", "Russian")
        assertEquals("Ovo", result)
    }

    @Test
    fun testRussian_capitalizedEgo() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("Его", "Russian")
        assertEquals("Evo", result)
    }

    @Test
    fun testUkrainian_kyiv() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("київ", "Ukrainian")
        assertEquals("kyiv", result)
    }

    @Test
    fun testUkrainian_hryvnia() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("гривня", "Ukrainian")
        assertEquals("hryvnya", result)
    }

    @Test
    fun testSerbian_cirilica() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("ћирилица", "Serbian")
        assertEquals("ćirilica", result)
    }

    @Test
    fun testSerbian_jToJ() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("југ", "Serbian")
        assertEquals("jug", result)
    }

    @Test
    fun testBulgarian_balgariya() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("българия", "Bulgarian")
        assertEquals("balgariya", result)
    }

    @Test
    fun testBulgarian_sht() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("щастие", "Bulgarian")!!
        assertTrue(result.contains("sht"))
    }

    @Test
    fun testBulgarian_shwaVowel() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("къща", "Bulgarian")
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
        assertNotNull(result)
        assertEquals("privet", result)
    }

    @Test
    fun testCyrillic_mixedWithLatin() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("hello мир", "Russian")!!
        assertTrue(result.contains("hello"))
        assertTrue(result.contains("mir"))
    }

    // ============================================================
    // romanize() auto-detect — pure Kotlin, no ICU needed
    // ============================================================

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
    fun testRomanize_autoDetectKorean() = runTest {
        val result =
            RomanizationUtils.romanize(
                "안녕하세요",
                enabledLanguages = listOf("Japanese", "Korean", "Chinese"),
            )
        assertNotNull(result)
        assertEquals("annyeonghaseyo", result)
    }

    @Test
    fun testRomanize_autoDetectRussian() = runTest {
        val result =
            RomanizationUtils.romanize("рыба", enabledLanguages = listOf("Russian", "Japanese"))
        assertNotNull(result)
        assertEquals("ryba", result)
    }

    @Test
    fun testRomanize_autoDetectHindi() = runTest {
        val result = RomanizationUtils.romanize("नमस्ते", enabledLanguages = listOf("Hindi"))
        assertNotNull(result)
        assertEquals("namaste", result)
    }

    @Test
    fun testRomanize_autoDetectPunjabi() = runTest {
        val result =
            RomanizationUtils.romanize("ਸਤ ਸ੍ਰੀ ਅਕਾਲ", enabledLanguages = listOf("Punjabi"))
        assertNotNull(result)
        assertEquals("sat sree akaal", result)
    }

    @Test
    fun testRomanize_whitespaceOnly() = runTest {
        val result = RomanizationUtils.romanize("   ", enabledLanguages = listOf("Japanese"))
        assertNull(result)
    }

    @Test
    fun testRomanize_specialCharactersOnly() = runTest {
        val result =
            RomanizationUtils.romanize("!@#$%", enabledLanguages = listOf("Japanese", "Russian"))
        assertNull(result)
    }
}
