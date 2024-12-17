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
                val lyricsElements = Ksoup.parse(response.data.body()).select("div[data-lyrics-container='true']")

                return buildString {
                    lyricsElements.forEach { element ->
                        element.childNodes().forEachIndexed { _, node ->
                            val text = when (node) {
                                is TextNode -> node.text()
                                is Element -> node.wholeText()
                                else -> return@forEachIndexed
                            }.trim()

                            if (text.isNotEmpty()) {
                                if (text.startsWith("[") || text.endsWith("]")) {
                                    append("\n")
                                }
                                append("\n$text")
                            }
                        }
                    }
                }
            }

            is Result.Error -> return null
        }
    }
}