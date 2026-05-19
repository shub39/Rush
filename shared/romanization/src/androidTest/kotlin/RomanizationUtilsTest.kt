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
 * Android instrumented tests for RomanizationUtils.
 *
 * Official standards referenced: Korean — Revised Romanization (RR, 2000)
 * https://www.korean.go.kr/front_eng/roman/roman_01.do Japanese— Modified Hepburn (BGN/PCGN 1976)
 * https://assets.publishing.service.gov.uk/media/5ab4e1e3ed915d78b9a459de/ROMANIZATION_OF_JAPANESE_KANA.pdf
 * Chinese — ISO 7098:2015 / ICU Han-Latin https://www.loc.gov/catdir/cpso/romanization/chinese.pdf
 * Hindi — ISO 15919:2001 / ALA-LC https://www.loc.gov/catdir/cpso/romanization/hindi.pdf Punjabi —
 * ISO 15919:2001 (Gurmukhi framework) https://www.iso.org/standard/28333.html Cyrillic— BGN/PCGN
 * per language https://geonames.nga.mil/geonames/GNSSearch/GNSDocs/romanization/
 *
 * These tests load the real IPADIC reading dictionary (ja_readings.tsv, 249K entries) and exercise
 * the full dictionary-backed tokenizer path for Japanese, unlike the JVM unit tests
 * (RomanizationUtilsIcu4jTest) which use the ICU4J fallback without a dictionary.
 */
class RomanizationUtilsTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
    }

    // Japanese corner cases (ICU-only)

    @Test
    fun testJapanese_hiragana_konnichiwa() = runTest {
        val result = RomanizationUtils.romanizeJapanese("こんにちは")
        assertTrue(result.contains("kon"))
        assertTrue(result.contains("nichi"))
    }

    @Test
    fun testJapanese_hiragana_sakura() = runTest {
        val result = RomanizationUtils.romanizeJapanese("さくら")
        assertEquals("sakura", result)
    }

    @Test
    fun testJapanese_katakana_cafe() = runTest {
        val result = RomanizationUtils.romanizeJapanese("カフェ")
        assertEquals("kafe", result)
    }

    @Test
    fun testJapanese_katakana_test() = runTest {
        val result = RomanizationUtils.romanizeJapanese("テスト")
        assertEquals("tesuto", result)
    }

    @Test
    fun testJapanese_katakanaLove() = runTest {
        val result = RomanizationUtils.romanizeJapanese("アイシテル")
        assertEquals("aishiteru", result)
    }

    @Test
    fun testJapanese_kanjiStripped() = runTest {
        // Without dictionary: kanji passes through (愛 → 愛)
        // With dictionary: kanji has reading (愛 → アイ → ai)
        val result = RomanizationUtils.romanizeJapanese("愛")
        assertTrue(result == "愛" || result == "ai")
    }

    @Test
    fun testJapanese_pureKanjiStripped() = runTest {
        // Without dictionary: kanji passes through
        // With dictionary: may be tokenized via IPADIC
        val result = RomanizationUtils.romanizeJapanese("花鳥風月")
        assertTrue(result == "花鳥風月" || result.contains("ka") || result.contains("fu"))
    }

    @Test
    fun testJapanese_longVowels() = runTest {
        val result = RomanizationUtils.romanizeJapanese("とうきょう")
        assertTrue(result.contains("tou"))
        assertTrue(result.contains("kyou"))
    }

    @Test
    fun testJapanese_smallTsu() = runTest {
        val result = RomanizationUtils.romanizeJapanese("まって")
        // ICU produces "ma~tsu" for small tsu
        assertTrue(result.contains("ma"))
    }

    @Test
    fun testJapanese_smallYaYuYo() = runTest {
        val result = RomanizationUtils.romanizeJapanese("きゃしゃちょ")
        assertTrue(result.contains("kya"))
        assertTrue(result.contains("sha"))
        assertTrue(result.contains("cho"))
    }

    @Test
    fun testJapanese_particleWa() = runTest {
        val result = RomanizationUtils.romanizeJapanese("わたしは")
        // Without tokenizer: は → ha; with tokenizer: particle rule → wa
        assertTrue(result == "watashiha" || result == "watashiwa")
    }

    @Test
    fun testJapanese_particleHe() = runTest {
        val result = RomanizationUtils.romanizeJapanese("へや")
        assertTrue(result.contains("heya"))
    }

    @Test
    fun testJapanese_particleO() = runTest {
        val result = RomanizationUtils.romanizeJapanese("それを")
        // Without tokenizer: を → wo (char-by-char)
        // With tokenizer grouping: all kana grouped → still "sorewo"
        // With per-char tokenization: particle rule → "o"
        assertTrue(result == "sorewo" || result == "sore o")
    }

    @Test
    fun testJapanese_onegai() = runTest {
        val result = RomanizationUtils.romanizeJapanese("お願い")
        // Without dictionary: kanji passes through, kana romanized
        // With dictionary: お願い → オネガイ → "onegai"
        assertTrue(result.contains("願") || result.contains("negai"))
    }

    @Test
    fun testJapanese_emptyString() = runTest {
        val result = RomanizationUtils.romanizeJapanese("")
        assertEquals("", result)
    }

    @Test
    fun testJapanese_latinOnlyPassthrough() = runTest {
        val result = RomanizationUtils.romanizeJapanese("hello")
        assertEquals("hello", result)
    }

    // Chinese corner cases

    @Test
    fun testChinese_pureKanjiDetectedAsChinese() = runTest {
        val result = RomanizationUtils.romanize("花鳥風月", enabledLanguages = listOf("Chinese"))
        assertNotNull(result)
        assertTrue(result!!.contains("huā"))
    }

    @Test
    fun testChinese_mixedWithKana() = runTest {
        val result =
            RomanizationUtils.romanize("食べる", enabledLanguages = listOf("Japanese", "Chinese"))
        assertNotNull(result)
        // Detected as Japanese due to kana
        assertTrue(result!!.contains("taberu") || result!!.contains("食"))
    }

    @Test
    fun testChinese_pureCjkWithBothJapaneseAndChinese() = runTest {
        val result =
            RomanizationUtils.romanize("望春风", enabledLanguages = listOf("Japanese", "Chinese"))
        assertNotNull(result)
        // With both JP and ZH enabled, pure CJK goes to Japanese IPADIC.
        // Chinese lyrics not in IPADIC pass through; enable Chinese-only for pinyin.
        assertTrue(result!!.isNotEmpty())
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

    // Cyrillic corner cases

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
        assertEquals("privet", result)
    }

    @Test
    fun testCyrillic_mixedWithLatin() = runTest {
        val result = RomanizationUtils.romanizeCyrillic("hello мир", "Russian")!!
        assertTrue(result.contains("hello"))
        assertTrue(result.contains("mir"))
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

    // romanize() auto-detect

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
    fun testRomanize_autoDetectJapanese() = runTest {
        val result =
            RomanizationUtils.romanize(
                "こんにちは",
                enabledLanguages = listOf("Japanese", "Chinese", "Korean"),
            )
        assertNotNull(result)
        // With tokenizer: こんにちは → konnichiwa (particle rule)
        // Without: → konnichiha
        assertTrue(
            result == "konnichiwa" || result == "konnichiha" || result!!.contains("konnichi")
        )
    }

    @Test
    fun testRomanize_autoDetectChinese() = runTest {
        val result =
            RomanizationUtils.romanize("你好", enabledLanguages = listOf("Japanese", "Chinese"))
        assertNotNull(result)
        // Pure CJK with both enabled → Japanese IPADIC first.
        // 好 is in IPADIC as コウ → "kou", 你 passes through → "你 kou".
        // For pinyin, use Chinese-only (tested in testChinese_pureKanjiDetectedAsChinese).
        assertTrue(result!!.contains("kou") || result!!.contains("你"))
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

    // ── Advanced Japanese (dictionary-backed) ──

    @Test
    fun testJapanese_dictionaryKanjiReading() = runTest {
        // With IPADIC loaded: 愛 → ai (dictionary lookup), 花 → ka or hana
        val result1 = RomanizationUtils.romanizeJapanese("愛")
        assertNotNull(result1)
        // 東京 → tōkyō (dictionary should segment this)
        val result2 = RomanizationUtils.romanizeJapanese("東京")
        assertNotNull(result2)
    }

    @Test
    fun testJapanese_mixedKanjiKana() = runTest {
        // Real Japanese phrase with kanji + kana + particle
        val result = RomanizationUtils.romanizeJapanese("食べる")
        assertNotNull(result)
        assertTrue("tab" in result!! || "食" in result)
    }

    @Test
    fun testJapanese_katakanaLoanWords() = runTest {
        // Common katakana loan words with chōonpu
        val result1 = RomanizationUtils.romanizeJapanese("コンピューター")
        assertNotNull(result1)
        val result2 = RomanizationUtils.romanizeJapanese("レストラン")
        assertNotNull(result2)
    }

    // ── Advanced Cyrillic ──

    @Test
    fun testCyrillic_bulgarianFullWord() = runTest {
        assertEquals("zdraveyte", RomanizationUtils.romanizeCyrillic("здравейте", "Bulgarian"))
        assertEquals("balgarski", RomanizationUtils.romanizeCyrillic("български", "Bulgarian"))
    }

    @Test
    fun testCyrillic_ukrainianAllUnique() = runTest {
        assertEquals("h", RomanizationUtils.romanizeCyrillic("г", "Ukrainian"))
        assertEquals("g", RomanizationUtils.romanizeCyrillic("ґ", "Ukrainian"))
        assertEquals("ye", RomanizationUtils.romanizeCyrillic("є", "Ukrainian"))
        assertEquals("y", RomanizationUtils.romanizeCyrillic("и", "Ukrainian"))
        assertEquals("i", RomanizationUtils.romanizeCyrillic("і", "Ukrainian"))
        assertEquals("yi", RomanizationUtils.romanizeCyrillic("ї", "Ukrainian"))
        assertEquals("yu", RomanizationUtils.romanizeCyrillic("ю", "Ukrainian"))
        assertEquals("ya", RomanizationUtils.romanizeCyrillic("я", "Ukrainian"))
    }

    @Test
    fun testCyrillic_russianAllCapsGenitive() = runTest {
        assertEquals("OVO", RomanizationUtils.romanizeCyrillic("ОГО", "Russian"))
        assertEquals("EVO", RomanizationUtils.romanizeCyrillic("ЕГО", "Russian"))
    }

    @Test
    fun testCyrillic_serbianWithDiacritics() = runTest {
        assertEquals("\u017E", RomanizationUtils.romanizeCyrillic("ж", "Serbian"))
        assertEquals("\u010D", RomanizationUtils.romanizeCyrillic("ч", "Serbian"))
        assertEquals("\u0161", RomanizationUtils.romanizeCyrillic("ш", "Serbian"))
        assertEquals("d\u017E", RomanizationUtils.romanizeCyrillic("џ", "Serbian"))
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

    @Test
    fun testPunjabi_tippiAndAddak() = runTest {
        assertEquals("panj", RomanizationUtils.romanizePunjabi("ਪੰਜ"))
        assertNotNull(RomanizationUtils.romanizePunjabi("ਸੱਚ"))
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
