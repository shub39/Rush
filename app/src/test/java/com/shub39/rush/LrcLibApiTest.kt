package com.shub39.rush

import com.shub39.rush.lyrics.LyricsFetcher
import org.junit.Test

class LrcLibApiTest {
    @Test
    fun getLyrics() {
        val result = LyricsFetcher.getLrcLibLyrics("clown", "Korn")
        println(result)
    }

    @Test
    fun getSearchResults() {
        val result = LyricsFetcher.getLrcLibSearchResults("clown")
        println(result)
    }
}