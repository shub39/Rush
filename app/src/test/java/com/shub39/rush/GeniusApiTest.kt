package com.shub39.rush

import com.shub39.rush.network.SongProvider
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GeniusApiTest {
    @Test
    fun getSearchResults() {
        val result = SongProvider.search("Cut Throat Death Grips")
        println(result)
    }

    @Test
    fun getSong() {
        val result = SongProvider.fetchLyrics(1977139)
        println(result)
    }
}