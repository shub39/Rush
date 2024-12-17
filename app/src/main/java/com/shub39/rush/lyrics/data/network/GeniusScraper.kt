package com.shub39.rush.lyrics.data.network

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import okhttp3.OkHttpClient
import okhttp3.Request

// thanks to @MainPandaHoon on tg 🙏🙏🙏
class GeniusScraper(
    private val client: OkHttpClient
) {
    fun scrapeLyrics(songUrl: String): String? {
        val request = Request.Builder()
            .url(songUrl)
            .build()

        val responseBody = client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            response.body?.string() ?: return null
        }

        val doc = Ksoup.parse(responseBody)
        val lyricsElements = doc.select("div[data-lyrics-container='true']")
        val lyrics = StringBuilder()

        lyricsElements.forEach { element ->
            element.childNodes().forEach { node ->
                when (node) {
                    is TextNode -> {
                        val text = node.text().trim()
                        if (text.isNotEmpty()) {
                            if (text.startsWith("[") || text.endsWith("]")) {
                                lyrics.append("\n")
                            }
                            lyrics.append("\n$text")
                        }
                    }

                    is Element -> {
                        val nestedText = node.wholeText().trim()
                        if (nestedText.isNotEmpty()) {
                            if (nestedText.startsWith("[") || nestedText.endsWith("]")) {
                                lyrics.append("\n")
                            }
                            lyrics.append("\n$nestedText")
                        }
                    }
                }
            }
        }

        return lyrics.toString()
    }
}