package com.shub39.rush

import com.shub39.rush.lyrics.SongProvider
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GeniusApiTest {
    @Test
    fun getSearchResults() {
        val result = SongProvider.search("Hello")
        println(result)
    }
}