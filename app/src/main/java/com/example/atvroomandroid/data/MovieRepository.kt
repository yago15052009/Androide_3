package com.example.atvroomandroid.data

import com.example.atvroomandroid.network.GhibliApi
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val dao: MovieDao,
    private val api: GhibliApi = GhibliApi.instance
) {
    fun observeMovies(): Flow<List<MovieEntity>> = dao.observeAll()

    suspend fun syncFromApi() {
        val movies = api.getFilms().map {
            MovieEntity(
                id = it.id,
                title = it.title,
                description = it.description,
                director = it.director,
                releaseDate = it.releaseDate,
                rtScore = it.rtScore
            )
        }
        dao.upsertAll(movies)
    }
}
