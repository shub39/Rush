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

class RomanizationChineseTest {

    private val chineseTransliterator by lazy { Transliterator.getInstance("Han-Latin") }

    private suspend fun romanizeChinese(text: String): String {
        return chineseTransliterator.transliterate(text).lowercase(Locale.ROOT)
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
}
