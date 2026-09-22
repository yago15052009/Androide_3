package com.example.atvroomandroid.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class GhibliMovie(
    val id: String,
    val title: String,
    val description: String,
    val director: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("rt_score") val rtScore: String
)

interface GhibliApi {
    @GET("films")
    suspend fun getFilms(): List<GhibliMovie>

    companion object {
        val instance: GhibliApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://ghibliapi.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GhibliApi::class.java)
        }
    }
}
