package com.shub39.rush.genius

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.jsoup.Jsoup

object Scraper {

    fun scrapeLyrics(songUrl: String): String {
        val (_, _, result) = songUrl.httpGet().responseString()

        return when (result) {
            is Result.Failure -> {
                "Network Error: ${result.error.message}"
            }

            is Result.Success -> {
                val html = result.get()
                val document = Jsoup.parse(html)

                val lyricsElement = document.select("div.lyrics, div[class*='Lyrics__Container']").first()

                if (lyricsElement != null) {
                    formatLyrics(lyricsElement.wholeText())
                } else {
                    "Lyrics not found."
                }
            }
        }
    }

    private fun formatLyrics(rawLyrics: String): String {
        return rawLyrics.lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .replace("[", "\n\n[")
            .removePrefix("\n\n")
    }

}

