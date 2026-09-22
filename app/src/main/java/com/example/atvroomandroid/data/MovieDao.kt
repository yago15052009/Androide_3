package com.example.atvroomandroid.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY releaseDate ASC")
    fun observeAll(): Flow<List<MovieEntity>>

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)
}
