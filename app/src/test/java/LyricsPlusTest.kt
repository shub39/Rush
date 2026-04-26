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
import com.shub39.rush.data.network.LyricsPlusApi
import com.shub39.rush.domain.dataclasses.TTMLParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LyricsPlusTest {
    private val lyricsPlusApi = LyricsPlusApi()

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @Test
    fun testFetch() =
        testIn("Test Fetch") {
            val ttml = lyricsPlusApi.fetchTTML("Snowchild", "the weeknd")
            val parsedLines = TTMLParser.parseTTML(ttml ?: "")
            parsedLines.forEach { println(it) }
            println(TTMLParser.isValidTTML(ttml ?: ""))
        }

    @Test
    fun testConvertToLRC() =
        testIn("Test Convert to LRC") {
            val ttml = lyricsPlusApi.fetchTTML("Dominator", "clipping.")
            val parsedLines = TTMLParser.parseTTML(ttml ?: "")
            parsedLines.forEach { println(it) }
            println(TTMLParser.isValidTTML(ttml ?: ""))
        }
}
