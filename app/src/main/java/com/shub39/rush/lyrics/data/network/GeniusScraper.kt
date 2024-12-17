package com.shub39.rush.lyrics.data.network

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import com.shub39.rush.core.data.safeCall
import com.shub39.rush.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

// thanks to @MainPandaHoon on tg 🙏🙏🙏
class GeniusScraper(
    private val client: HttpClient
) {
    suspend fun scrapeLyrics(songUrl: String): String? {

        val response = safeCall<HttpResponse> {
            client.get(
                urlString = songUrl
            )
        }

        when (response) {
            is Result.Success -> {
                val doc = Ksoup.parse(response.data.body())
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

            is Result.Error -> {
                return null
            }
        }
    }
}