package com.shub39.rush.lyrics

import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.shub39.rush.database.LrcLibSong
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object LyricsFetcher {

    private const val TAG = "LyricsFetcher"
    private const val LRC_BASE_URL = "https://lrclib.net/"

    private val apiService: LrcLibApiService

    init {
        val client = OkHttpClient.Builder()
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(LRC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        apiService = retrofit.create(LrcLibApiService::class.java)
    }

    fun scrapeLyrics(songUrl: String): String? {
        val (_, _, result) = songUrl.httpGet().responseString()

        return when (result) {
            is Result.Failure -> {
                Log.e("Scraper", "Error fetching lyrics: ${result.error}")
                return null
            }

            is Result.Success -> {
                val html = result.get()
                val document = Jsoup.parse(html)
                var lyrics = ""
                val lyricsElement = document.select("div.lyrics, div[class*='Lyrics__Container']")
                lyricsElement.forEach {
                    lyrics += formatGeniusLyrics(it.wholeText())
                    lyrics += "\n"
                }

                lyrics
            }
        }
    }

    fun getLrcLibLyrics(
        trackName: String,
        artistName: String
    ): Pair<String, String?>? {
        val response = apiService.getLrcLyrics(trackName, artistName).execute()

        if (response.isSuccessful) {
            val jsonSong = response.body()
            if (!jsonSong.isNullOrEmpty()) {
                val lyrics = jsonSong[0].plainLyrics ?: jsonSong[1].plainLyrics ?: ""
                val syncedLyrics = jsonSong[0].syncedLyrics ?: if (jsonSong.size > 1) jsonSong[1].syncedLyrics else null
                return Pair(lyrics, syncedLyrics)
            }
        }
        
        return null
    }

    private fun formatGeniusLyrics(rawLyrics: String): String {
        return rawLyrics.lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .replace("[", "\n[")
            .removePrefix("\n")
    }

    interface LrcLibApiService {
        @GET("api/search")
        fun getLrcLyrics(
            @Query("track_name") trackName: String,
            @Query("artist_name") artistName: String
        ) : Call<List<LrcLibSong>>
    }

}