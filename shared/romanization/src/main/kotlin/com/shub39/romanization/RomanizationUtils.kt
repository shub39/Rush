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

import android.icu.text.Transliterator
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RomanizationUtils {
    private val japaneseTransliterator by lazy { Transliterator.getInstance("Any-Latin; Any-ASCII") }
    private val chineseTransliterator by lazy { Transliterator.getInstance("Han-Latin; Any-ASCII") }

    // Hangul Romaja mapping
    private val HANGUL_ROMAJA_MAP: Map<String, Map<String, String>> =
        mapOf(
            "cho" to
                mapOf(
                    "ᄀ" to "g",
                    "ᄁ" to "kk",
                    "ᄂ" to "n",
                    "ᄃ" to "d",
                    "ᄄ" to "tt",
                    "ᄅ" to "r",
                    "ᄆ" to "m",
                    "ᄇ" to "b",
                    "ᄈ" to "pp",
                    "ᄉ" to "s",
                    "ᄊ" to "ss",
                    "ᄋ" to "",
                    "ᄌ" to "j",
                    "ᄍ" to "jj",
                    "ᄎ" to "ch",
                    "ᄏ" to "k",
                    "ᄐ" to "t",
                    "ᄑ" to "p",
                    "ᄒ" to "h",
                ),
            "jung" to
                mapOf(
                    "ᅡ" to "a",
                    "ᅢ" to "ae",
                    "ᅣ" to "ya",
                    "ᅤ" to "yae",
                    "ᅥ" to "eo",
                    "ᅦ" to "e",
                    "ᅧ" to "yeo",
                    "ᅨ" to "ye",
                    "ᅩ" to "o",
                    "ᅪ" to "wa",
                    "ᅫ" to "wae",
                    "ᅬ" to "oe",
                    "ᅭ" to "yo",
                    "ᅮ" to "u",
                    "ᅯ" to "wo",
                    "ᅰ" to "we",
                    "ᅱ" to "wi",
                    "ᅲ" to "yu",
                    "ᅳ" to "eu",
                    "ᅴ" to "eui",
                    "ᅵ" to "i",
                ),
            "jong" to
                mapOf(
                    "ᆨ" to "k",
                    "ᆨᄋ" to "g",
                    "ᆨᄂ" to "ngn",
                    "ᆨᄅ" to "ngn",
                    "ᆨᄆ" to "ngm",
                    "ᆨᄒ" to "kh",
                    "ᆩ" to "kk",
                    "ᆩᄋ" to "kg",
                    "ᆩᄂ" to "ngn",
                    "ᆩᄅ" to "ngn",
                    "ᆩᄆ" to "ngm",
                    "ᆩᄒ" to "kh",
                    "ᆪ" to "k",
                    "ᆪᄋ" to "ks",
                    "ᆪᄂ" to "ngn",
                    "ᆪᄅ" to "ngn",
                    "ᆪᄆ" to "ngm",
                    "ᆪᄒ" to "kch",
                    "ᆫ" to "n",
                    "ᆫᄅ" to "ll",
                    "ᆬ" to "n",
                    "ᆬᄋ" to "nj",
                    "ᆬᄂ" to "nn",
                    "ᆬᄅ" to "nn",
                    "ᆬᄆ" to "nm",
                    "ᆬㅎ" to "nch",
                    "ᆭ" to "n",
                    "ᆭᄋ" to "nh",
                    "ᆭᄅ" to "nn",
                    "ᆮ" to "t",
                    "ᆮᄋ" to "d",
                    "ᆮᄂ" to "nn",
                    "ᆮᄅ" to "nn",
                    "ᆮᄆ" to "nm",
                    "ᆮᄒ" to "th",
                    "ᆯ" to "l",
                    "ᆯᄋ" to "r",
                    "ᆯᄂ" to "ll",
                    "ᆯᄅ" to "ll",
                    "ᆰ" to "k",
                    "ᆰᄋ" to "lg",
                    "ᆰᄂ" to "ngn",
                    "ᆰᄅ" to "ngn",
                    "ᆰᄆ" to "ngm",
                    "ᆰᄒ" to "lkh",
                    "ᆱ" to "m",
                    "ᆱᄋ" to "lm",
                    "ᆱᄂ" to "mn",
                    "ᆱᄅ" to "mn",
                    "ᆱᄆ" to "mm",
                    "ᆱᄒ" to "lmh",
                    "ᆲ" to "p",
                    "ᆲᄋ" to "lb",
                    "ᆲᄂ" to "mn",
                    "ᆲᄅ" to "mn",
                    "ᆲᄆ" to "mm",
                    "ᆲᄒ" to "lph",
                    "ᆳ" to "t",
                    "ᆳᄋ" to "ls",
                    "ᆳᄂ" to "nn",
                    "ᆳᄅ" to "nn",
                    "ᆳᄆ" to "nm",
                    "ᆳᄒ" to "lsh",
                    "ᆴ" to "t",
                    "ᆴᄋ" to "lt",
                    "ᆴᄂ" to "nn",
                    "ᆴᄅ" to "nn",
                    "ᆴᄆ" to "nm",
                    "ᆴᄒ" to "lth",
                    "ᆵ" to "p",
                    "ᆵᄋ" to "lp",
                    "ᆵᄂ" to "mn",
                    "ᆵᄅ" to "mn",
                    "ᆵᄆ" to "mm",
                    "ᆵᄒ" to "lph",
                    "ᆶ" to "l",
                    "ᆶᄋ" to "lh",
                    "ᆶᄂ" to "ll",
                    "ᆶᄅ" to "ll",
                    "ᆶᄆ" to "lm",
                    "ᆶᄒ" to "lh",
                    "ᆷ" to "m",
                    "ᆷᄅ" to "mn",
                    "ᆸ" to "p",
                    "ᆸᄋ" to "b",
                    "ᆸᄂ" to "mn",
                    "ᆸᄅ" to "mn",
                    "ᆸᄆ" to "mm",
                    "ᆸᄒ" to "ph",
                    "ᆹ" to "p",
                    "ᆹᄋ" to "ps",
                    "ᆹᄂ" to "mn",
                    "ᆹᄅ" to "mn",
                    "ᆹᄆ" to "mm",
                    "ᆹᄒ" to "psh",
                    "ᆺ" to "t",
                    "ᆺᄋ" to "s",
                    "ᆺᄂ" to "nn",
                    "ᆺᄅ" to "nn",
                    "ᆺᄆ" to "nm",
                    "ᆺᄒ" to "sh",
                    "ᆻ" to "t",
                    "ᆻᄋ" to "ss",
                    "ᆻᄂ" to "tn",
                    "ᆻᄅ" to "tn",
                    "ᆻᄆ" to "nm",
                    "ᆻᄒ" to "th",
                    "ᆼ" to "ng",
                    "ᆽ" to "t",
                    "ᆽᄋ" to "j",
                    "ᆽᄂ" to "nn",
                    "ᆽᄅ" to "nn",
                    "ᆽᄆ" to "nm",
                    "ᆽᄒ" to "ch",
                    "ᆾ" to "t",
                    "ᆾᄋ" to "ch",
                    "ᆾᄂ" to "nn",
                    "ᆾᄅ" to "nn",
                    "ᆾᄆ" to "nm",
                    "ᆾᄒ" to "ch",
                    "ᆿ" to "k",
                    "ᆿᄋ" to "k",
                    "ᆿᄂ" to "ngn",
                    "ᆿᄅ" to "ngn",
                    "ᆿᄆ" to "ngm",
                    "ᆿᄒ" to "kh",
                    "ᇀ" to "t",
                    "ᇀᄋ" to "t",
                    "ᇀᄂ" to "nn",
                    "ᇀᄅ" to "nn",
                    "ᇀᄆ" to "nm",
                    "ᇀᄒ" to "th",
                    "ᇁ" to "p",
                    "ᇁᄋ" to "p",
                    "ᇁᄂ" to "mn",
                    "ᇁᄅ" to "mn",
                    "ᇁᄆ" to "mm",
                    "ᇁᄒ" to "ph",
                    "ᇂ" to "t",
                    "ᇂᄋ" to "h",
                    "ᇂᄂ" to "nn",
                    "ᇂᄅ" to "nn",
                    "ᇂᄆ" to "mm",
                    "ᇂᄒ" to "t",
                    "ᇂᄀ" to "k",
                ),
        )

    // Devanagari (Hindi) mapping
    private val DEVANAGARI_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "अ" to "a",
            "आ" to "aa",
            "इ" to "i",
            "ई" to "ee",
            "उ" to "u",
            "ऊ" to "oo",
            "ऋ" to "ri",
            "ए" to "e",
            "ऐ" to "ai",
            "ओ" to "o",
            "औ" to "au",
            "क" to "k",
            "ख" to "kh",
            "ग" to "g",
            "घ" to "gh",
            "ङ" to "ng",
            "च" to "ch",
            "छ" to "chh",
            "ज" to "j",
            "झ" to "jh",
            "ञ" to "ny",
            "ट" to "t",
            "ठ" to "th",
            "ड" to "d",
            "ढ" to "dh",
            "ण" to "n",
            "त" to "t",
            "थ" to "th",
            "द" to "d",
            "ध" to "dh",
            "न" to "n",
            "प" to "p",
            "फ" to "ph",
            "ब" to "b",
            "भ" to "bh",
            "म" to "m",
            "य" to "y",
            "र" to "r",
            "ल" to "l",
            "व" to "v",
            "श" to "sh",
            "ष" to "sh",
            "स" to "s",
            "ह" to "h",
            "क्ष" to "ksh",
            "त्र" to "tr",
            "ज्ञ" to "gy",
            "श्र" to "shr",
            "ा" to "aa",
            "ि" to "i",
            "ी" to "ee",
            "ु" to "u",
            "ू" to "oo",
            "ृ" to "ri",
            "े" to "e",
            "ै" to "ai",
            "ो" to "o",
            "ौ" to "au",
            "ं" to "n",
            "ः" to "h",
            "ँ" to "n",
            "़" to "",
            "्" to "",
            "०" to "0",
            "१" to "1",
            "२" to "2",
            "३" to "3",
            "४" to "4",
            "५" to "5",
            "६" to "6",
            "७" to "7",
            "८" to "8",
            "९" to "9",
            "ॐ" to "Om",
            "ऽ" to "",
            "क़" to "q",
            "ख़" to "kh",
            "ग़" to "g",
            "ज़" to "z",
            "ड़" to "r",
            "ढ़" to "rh",
            "फ़" to "f",
            "य़" to "y",
        )

    // Gurmukhi (Punjabi) mapping
    private val GURMUKHI_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "ੳ" to "o",
            "ਅ" to "a",
            "ੲ" to "e",
            "ਸ" to "s",
            "ਹ" to "h",
            "ਕ" to "k",
            "ਖ" to "kh",
            "ਗ" to "g",
            "ਘ" to "gh",
            "ਙ" to "ng",
            "ਚ" to "ch",
            "ਛ" to "chh",
            "ਜ" to "j",
            "ਝ" to "jh",
            "ਞ" to "ny",
            "ਟ" to "t",
            "ਠ" to "th",
            "ਡ" to "d",
            "ਢ" to "dh",
            "ਣ" to "n",
            "ਤ" to "t",
            "ਥ" to "th",
            "ਦ" to "d",
            "ਧ" to "dh",
            "ਨ" to "n",
            "ਪ" to "p",
            "ਫ" to "ph",
            "ਬ" to "b",
            "ਭ" to "bh",
            "ਮ" to "m",
            "ਯ" to "y",
            "ਰ" to "r",
            "ਲ" to "l",
            "ਵ" to "v",
            "ੜ" to "r",
            "ਸ਼" to "sh",
            "ਖ਼" to "kh",
            "ਗ਼" to "g",
            "ਜ਼" to "z",
            "ਫ਼" to "f",
            "ਲ਼" to "l",
            "ਾ" to "aa",
            "ਿ" to "i",
            "ੀ" to "ee",
            "ੁ" to "u",
            "ੂ" to "oo",
            "ੇ" to "e",
            "ੈ" to "ai",
            "ੋ" to "o",
            "ੌ" to "au",
            "ੰ" to "n",
            "ਂ" to "n",
            "ੱ" to "",
            "੍" to "",
            "਼" to "",
            "ੴ" to "Ek Onkar",
            "੦" to "0",
            "੧" to "1",
            "੨" to "2",
            "੩" to "3",
            "੪" to "4",
            "੫" to "5",
            "੬" to "6",
            "੭" to "7",
            "੮" to "8",
            "੯" to "9",
        )

    // Cyrillic mappings
    private val GENERAL_CYRILLIC_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "А" to "A",
            "Б" to "B",
            "В" to "V",
            "Г" to "G",
            "Ґ" to "G",
            "Д" to "D",
            "Ѓ" to "Ǵ",
            "Ђ" to "Đ",
            "Е" to "E",
            "Ё" to "Yo",
            "Є" to "Ye",
            "Ж" to "Zh",
            "З" to "Z",
            "Ѕ" to "Dz",
            "И" to "I",
            "І" to "I",
            "Ї" to "Yi",
            "Й" to "Y",
            "Ј" to "Y",
            "К" to "K",
            "Л" to "L",
            "Љ" to "Ly",
            "М" to "M",
            "Н" to "N",
            "Њ" to "Ny",
            "О" to "O",
            "П" to "P",
            "Р" to "R",
            "С" to "S",
            "Т" to "T",
            "Ћ" to "Ć",
            "У" to "U",
            "Ў" to "Ŭ",
            "Ф" to "F",
            "Х" to "Kh",
            "Ц" to "Ts",
            "Ч" to "Ch",
            "Џ" to "Dž",
            "Ш" to "Sh",
            "Щ" to "Shch",
            "Ъ" to "ʺ",
            "Ы" to "Y",
            "Ь" to "ʹ",
            "Э" to "E",
            "Ю" to "Yu",
            "Я" to "Ya",
            "а" to "a",
            "б" to "b",
            "в" to "v",
            "г" to "g",
            "ґ" to "g",
            "д" to "d",
            "ѓ" to "ǵ",
            "ђ" to "đ",
            "е" to "e",
            "ё" to "yo",
            "є" to "ye",
            "ж" to "zh",
            "з" to "z",
            "ѕ" to "dz",
            "и" to "i",
            "і" to "i",
            "ї" to "yi",
            "й" to "y",
            "ј" to "y",
            "к" to "k",
            "л" to "l",
            "љ" to "ly",
            "м" to "m",
            "н" to "n",
            "њ" to "ny",
            "о" to "o",
            "п" to "p",
            "р" to "r",
            "с" to "s",
            "т" to "t",
            "ћ" to "ć",
            "у" to "u",
            "ў" to "ŭ",
            "ф" to "f",
            "х" to "kh",
            "ц" to "ts",
            "ч" to "ch",
            "џ" to "dž",
            "ш" to "sh",
            "щ" to "shch",
            "ъ" to "ʺ",
            "ы" to "y",
            "ь" to "ʹ",
            "э" to "e",
            "ю" to "yu",
            "я" to "ya",
        )

    private val RUSSIAN_ROMAJI_MAP: Map<String, String> =
        mapOf("ого" to "ovo", "Ого" to "Ovo", "его" to "evo", "Его" to "Evo")

    private val UKRAINIAN_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "Г" to "H",
            "г" to "h",
            "Ґ" to "G",
            "ґ" to "g",
            "Є" to "Ye",
            "є" to "ye",
            "І" to "I",
            "і" to "i",
            "Ї" to "Yi",
            "ї" to "yi",
        )

    private val SERBIAN_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "Ж" to "Ž",
            "Љ" to "Lj",
            "Њ" to "Nj",
            "Ц" to "C",
            "Ч" to "Č",
            "Џ" to "Dž",
            "Ш" to "Š",
            "Х" to "H",
            "ж" to "ž",
            "љ" to "lj",
            "њ" to "nj",
            "ц" to "c",
            "ч" to "č",
            "џ" to "dž",
            "ш" to "š",
            "х" to "h",
        )

    private val BULGARIAN_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "Ж" to "Zh",
            "Ц" to "Ts",
            "Ч" to "Ch",
            "Ш" to "Sh",
            "Щ" to "Sht",
            "Ъ" to "A",
            "Ь" to "Y",
            "Ю" to "Yu",
            "Я" to "Ya",
            "ж" to "zh",
            "ц" to "ts",
            "ч" to "ch",
            "ш" to "sh",
            "щ" to "sht",
            "ъ" to "a",
            "ь" to "y",
            "ю" to "yu",
            "я" to "ya",
        )

    private val BELARUSIAN_ROMAJI_MAP: Map<String, String> =
        mapOf("Г" to "H", "г" to "h", "Ў" to "W", "ў" to "w")

    private val KYRGYZ_ROMAJI_MAP: Map<String, String> =
        mapOf("Ү" to "Ü", "ү" to "ü", "Ы" to "Y", "ы" to "y")

    private val MACEDONIAN_ROMAJI_MAP: Map<String, String> =
        mapOf(
            "Ѓ" to "Gj",
            "Ѕ" to "Dz",
            "И" to "I",
            "Ј" to "J",
            "Љ" to "Lj",
            "Њ" to "Nj",
            "Ќ" to "Kj",
            "Џ" to "Dž",
            "Ч" to "Č",
            "Ш" to "Sh",
            "Ж" to "Zh",
            "Ц" to "C",
            "Х" to "H",
            "ѓ" to "gj",
            "ѕ" to "dz",
            "и" to "i",
            "ј" to "j",
            "љ" to "lj",
            "њ" to "nj",
            "ќ" to "kj",
            "џ" to "dž",
            "ч" to "č",
            "ш" to "sh",
            "ж" to "zh",
            "ц" to "c",
            "х" to "h",
        )

    // Japanese romanization
    suspend fun romanizeJapanese(text: String): String =
        withContext(Dispatchers.Default) {
            japaneseTransliterator.transliterate(text).lowercase(Locale.ROOT)
        }

    // Korean romanization
    suspend fun romanizeKorean(text: String): String =
        withContext(Dispatchers.Default) {
            val romajaBuilder = StringBuilder()
            var prevFinal: String? = null

            for (i in text.indices) {
                val char = text[i]
                if (char in '\uAC00'..'\uD7A3') {
                    val syllableIndex = char.code - 0xAC00
                    val choIndex = syllableIndex / (21 * 28)
                    val jungIndex = (syllableIndex % (21 * 28)) / 28
                    val jongIndex = syllableIndex % 28

                    val choChar = (0x1100 + choIndex).toChar().toString()
                    val jungChar = (0x1161 + jungIndex).toChar().toString()
                    val jongChar =
                        if (jongIndex == 0) null else (0x11A7 + jongIndex).toChar().toString()

                    if (prevFinal != null) {
                        val contextKey = prevFinal + choChar
                        val jong =
                            HANGUL_ROMAJA_MAP["jong"]?.get(contextKey)
                                ?: HANGUL_ROMAJA_MAP["jong"]?.get(prevFinal)
                                ?: prevFinal
                        romajaBuilder.append(jong)
                    }

                    val cho = HANGUL_ROMAJA_MAP["cho"]?.get(choChar) ?: choChar
                    val jung = HANGUL_ROMAJA_MAP["jung"]?.get(jungChar) ?: jungChar
                    romajaBuilder.append(cho).append(jung)
                    prevFinal = jongChar
                } else {
                    if (prevFinal != null) {
                        val jong = HANGUL_ROMAJA_MAP["jong"]?.get(prevFinal) ?: prevFinal
                        romajaBuilder.append(jong)
                        prevFinal = null
                    }
                    romajaBuilder.append(char)
                }
            }

            if (prevFinal != null) {
                val jong = HANGUL_ROMAJA_MAP["jong"]?.get(prevFinal) ?: prevFinal
                romajaBuilder.append(jong)
            }

            romajaBuilder.toString()
        }

    // Chinese romanization
    suspend fun romanizeChinese(text: String): String =
        withContext(Dispatchers.Default) {
            chineseTransliterator.transliterate(text).lowercase(Locale.ROOT)
        }

    // Cyrillic romanization
    suspend fun romanizeCyrillic(text: String, language: String? = null): String? =
        withContext(Dispatchers.Default) {
            if (text.isEmpty()) return@withContext null

            val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }

            if (
                cyrillicChars.isEmpty() ||
                    (cyrillicChars.length == 1 &&
                        (cyrillicChars[0] == 'е' || cyrillicChars[0] == 'Е'))
            ) {
                return@withContext null
            }

            when (language) {
                "Russian" -> romanizeRussianInternal(text)
                "Ukrainian" -> romanizeUkrainianInternal(text)
                "Serbian" -> romanizeSerbianInternal(text)
                "Bulgarian" -> romanizeBulgarianInternal(text)
                "Belarusian" -> romanizeBelarusianInternal(text)
                "Kyrgyz" -> romanizeKyrgyzInternal(text)
                "Macedonian" -> romanizeMacedonianInternal(text)
                else -> {
                    val sb = StringBuilder()
                    for (char in text) {
                        val str = char.toString()
                        sb.append(GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
                    }
                    sb.toString()
                }
            }
        }

    private fun romanizeRussianInternal(text: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            if (i + 2 <= text.length) {
                val threeChar = text.substring(i, minOf(i + 3, text.length))
                if (RUSSIAN_ROMAJI_MAP.containsKey(threeChar)) {
                    sb.append(RUSSIAN_ROMAJI_MAP[threeChar])
                    i += 3
                    continue
                }
            }
            val char = text[i]
            val str = char.toString()
            sb.append(GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
            i++
        }
        return sb.toString()
    }

    private fun romanizeUkrainianInternal(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            val str = char.toString()
            sb.append(UKRAINIAN_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
        }
        return sb.toString()
    }

    private fun romanizeSerbianInternal(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            val str = char.toString()
            sb.append(SERBIAN_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
        }
        return sb.toString()
    }

    private fun romanizeBulgarianInternal(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            val str = char.toString()
            sb.append(BULGARIAN_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
        }
        return sb.toString()
    }

    private fun romanizeBelarusianInternal(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            val str = char.toString()
            sb.append(BELARUSIAN_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
        }
        return sb.toString()
    }

    private fun romanizeKyrgyzInternal(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            val str = char.toString()
            sb.append(KYRGYZ_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
        }
        return sb.toString()
    }

    private fun romanizeMacedonianInternal(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            val str = char.toString()
            sb.append(MACEDONIAN_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
        }
        return sb.toString()
    }

    // Hindi romanization
    suspend fun romanizeHindi(text: String): String =
        withContext(Dispatchers.Default) {
            val sb = StringBuilder()
            var i = 0
            while (i < text.length) {
                var consumed = false
                if (i + 1 < text.length) {
                    val twoChar = text.substring(i, i + 2)
                    if (DEVANAGARI_ROMAJI_MAP.containsKey(twoChar)) {
                        sb.append(DEVANAGARI_ROMAJI_MAP[twoChar])
                        i += 2
                        consumed = true
                    }
                }
                if (!consumed) {
                    val char = text[i]
                    val str = char.toString()
                    sb.append(DEVANAGARI_ROMAJI_MAP[str] ?: str)
                    i++
                }
            }
            sb.toString()
        }

    // Punjabi romanization
    suspend fun romanizePunjabi(text: String): String =
        withContext(Dispatchers.Default) {
            val sb = StringBuilder()
            var i = 0
            while (i < text.length) {
                var consumed = false
                if (i + 1 < text.length) {
                    val twoChar = text.substring(i, i + 2)
                    if (GURMUKHI_ROMAJI_MAP.containsKey(twoChar)) {
                        sb.append(GURMUKHI_ROMAJI_MAP[twoChar])
                        i += 2
                        consumed = true
                    }
                }
                if (!consumed) {
                    val char = text[i]
                    val str = char.toString()
                    sb.append(GURMUKHI_ROMAJI_MAP[str] ?: str)
                    i++
                }
            }
            sb.toString()
        }

    // Language detection
    fun isJapanese(text: String): Boolean {
        return text.any { it in '\u3040'..'\u309F' || it in '\u30A0'..'\u30FF' }
    }

    fun isKorean(text: String): Boolean {
        return text.any {
            it in '\uAC00'..'\uD7A3' || it in '\u1100'..'\u11FF' || it in '\u3130'..'\u318F'
        }
    }

    fun isChinese(text: String): Boolean {
        return text.any { it in '\u4E00'..'\u9FFF' }
    }

    fun isHindi(text: String): Boolean {
        return text.any { it in '\u0900'..'\u097F' }
    }

    fun isPunjabi(text: String): Boolean {
        return text.any { it in '\u0A00'..'\u0A7F' }
    }

    fun isCyrillic(text: String): Boolean {
        return text.any { it in '\u0400'..'\u04FF' }
    }

    private val UKRAINIAN_UNIQUE_LETTERS = setOf('Ґ', 'ґ', 'Є', 'є', 'Ї', 'ї')
    private val SERBIAN_UNIQUE_LETTERS = setOf('Ђ', 'ђ', 'Ћ', 'ћ', 'Џ', 'џ')
    private val BELARUSIAN_UNIQUE_LETTERS = setOf('Ў', 'ў')
    private val KYRGYZ_UNIQUE_LETTERS = setOf('Ң', 'ң', 'Ү', 'ү')
    private val MACEDONIAN_UNIQUE_LETTERS = setOf('Ѓ', 'ѓ', 'Ѕ', 'ѕ', 'Ќ', 'ќ')
    private val RUSSIAN_SPECIFIC_LETTERS = setOf('Ы', 'ы', 'Э', 'э', 'Ё', 'ё')

    fun isRussian(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        if (cyrillicChars.any { it in UKRAINIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in SERBIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in BELARUSIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in KYRGYZ_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in MACEDONIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.none { it in RUSSIAN_SPECIFIC_LETTERS }) return false
        return true
    }

    fun isUkrainian(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        return cyrillicChars.any { it in UKRAINIAN_UNIQUE_LETTERS }
    }

    fun isSerbian(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        return cyrillicChars.any { it in SERBIAN_UNIQUE_LETTERS }
    }

    fun isBulgarian(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        if (cyrillicChars.any { it in UKRAINIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in SERBIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in BELARUSIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in KYRGYZ_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in MACEDONIAN_UNIQUE_LETTERS }) return false
        if (cyrillicChars.any { it in RUSSIAN_SPECIFIC_LETTERS }) return false
        return true
    }

    fun isBelarusian(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        return cyrillicChars.any { it in BELARUSIAN_UNIQUE_LETTERS }
    }

    fun isKyrgyz(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        return cyrillicChars.any { it in KYRGYZ_UNIQUE_LETTERS }
    }

    fun isMacedonian(text: String): Boolean {
        val cyrillicChars = text.filter { it in '\u0400'..'\u04FF' }
        if (cyrillicChars.isEmpty()) return false
        return cyrillicChars.any { it in MACEDONIAN_UNIQUE_LETTERS }
    }

    // Main romanization function
    suspend fun romanize(
        text: String,
        enabledLanguages: List<String> =
            listOf(
                "Japanese",
                "Korean",
                "Chinese",
                "Hindi",
                "Punjabi",
                "Russian",
                "Ukrainian",
                "Serbian",
                "Bulgarian",
                "Belarusian",
                "Kyrgyz",
                "Macedonian",
            ),
    ): String? {
        return when {
            "Japanese" in enabledLanguages && isJapanese(text) -> romanizeJapanese(text)
            "Korean" in enabledLanguages && isKorean(text) -> romanizeKorean(text)
            "Chinese" in enabledLanguages && isChinese(text) -> romanizeChinese(text)
            "Hindi" in enabledLanguages && isHindi(text) -> romanizeHindi(text)
            "Punjabi" in enabledLanguages && isPunjabi(text) -> romanizePunjabi(text)
            "Ukrainian" in enabledLanguages && isUkrainian(text) ->
                romanizeCyrillic(text, "Ukrainian")

            "Serbian" in enabledLanguages && isSerbian(text) -> romanizeCyrillic(text, "Serbian")
            "Macedonian" in enabledLanguages && isMacedonian(text) ->
                romanizeCyrillic(text, "Macedonian")

            "Belarusian" in enabledLanguages && isBelarusian(text) ->
                romanizeCyrillic(text, "Belarusian")

            "Kyrgyz" in enabledLanguages && isKyrgyz(text) -> romanizeCyrillic(text, "Kyrgyz")
            "Bulgarian" in enabledLanguages && isBulgarian(text) ->
                romanizeCyrillic(text, "Bulgarian")

            "Russian" in enabledLanguages && isRussian(text) -> romanizeCyrillic(text, "Russian")
            else -> null
        }
    }
}
