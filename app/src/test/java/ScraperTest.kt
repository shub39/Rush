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
import com.shub39.rush.data.network.GeniusScraper
import com.shub39.rush.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ScraperTest {
    val scraper = GeniusScraper()

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @Test
    fun testScrape() =
        testIn("Scrape test") {
            val lyrics = scraper.geniusScrape("https://genius.com/Haddaway-what-is-love-lyrics")

            println(lyrics)

            when (lyrics) {
                is Result.Error -> {
                    println(lyrics.message)
                }
                is Result.Success -> {
                    println(lyrics.data)
                }
            }
        }
}
