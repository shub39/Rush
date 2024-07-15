package com.shub39.rush.genius

import android.util.Log
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
                var lyrics = ""
                val lyricsElement = document.select("div.lyrics, div[class*='Lyrics__Container']")
                lyricsElement.forEach {
                    lyrics += formatLyrics(it.wholeText())
                    lyrics += "\n"
                }
                Log.d("Scraper", document.toString())
                lyrics
            }
        }
    }

    private fun formatLyrics(rawLyrics: String): String {
        return rawLyrics.lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .replace("[", "\n[")
            .removePrefix("\n")
    }

}

