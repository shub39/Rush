package com.shub39.rush.genius

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    fun search(@Query("q") query: String): Call<JsonElement>

    @GET("songs/{songId}")
    fun getSong(@Path("songId") songId: Long): Call<JsonElement>
}