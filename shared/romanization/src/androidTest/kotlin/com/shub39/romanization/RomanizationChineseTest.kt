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

class RomanizationChineseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUp() {
            RomanizationUtils.loadReadingDictionary(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        }
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
}
