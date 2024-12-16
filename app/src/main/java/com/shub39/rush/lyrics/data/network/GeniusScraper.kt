package com.shub39.rush.lyrics.data.network

import com.fleeksoft.ksoup.Ksoup
import okhttp3.OkHttpClient
import okhttp3.Request

class GeniusScraper(
    private val client: OkHttpClient
) {
    fun scrapeLyrics(songUrl: String): String {
        val request = Request.Builder()
            .url(songUrl)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return ""

        val doc = Ksoup.parse(responseBody)
        val lyricsElement = doc.select("div[data-lyrics-container='true']") // this is it!! gotta learn scraping

        return lyricsElement.html()
    }
}