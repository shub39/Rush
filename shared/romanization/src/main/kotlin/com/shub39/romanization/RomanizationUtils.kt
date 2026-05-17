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

import android.content.Context
import android.icu.text.Transliterator
import android.util.Log
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RomanizationUtils {

    // Japanese tokenizer (longest-match over the reading dictionary)

    // Reading dictionary: surface form → katakana reading
    @Volatile private var readingDictionary: Map<String, String>? = null

    // Fast longest-match index: first-char → (surface, reading) sorted by length desc
    @Volatile private var tokenizerIndex: Map<Char, List<Pair<String, String>>>? = null

    /**
     * Load the reading dictionary and build the tokenizer index.
     *
     * @param context Application context for accessing assets
     */
    fun loadReadingDictionary(context: Context) {
        if (readingDictionary != null) return
        try {
            val startMs = System.currentTimeMillis()

            // 1. Read TSV into map
            val lines =
                context.assets.open("ja_readings.tsv").bufferedReader(Charsets.UTF_8).readLines()
            val map = HashMap<String, String>(lines.size)
            for (line in lines) {
                val tab = line.indexOf('\t')
                if (tab > 0) {
                    map[line.substring(0, tab)] = line.substring(tab + 1)
                }
            }
            readingDictionary = map

            // 2. Build longest-match index
            val grouped = HashMap<Char, MutableList<Pair<String, String>>>()
            for ((surface, reading) in map) {
                if (surface.isEmpty()) continue
                grouped.getOrPut(surface[0]) { mutableListOf() }.add(surface to reading)
            }
            // Sort each bucket by length descending (longest match first)
            for (list in grouped.values) {
                list.sortByDescending { it.first.length }
            }
            tokenizerIndex = grouped

            val elapsed = System.currentTimeMillis() - startMs
            Log.d("RomanizationUtils", "Loaded ${map.size} readings + index in ${elapsed}ms")
        } catch (e: Exception) {
            Log.e("RomanizationUtils", "Failed to load reading dictionary", e)
            readingDictionary = emptyMap()
            tokenizerIndex = emptyMap()
        }
    }

    /**
     * Reset the reading dictionary so the next call to [loadReadingDictionary] reloads from assets.
     * Used by [com.shub39.rush.app.JapaneseReadingProvider.reload] after a dictionary update.
     */
    fun resetReadingDictionary() {
        readingDictionary = null
        tokenizerIndex = null
    }

    // ICU transliterators for Japanese→romaji conversion
    // Katakana-Latin: used when we have an explicit katakana reading from a dictionary
    private val katakanaTransliterator by lazy { Transliterator.getInstance("Katakana-Latin") }

    // Note: We intentionally do NOT use Any-Latin or similar transliterators
    // for kanji because they give Chinese pinyin readings (e.g. 日本語 → rì běn yǔ)
    // instead of Japanese readings. The bundled reading dictionary (ja_readings.tsv)
    // provides proper Japanese readings.

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
                    "ᆨᄋ" to "",
                    "ᆨᄂ" to "ng",
                    "ᆨᄅ" to "ng",
                    "ᆨᄆ" to "ng",
                    "ᆨᄒ" to "",
                    "ᆩ" to "kk",
                    "ᆩᄋ" to "",
                    "ᆩᄂ" to "ng",
                    "ᆩᄅ" to "ng",
                    "ᆩᄆ" to "ng",
                    "ᆩᄒ" to "",
                    "ᆪ" to "k",
                    "ᆪᄋ" to "k",
                    "ᆪᄂ" to "ng",
                    "ᆪᄅ" to "ng",
                    "ᆪᄆ" to "ng",
                    "ᆪᄒ" to "k",
                    "ᆫ" to "n",
                    "ᆫᄅ" to "l",
                    "ᆬ" to "n",
                    "ᆬᄋ" to "n",
                    "ᆬᄂ" to "n",
                    "ᆬᄅ" to "l",
                    "ᆬᄆ" to "n",
                    "ᆬᄒ" to "n",
                    "ᆭ" to "n",
                    "ᆭᄋ" to "n",
                    "ᆭᄅ" to "l",
                    "ᆭᄒ" to "n",
                    "ᆮ" to "t",
                    "ᆮᄋ" to "",
                    "ᆮᄂ" to "n",
                    "ᆮᄅ" to "n",
                    "ᆮᄆ" to "n",
                    "ᆮᄒ" to "",
                    "ᆯ" to "l",
                    "ᆯᄋ" to "",
                    "ᆯᄂ" to "l",
                    "ᆯᄅ" to "l",
                    "ᆰ" to "k",
                    "ᆰᄋ" to "l",
                    "ᆰᄂ" to "ng",
                    "ᆰᄅ" to "ng",
                    "ᆰᄆ" to "ng",
                    "ᆰᄒ" to "l",
                    "ᆱ" to "m",
                    "ᆱᄋ" to "l",
                    "ᆱᄂ" to "m",
                    "ᆱᄅ" to "m",
                    "ᆱᄆ" to "m",
                    "ᆱᄒ" to "l",
                    "ᆲ" to "p",
                    "ᆲᄋ" to "l",
                    "ᆲᄂ" to "m",
                    "ᆲᄅ" to "m",
                    "ᆲᄆ" to "m",
                    "ᆲᄒ" to "l",
                    "ᆳ" to "t",
                    "ᆳᄋ" to "l",
                    "ᆳᄂ" to "n",
                    "ᆳᄅ" to "n",
                    "ᆳᄆ" to "n",
                    "ᆳᄒ" to "l",
                    "ᆴ" to "t",
                    "ᆴᄋ" to "l",
                    "ᆴᄂ" to "n",
                    "ᆴᄅ" to "n",
                    "ᆴᄆ" to "n",
                    "ᆴᄒ" to "l",
                    "ᆵ" to "p",
                    "ᆵᄋ" to "l",
                    "ᆵᄂ" to "m",
                    "ᆵᄅ" to "m",
                    "ᆵᄆ" to "m",
                    "ᆵᄒ" to "l",
                    "ᆶ" to "l",
                    "ᆶᄋ" to "l",
                    "ᆶᄂ" to "l",
                    "ᆶᄅ" to "l",
                    "ᆶᄆ" to "l",
                    "ᆶᄒ" to "l",
                    "ᆷ" to "m",
                    "ᆷᄅ" to "m",
                    "ᆸ" to "p",
                    "ᆸᄋ" to "",
                    "ᆸᄂ" to "m",
                    "ᆸᄅ" to "m",
                    "ᆸᄆ" to "m",
                    "ᆸᄒ" to "",
                    "ᆹ" to "p",
                    "ᆹᄋ" to "p",
                    "ᆹᄂ" to "m",
                    "ᆹᄅ" to "m",
                    "ᆹᄆ" to "m",
                    "ᆹᄒ" to "p",
                    "ᆺ" to "t",
                    "ᆺᄋ" to "",
                    "ᆺᄂ" to "n",
                    "ᆺᄅ" to "n",
                    "ᆺᄆ" to "n",
                    "ᆺᄒ" to "",
                    "ᆻ" to "t",
                    "ᆻᄋ" to "",
                    "ᆻᄂ" to "n",
                    "ᆻᄅ" to "n",
                    "ᆻᄆ" to "n",
                    "ᆻᄒ" to "",
                    "ᆼ" to "ng",
                    "ᆽ" to "t",
                    "ᆽᄋ" to "",
                    "ᆽᄂ" to "n",
                    "ᆽᄅ" to "n",
                    "ᆽᄆ" to "n",
                    "ᆽᄒ" to "",
                    "ᆾ" to "t",
                    "ᆾᄋ" to "",
                    "ᆾᄂ" to "n",
                    "ᆾᄅ" to "n",
                    "ᆾᄆ" to "n",
                    "ᆾᄒ" to "",
                    "ᆿ" to "k",
                    "ᆿᄋ" to "",
                    "ᆿᄂ" to "ng",
                    "ᆿᄅ" to "ng",
                    "ᆿᄆ" to "ng",
                    "ᆿᄒ" to "",
                    "ᇀ" to "t",
                    "ᇀᄋ" to "",
                    "ᇀᄂ" to "n",
                    "ᇀᄅ" to "n",
                    "ᇀᄆ" to "n",
                    "ᇀᄒ" to "",
                    "ᇁ" to "p",
                    "ᇁᄋ" to "",
                    "ᇁᄂ" to "m",
                    "ᇁᄅ" to "m",
                    "ᇁᄆ" to "m",
                    "ᇁᄒ" to "",
                    "ᇂ" to "t",
                    "ᇂᄋ" to "",
                    "ᇂᄂ" to "n",
                    "ᇂᄅ" to "n",
                    "ᇂᄆ" to "m",
                    "ᇂᄒ" to "",
                    "ᇂᄀ" to "",
                ),
        )

    // Cho override map for Korean: when a context-dependent jong+cho combination
    // changes the cho sound, this map provides the overridden cho value.
    private val CHO_OVERRIDE: Map<String, String> =
        mapOf(
            "ᆫᄅ" to "l",
            "ᆯᄂ" to "l",
            "ᆯᄅ" to "l",
            "ᆬᄅ" to "l",
            "ᆭᄅ" to "l",
            "ᆶᄂ" to "l",
            "ᆶᄅ" to "l",
            "ᆨᄅ" to "n",
            "ᆩᄅ" to "n",
            "ᆪᄅ" to "n",
            "ᆮᄅ" to "n",
            "ᆰᄅ" to "n",
            "ᆱᄅ" to "n",
            "ᆲᄅ" to "n",
            "ᆳᄅ" to "n",
            "ᆴᄅ" to "n",
            "ᆵᄅ" to "n",
            "ᆷᄅ" to "n",
            "ᆸᄅ" to "n",
            "ᆹᄅ" to "n",
            "ᆺᄅ" to "n",
            "ᆻᄅ" to "n",
            "ᆽᄅ" to "n",
            "ᆾᄅ" to "n",
            "ᆿᄅ" to "n",
            "ᇀᄅ" to "n",
            "ᇁᄅ" to "n",
            "ᇂᄅ" to "n",
            "ᆨᄋ" to "g",
            "ᆩᄋ" to "g",
            "ᆪᄋ" to "s",
            "ᆮᄋ" to "d",
            "ᆯᄋ" to "r",
            "ᆰᄋ" to "g",
            "ᆱᄋ" to "m",
            "ᆲᄋ" to "b",
            "ᆳᄋ" to "s",
            "ᆴᄋ" to "d",
            "ᆵᄋ" to "b",
            "ᆶᄋ" to "h",
            "ᆸᄋ" to "b",
            "ᆹᄋ" to "s",
            "ᆺᄋ" to "s",
            "ᆻᄋ" to "s",
            "ᆽᄋ" to "j",
            "ᆾᄋ" to "ch",
            "ᆿᄋ" to "k",
            "ᇀᄋ" to "d",
            "ᇁᄋ" to "p",
            "ᇂᄋ" to "h",
            "ᆨᄒ" to "kh",
            "ᆩᄒ" to "kh",
            "ᆪᄒ" to "kh",
            "ᆮᄒ" to "th",
            "ᆰᄒ" to "kh",
            "ᆱᄒ" to "mh",
            "ᆲᄒ" to "ph",
            "ᆳᄒ" to "sh",
            "ᆴᄒ" to "th",
            "ᆵᄒ" to "ph",
            "ᆶᄒ" to "h",
            "ᆸᄒ" to "ph",
            "ᆹᄒ" to "s",
            "ᆺᄒ" to "s",
            "ᆻᄒ" to "th",
            "ᆽᄒ" to "ch",
            "ᆾᄒ" to "ch",
            "ᆿᄒ" to "kh",
            "ᇀᄒ" to "th",
            "ᇁᄒ" to "ph",
            "ᆬᄒ" to "ch",
            "ᆭᄒ" to "ch",
            "ᇂᄒ" to "",
            "ᇂᄀ" to "k",
            "ᆨᄂ" to "n",
            "ᆩᄂ" to "n",
            "ᆪᄂ" to "n",
            "ᆮᄂ" to "n",
            "ᆰᄂ" to "n",
            "ᆳᄂ" to "n",
            "ᆴᄂ" to "n",
            "ᆺᄂ" to "n",
            "ᆻᄂ" to "n",
            "ᆽᄂ" to "n",
            "ᆾᄂ" to "n",
            "ᆿᄂ" to "n",
            "ᇀᄂ" to "n",
            "ᇂᄂ" to "n",
            "ᆨᄆ" to "m",
            "ᆩᄆ" to "m",
            "ᆪᄆ" to "m",
            "ᆮᄆ" to "m",
            "ᆰᄆ" to "m",
            "ᆳᄆ" to "m",
            "ᆴᄆ" to "m",
            "ᆺᄆ" to "m",
            "ᆻᄆ" to "m",
            "ᆽᄆ" to "m",
            "ᆾᄆ" to "m",
            "ᆿᄆ" to "m",
            "ᇀᄆ" to "m",
            "ᇂᄆ" to "m",
            "ᆬᄂ" to "n",
            "ᆶᄆ" to "m",
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
            "क़" to "q",
            "ख़" to "kh",
            "ग़" to "g",
            "ज़" to "z",
            "ड़" to "r",
            "ढ़" to "rh",
            "फ़" to "f",
            "य़" to "y",
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
            "ਸ਼" to "sh",
            "ਖ਼" to "kh",
            "ਗ਼" to "g",
            "ਜ਼" to "z",
            "ਫ਼" to "f",
            "ਲ਼" to "l",
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
            "Ѓ" to "Ǵ",
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
            "ѓ" to "ǵ",
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
            "И" to "Y",
            "и" to "y",
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
            "Ј" to "J",
            "ж" to "ž",
            "љ" to "lj",
            "њ" to "nj",
            "ц" to "c",
            "ч" to "č",
            "џ" to "dž",
            "ш" to "š",
            "х" to "h",
            "ј" to "j",
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

    private val BELARUSIAN_ROMAJI_MAP: Map<String, String> = mapOf("Г" to "H", "г" to "h")

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
            if (text.isEmpty()) return@withContext ""

            val index = tokenizerIndex
            val dict = readingDictionary
            if (index != null && !index.isEmpty()) {
                romanizeJapaneseWithTokenizer(text, index, dict)
            } else {
                romanizeJapaneseFallback(text)
            }
        }

    /**
     * Tokenize Japanese text using longest-match over the reading dictionary.
     *
     * For each token:
     * 1. If the surface is a particle (は/へ/を) → special mapping.
     * 2. If the surface has a reading in the dictionary → transliterate reading (katakana→romaji).
     * 3. Otherwise → transliterate any kana in the surface, leave kanji unchanged.
     */
    private fun romanizeJapaneseWithTokenizer(
        text: String,
        index: Map<Char, List<Pair<String, String>>>,
        dict: Map<String, String>?,
    ): String {
        val parts = mutableListOf<String>()
        var i = 0

        while (i < text.length) {
            val ch = text[i]
            val candidates = index[ch]
            var matchedSurface: String? = null
            var matchedReading: String? = null

            // Longest-match lookup
            if (candidates != null) {
                for ((surface, reading) in candidates) {
                    if (
                        i + surface.length <= text.length &&
                            text.regionMatches(i, surface, 0, surface.length)
                    ) {
                        matchedSurface = surface
                        matchedReading = reading
                        break
                    }
                }
            }

            val surface: String
            val reading: String?

            if (matchedSurface != null) {
                surface = matchedSurface
                reading = matchedReading
                i += surface.length
            } else {
                // No dictionary match — group consecutive characters of same type
                val end =
                    when {
                        ch in '\u3040'..'\u309F' || ch in '\u30A0'..'\u30FF' -> {
                            // Group consecutive kana (hiragana/katakana)
                            var j = i + 1
                            while (j < text.length) {
                                val c = text[j]
                                if (c in '\u3040'..'\u309F' || c in '\u30A0'..'\u30FF') j++
                                else break
                            }
                            j
                        }
                        ch in 'a'..'z' || ch in 'A'..'Z' -> {
                            // Group consecutive Latin letters
                            var j = i + 1
                            while (j < text.length) {
                                val c = text[j]
                                if (c in 'a'..'z' || c in 'A'..'Z') j++ else break
                            }
                            j
                        }
                        else -> i + 1 // single character (kanji, punctuation, etc.)
                    }
                surface = text.substring(i, end)
                reading = null
                i = end
            }

            val romanized =
                when {
                    // Particles with special pronunciations
                    surface == "は" && surface.length == 1 -> "wa"
                    surface == "へ" && surface.length == 1 -> "e"
                    surface == "を" && surface.length == 1 -> "o"
                    // Reading from dictionary (katakana → romaji)
                    reading != null && reading.isNotEmpty() -> {
                        katakanaTransliterator.transliterate(reading).lowercase(Locale.ROOT)
                    }
                    // Fallback: transliterate any kana in surface, leave kanji
                    else -> {
                        val hasKana =
                            surface.any { it in '\u3040'..'\u309F' || it in '\u30A0'..'\u30FF' }
                        if (hasKana) {
                            val katakanaSurface =
                                surface
                                    .map { c ->
                                        if (c in '\u3040'..'\u309F') (c.code + 0x60).toChar() else c
                                    }
                                    .joinToString("")
                            katakanaTransliterator
                                .transliterate(katakanaSurface)
                                .lowercase(Locale.ROOT)
                        } else {
                            surface
                        }
                    }
                }

            if (romanized.isNotEmpty()) {
                parts.add(romanized)
            }
        }

        return parts
            .joinToString(" ")
            .replace(Regex("\\s+([。、？！.,!?;:：；)])"), "$1")
            .replace(Regex("([「『(（])\\s+"), "$1")
            .replace(Regex("\\s{2,}"), " ")
            .trim()
    }

    // ICU-only fallback: kana → romaji, kanji passes through
    // Uses the reading dictionary if available for kanji tokens.
    // Does NOT use Any-Latin/Japanese-Latin transliterators in this path
    // because they give Chinese pinyin for kanji which is wrong for Japanese.
    private fun romanizeJapaneseFallback(text: String): String {
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
                        val contextJong = HANGUL_ROMAJA_MAP["jong"]?.get(contextKey)
                        val jong =
                            contextJong ?: HANGUL_ROMAJA_MAP["jong"]?.get(prevFinal) ?: prevFinal
                        romajaBuilder.append(jong)

                        val cho =
                            if (contextJong != null) {
                                CHO_OVERRIDE[contextKey]
                                    ?: HANGUL_ROMAJA_MAP["cho"]?.get(choChar)
                                    ?: choChar
                            } else {
                                HANGUL_ROMAJA_MAP["cho"]?.get(choChar) ?: choChar
                            }
                        val jung = HANGUL_ROMAJA_MAP["jung"]?.get(jungChar) ?: jungChar
                        romajaBuilder.append(cho).append(jung)
                    } else {
                        val cho = HANGUL_ROMAJA_MAP["cho"]?.get(choChar) ?: choChar
                        val jung = HANGUL_ROMAJA_MAP["jung"]?.get(jungChar) ?: jungChar
                        romajaBuilder.append(cho).append(jung)
                    }
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

    private val chineseTransliterator by lazy { Transliterator.getInstance("Han-Latin") }

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
        for ((index, char) in text.withIndex()) {
            val str = char.toString()
            if (str == "ї" && index > 0 && text[index - 1].let { it in '\u0400'..'\u04FF' }) {
                sb.append("i")
            } else if (
                str == "Ї" && index > 0 && text[index - 1].let { it in '\u0400'..'\u04FF' }
            ) {
                sb.append("I")
            } else {
                sb.append(UKRAINIAN_ROMAJI_MAP[str] ?: GENERAL_CYRILLIC_ROMAJI_MAP[str] ?: str)
            }
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
    private val DEVANAGARI_REPLACING_VOWEL_SIGNS =
        setOf("ा", "ि", "ी", "ु", "ू", "ृ", "े", "ै", "ो", "ौ")
    private val DEVANAGARI_CONSONANTS =
        setOf(
            "क",
            "ख",
            "ग",
            "घ",
            "ङ",
            "च",
            "छ",
            "ज",
            "झ",
            "ञ",
            "ट",
            "ठ",
            "ड",
            "ढ",
            "ण",
            "त",
            "थ",
            "द",
            "ध",
            "न",
            "प",
            "फ",
            "ब",
            "भ",
            "म",
            "य",
            "र",
            "ल",
            "व",
            "श",
            "ष",
            "स",
            "ह",
            "ळ",
            "क्ष",
            "त्र",
            "ज्ञ",
            "श्र",
        )
    private val DEVANAGARI_VIRAMA = "्"

    suspend fun romanizeHindi(text: String): String =
        withContext(Dispatchers.Default) {
            val sb = StringBuilder()
            var i = 0
            while (i < text.length) {
                var consumed = false
                // Try multi-char match first: 3-char conjuncts (क्ष, त्र, ज्ञ, श्र),
                // then 2-char Nukta forms. Fall through to single-char for inherent vowel.
                for (len in 3 downTo 2) {
                    if (i + len <= text.length) {
                        val substr = text.substring(i, i + len)
                        if (DEVANAGARI_ROMAJI_MAP.containsKey(substr)) {
                            sb.append(DEVANAGARI_ROMAJI_MAP[substr])
                            i += len
                            consumed = true

                            // Inherent 'a' after consonant-ending conjunct
                            // (e.g. क्षमा → kshamaa, not kshmaa)
                            val lastChar = substr.last().toString()
                            if (lastChar in DEVANAGARI_CONSONANTS) {
                                val nextChar =
                                    if (i < text.length) text[i].toString() else null
                                if (
                                    nextChar != null &&
                                        nextChar !in DEVANAGARI_REPLACING_VOWEL_SIGNS &&
                                        nextChar != DEVANAGARI_VIRAMA &&
                                        (nextChar in DEVANAGARI_CONSONANTS ||
                                            nextChar == "ं" ||
                                            nextChar == "ः" ||
                                            nextChar == "ँ")
                                ) {
                                    sb.append("a")
                                }
                            }

                            break
                        }
                    }
                }
                if (!consumed) {
                    val char = text[i]
                    val str = char.toString()
                    val mapped = DEVANAGARI_ROMAJI_MAP[str] ?: str
                    sb.append(mapped)
                    if (str in DEVANAGARI_CONSONANTS) {
                        val nextChar = if (i + 1 < text.length) text[i + 1].toString() else null
                        if (
                            nextChar != null &&
                                nextChar !in DEVANAGARI_REPLACING_VOWEL_SIGNS &&
                                nextChar != DEVANAGARI_VIRAMA &&
                                (nextChar in DEVANAGARI_CONSONANTS ||
                                    nextChar == "ं" ||
                                    nextChar == "ः" ||
                                    nextChar == "ँ")
                        ) {
                            sb.append("a")
                        }
                    }
                    i++
                }
            }
            sb.toString()
        }

    // Punjabi romanization
    private val GURMUKHI_REPLACING_VOWEL_SIGNS = setOf("ਾ", "ਿ", "ੀ", "ੁ", "ੂ", "ੇ", "ੈ", "ੋ", "ੌ")
    private val GURMUKHI_CONSONANTS =
        setOf(
            "ਸ",
            "ਹ",
            "ਕ",
            "ਖ",
            "ਗ",
            "ਘ",
            "ਙ",
            "ਚ",
            "ਛ",
            "ਜ",
            "ਝ",
            "ਞ",
            "ਟ",
            "ਠ",
            "ਡ",
            "ਢ",
            "ਣ",
            "ਤ",
            "ਥ",
            "ਦ",
            "ਧ",
            "ਨ",
            "ਪ",
            "ਫ",
            "ਬ",
            "ਭ",
            "ਮ",
            "ਯ",
            "ਰ",
            "ਲ",
            "ਵ",
            "ੜ",
            "ਸ਼",
            "ਖ਼",
            "ਗ਼",
            "ਜ਼",
            "ਫ਼",
            "ਲ਼",
        )
    private val GURMUKHI_VIRAMA = "੍"

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

                        // Inherent 'a' after nukta-combined consonant
                        // (e.g. ਸ਼ਕ → shak, not shk)
                        val firstChar = twoChar.first().toString()
                        if (firstChar in GURMUKHI_CONSONANTS) {
                            val nextChar =
                                if (i < text.length) text[i].toString() else null
                            if (
                                nextChar != null &&
                                    nextChar !in GURMUKHI_REPLACING_VOWEL_SIGNS &&
                                    nextChar != GURMUKHI_VIRAMA &&
                                    (nextChar in GURMUKHI_CONSONANTS ||
                                        nextChar == "ੰ" ||
                                        nextChar == "ਂ" ||
                                        nextChar == "ਁ")
                            ) {
                                sb.append("a")
                            }
                        }
                    }
                }
                if (!consumed) {
                    val char = text[i]
                    val str = char.toString()
                    val mapped = GURMUKHI_ROMAJI_MAP[str] ?: str
                    sb.append(mapped)
                    if (str in GURMUKHI_CONSONANTS) {
                        val nextChar = if (i + 1 < text.length) text[i + 1].toString() else null
                        if (
                            nextChar != null &&
                                nextChar !in GURMUKHI_REPLACING_VOWEL_SIGNS &&
                                nextChar != GURMUKHI_VIRAMA &&
                                (nextChar in GURMUKHI_CONSONANTS ||
                                    nextChar == "ੰ" ||
                                    nextChar == "ਂ" ||
                                    nextChar == "ਁ")
                        ) {
                            sb.append("a")
                        }
                    }
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
        if (isJapanese(text)) return false
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
            // Japanese text with kana → always treat as Japanese
            "Japanese" in enabledLanguages && isJapanese(text) -> romanizeJapanese(text)
            "Korean" in enabledLanguages && isKorean(text) -> romanizeKorean(text)
            // Pure CJK (no kana): route to Japanese IPADIC readings first.
            // The bundled dictionary covers common Japanese kanji. Chinese text
            // not in the IPADIC passes through unchanged — the user can enable
            // Chinese-only mode for pinyin output.
            "Japanese" in enabledLanguages &&
                text.any { it in '\u4E00'..'\u9FFF' || it in '\u3400'..'\u4DBF' } ->
                romanizeJapanese(text)
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
