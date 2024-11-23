package com.shub39.rush

import com.shub39.rush.lyrics.data.network.SongProvider
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ApiTest {
    @Test
    fun getSearchResults() {
        val result = SongProvider.geniusSearch("Cut Throat Death Grips")
        println(result)
    }

    @Test
    fun getSong() {
        val result = SongProvider.fetchLyrics(1977140)
        println(result)
    }

    @Test
    fun getLrc() {
        println(SongProvider.lrcLibSearch("lil boy", "death"))
    }
}