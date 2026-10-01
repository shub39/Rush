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
import com.shub39.rush.shared.core.Result
import com.shub39.rush.shared.logic.network.GeniusApi
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

class GeniusApiTest {
    val api = GeniusApi()

    @Test
    fun testApi() = runBlocking {
        val result = api.geniusSearch("Guest House Daughters")
        println(result)
        assert(result is Result.Success)
    }

    @Test
    fun testSongFetch() = runBlocking {
        val result = api.getGeniusLyrics(378195)
        println(result)
        assert(result is Result.Success)
    }
}
