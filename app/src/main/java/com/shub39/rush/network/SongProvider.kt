package com.shub39.rush.network

import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.shub39.rush.database.LrcLibSong
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.Song
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import com.shub39.rush.error.Result
import com.shub39.rush.error.SourceError
import org.jsoup.Jsoup
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
Currently using Genius as a primary source of search results, the url provided is them scraped and the song title and artist is
used to get lrclib lyrics.
 */
object SongProvider {

    private const val TAG = "SongProvider"

    private const val BASE_URL = "https://api.genius.com/"
    private const val LRC_BASE_URL = "https://lrclib.net/"
    private const val AUTH_HEADER = "Authorization"
    private const val BEARER_TOKEN = "Bearer ${Tokens.GENIUS_API}"

    private val geniusApiService: GeniusApiService
    private val lrcApiService: LrcLibApiService

    init {

        val geniusClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader(AUTH_HEADER, BEARER_TOKEN)
                .build()
            chain.proceed(newRequest)
        }.build()

        val lrcClient = OkHttpClient.Builder()
            .build()

        val geniusRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(geniusClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val lrcRetrofit = Retrofit.Builder()
            .baseUrl(LRC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(lrcClient)
            .build()

        geniusApiService = geniusRetrofit.create(GeniusApiService::class.java)
        lrcApiService = lrcRetrofit.create(LrcLibApiService::class.java)

    }

    fun geniusSearch(query: String): Result<List<SearchResult>, SourceError> {
        return try {
            val response: Response<JsonElement> = geniusApiService.search(query).execute()

            if (response.isSuccessful) {
                val jsonHits = response.body()?.asJsonObject
                    ?.getAsJsonObject("response")
                    ?.getAsJsonArray("hits")
                    ?: return Result.Error(SourceError.Data.PARSE_ERROR)

                val results = jsonHits.mapNotNull {
                    try {
                        val jo = it.asJsonObject.getAsJsonObject("result")

                        val title = jo.get("title").asString
                        val artist = jo.getAsJsonObject("primary_artist").get("name").asString
                        val album = jo.getAsJsonObject("album")?.get("name")?.asString
                        val artUrl = jo.get("header_image_thumbnail_url").asString
                        val url = jo.get("url").asString
                        val id = jo.get("id").asLong

                        SearchResult(title, artist, album, artUrl, url, id)
                    } catch (e: Exception) {
                        Log.e(TAG, e.message, e)
                        null
                    }
                }

                if (results.isEmpty()) {
                    Result.Error(SourceError.Data.NO_RESULTS)
                } else {
                    Result.Success(results)
                }
            } else {
                Result.Error(SourceError.Network.REQUEST_FAILED)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            Result.Error(SourceError.Data.IO_ERROR)
        }
    }

    // provides lyrics too, slower than get
    fun lrcLibSearch(
        trackName: String,
        artistName: String = ""
    ): Result<List<LrcLibSong>, SourceError> {
        val response = lrcApiService.getSearchResults(trackName, artistName).execute()

        if (response.isSuccessful) {
            return when (response.body()) {
                null -> Result.Error(SourceError.Data.NO_RESULTS)
                else -> Result.Success(response.body()!!)
            }
        }

        return Result.Error(SourceError.Network.REQUEST_FAILED)
    }

    fun fetchLyrics(songId: Long): Result<Song, SourceError> {
        Log.i(TAG, "Fetching song $songId")

        return try {
            val response: Response<JsonElement> = geniusApiService.getSong(songId).execute()

            if (response.isSuccessful) {
                val jsonSong = response.body()?.asJsonObject
                    ?.getAsJsonObject("response")
                    ?.getAsJsonObject("song")
                    ?: return Result.Error(SourceError.Data.PARSE_ERROR)

                val title = jsonSong.get("title")?.asString ?: "Unknown Title"
                val artist = jsonSong.getAsJsonObject("primary_artist")?.get("name")?.asString
                    ?: "Unknown Artist"
                val sourceUrl = jsonSong.get("url")?.asString ?: ""
                val album = getAlbum(jsonSong)
                val artUrl = jsonSong.get("header_image_thumbnail_url")?.asString ?: ""
                val lrcLib = getLrcLibLyrics(title, artist) ?: Pair("", null)
                val lyrics = lrcLib.first
                val syncedLyrics = lrcLib.second
                val geniusLyrics = scrapeLyrics(sourceUrl)

                Result.Success(
                    Song(
                        songId,
                        title,
                        artist,
                        lyrics,
                        album,
                        sourceUrl,
                        artUrl,
                        syncedLyrics,
                        geniusLyrics
                    )
                )
            } else {
                Result.Error(SourceError.Network.REQUEST_FAILED)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            Result.Error(SourceError.Data.IO_ERROR)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            Result.Error(SourceError.Data.UNKNOWN)
        }
    }

    private fun getLrcLibLyrics(
        trackName: String,
        artistName: String
    ): Pair<String, String?>? {
        val response = lrcApiService.getLrcLyrics(trackName, artistName).execute()

        if (response.isSuccessful) {
            val jsonSong = response.body()

            if (jsonSong != null) {
                val lyrics = jsonSong.plainLyrics ?: ""
                val syncedLyrics = jsonSong.syncedLyrics
                return Pair(lyrics, syncedLyrics)
            }
        }

        return null
    }

    private fun scrapeLyrics(songUrl: String): String? {
        val (_, _, result) = songUrl.httpGet().responseString()

        return when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                Log.e(TAG, "Error scraping lyrics: ${result.error}")
                return null
            }

            is com.github.kittinunf.result.Result.Success -> {
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

    private fun getAlbum(jsonObject: JsonObject): String? {
        val albumJson = jsonObject["album"] ?: return null
        if (albumJson.isJsonNull) return null
        return albumJson.asJsonObject.get("name").asString
    }

    private fun formatGeniusLyrics(rawLyrics: String): String {
        return rawLyrics.lines()
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .replace("[", "\n[")
            .removePrefix("\n")
    }

    interface GeniusApiService {
        @GET("search")
        fun search(@Query("q") query: String): Call<JsonElement>

        @GET("songs/{songId}")
        fun getSong(@Path("songId") songId: Long): Call<JsonElement>
    }

    interface LrcLibApiService {
        @GET("api/get")
        fun getLrcLyrics(
            @Query("track_name") trackName: String,
            @Query("artist_name") artistName: String
        ): Call<LrcLibSong>

        @GET("api/search")
        fun getSearchResults(
            @Query("track_name") query: String,
            @Query("artist_name") artistName: String = ""
        ): Call<List<LrcLibSong>>
    }

}