package com.shub39.rush

import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.lyrics.data.network.GeniusApi
import com.shub39.rush.lyrics.data.network.GeniusScraper
import com.shub39.rush.lyrics.data.network.LrcLibApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ApiTest {
    private val client = HttpClientFactory.create()

    @Test
    fun getGeniusSearchResults() = runBlocking {
        val api = GeniusApi(client)
        val result = api.geniusSearch("Satan in the wait")
        println(result)
    }

    @Test
    fun getGeniusSong() = runBlocking {
        val api = GeniusApi(client)
        val result = api.geniusSong(3836181)
        println(result)
    }

    @Test
    fun getLrc() = runBlocking {
        val api = LrcLibApi(client)
        val result = api.getLrcLyrics("Little lamb", "sematary")
        println(result)
    }

    @Test
    fun searchLrc() = runBlocking {
        val api = LrcLibApi(client)
        val result = api.searchLrcLyrics("talk talk", "charli xcx")
        println(result)
    }

    @Test
    fun scrape() = runBlocking {
        val scraper = GeniusScraper(client)
        val lyrics = scraper.scrapeLyrics("https://genius.com/Lana-del-rey-bluebird-lyrics")
        println(lyrics)
    }
}