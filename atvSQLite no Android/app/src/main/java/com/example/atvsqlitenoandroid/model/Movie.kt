package com.example.atvsqlitenoandroid.model

data class Movie(
    val id: Long = 0,
    val title: String,
    val director: String,
    val year: Int,
    val rating: Float,
    val notes: String
)
