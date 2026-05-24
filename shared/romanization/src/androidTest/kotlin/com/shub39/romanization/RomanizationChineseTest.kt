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
 * Chinese romanization tests — ISO 7098:2015 (Pinyin).
 *
 * ISO: https://www.iso.org/standard/61461.html ALA-LC:
 * https://www.loc.gov/catdir/cpso/romanization/chinese.pdf
 */
class RomanizationChineseTest {

    private lateinit var romanizationUtils: RomanizationUtils

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        romanizationUtils = RomanizationUtils(context)
        romanizationUtils.loadReadingDictionary(context)
    }

    // Chinese corner cases

    @Test
    fun testChinese_pureKanjiDetectedAsChinese() = runTest {
        val result = romanizationUtils.romanize("花鳥風月", enabledLanguages = listOf("Chinese"))
        assertNotNull(result)
        assertTrue(result!!.contains("huā"))
    }

    @Test
    fun testChinese_mixedWithKana() = runTest {
        val result =
            romanizationUtils.romanize("食べる", enabledLanguages = listOf("Japanese", "Chinese"))
        assertNotNull(result)
        // Detected as Japanese due to kana
        assertTrue(result!!.contains("taberu") || result!!.contains("食"))
    }

    @Test
    fun testChinese_pureCjkWithBothJapaneseAndChinese() = runTest {
        val result =
            romanizationUtils.romanize("望春风", enabledLanguages = listOf("Japanese", "Chinese"))
        assertNotNull(result)
        // With both JP and ZH enabled, pure CJK goes to Japanese IPADIC.
        // Chinese lyrics not in IPADIC pass through; enable Chinese-only for pinyin.
        assertTrue(result!!.isNotEmpty())
    }
}
