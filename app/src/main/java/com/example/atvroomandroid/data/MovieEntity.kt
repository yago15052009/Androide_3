package com.example.atvroomandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val director: String,
    val releaseDate: String,
    val rtScore: String
)
