package com.shub39.rush.core.data.network

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import com.shub39.rush.core.data.safeCall
import com.shub39.rush.core.domain.Result
import com.shub39.rush.core.domain.SourceError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

// thanks to https://github.com/imjyotiraditya/genius-lyrics-cli and https://github.com/rramiachraf/dumb
class GeniusScraper(
    private val client: HttpClient
) {
    companion object {
        val dumbInstances = listOf(
            "dumb.ducks.party/",
            "dumb.lunar.icu/",
            "dumb.bloat.cat/",
            "dumb.jeikobu.net/"
        )

        val nonLyricsRegex = listOf(
            Regex("^\\d+Contributors.*"),
            Regex("^\\d+ Contributor.*"),
            Regex("(?i)^Translations.*"),
            Regex("(?i)^Read More.*"),
            Regex("(?i)^.*Lyrics$")
        )
    }

    suspend fun geniusScrape(songUrl: String): Result<String, SourceError> {
        val response = safeCall<HttpResponse> {
            client.get(
                urlString = songUrl
            )
        }

        when (response) {
            is Result.Success -> {
                val lyricsElements = Ksoup.parse(response.data.body<String>())
                    .select("div[data-lyrics-container='true']")

                val data = buildString {
                    lyricsElements.forEach { element ->
                        element.childNodes().forEach { node ->
                            val text = when (node) {
                                is TextNode -> node.text()
                                is Element -> node.wholeText()
                                else -> return@forEach
                            }.trim()

                            if (text.isBlank()) return@forEach

                            if (nonLyricsRegex.any { it.matches(text) }) return@forEach

                            if (text.isNotEmpty()) {
                                if (text.startsWith("[") || text.endsWith("]")) {
                                    append("\n")
                                }
                                append("\n$text")
                            }
                        }
                    }
                }

                return Result.Success(data)
            }

            is Result.Error -> {
                var error = Result.Error<String, SourceError>(error = response.error, debugMessage = response.debugMessage)

                dumbInstances.forEach { instance ->
                    when (val result = dumbScrape(songUrl.replace("genius.com/", instance))) {
                        is Result.Error -> error = result
                        is Result.Success -> return result
                    }
                }

                return error
            }
        }
    }

    suspend fun dumbScrape(songUrl: String): Result<String, SourceError> {
        val response = safeCall<HttpResponse> {
            client.get(
                urlString = songUrl
            )
        }

        return when (response) {
            is Result.Success -> {
                val lyricsElements = Ksoup.parse(response.data.body<String>()).select("#lyrics")

                val data = buildString {
                    lyricsElements.forEach { element ->
                        element.childNodes().forEach { node ->
                            val text = when (node) {
                                is TextNode -> node.text()
                                is Element -> if (node.tagName() == "br") "\n" else node.wholeText()
                                else -> return@forEach
                            }.trimEnd()

                            if (text.isNotEmpty()) {
                                if (text.trim() in listOf("(", ")")) return@forEach
                                if (text.startsWith("[") && text.endsWith("]")) {
                                    append("\n")
                                }
                                append("\n$text")
                            }
                        }
                    }
                }.trim()

                Result.Success(data)
            }

            is Result.Error -> Result.Error(error = response.error, debugMessage = response.debugMessage)
        }
    }
}