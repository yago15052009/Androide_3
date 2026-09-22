package com.example.atvsqlitenoandroid.repository

import android.content.ContentValues
import android.content.Context
import com.example.atvsqlitenoandroid.database.MovieDbHelper
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.COL_DIR
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.COL_ID
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.COL_NOTES
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.COL_RATING
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.COL_TITLE
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.COL_YEAR
import com.example.atvsqlitenoandroid.database.MovieDbHelper.Companion.TABLE_MOVIES
import com.example.atvsqlitenoandroid.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(context: Context) {

    private val dbHelper = MovieDbHelper(context)

    suspend fun insert(movie: Movie) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(COL_TITLE, movie.title)
            put(COL_DIR, movie.director)
            put(COL_YEAR, movie.year)
            put(COL_RATING, movie.rating)
            put(COL_NOTES, movie.notes)
        }
        dbHelper.writableDatabase.insert(TABLE_MOVIES, null, values)
    }

    suspend fun getAll(): List<Movie> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_MOVIES, null, null, null, null, null, "$COL_ID DESC")
        val movies = mutableListOf<Movie>()
        cursor.use {
            while (it.moveToNext()) {
                movies += Movie(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                    director = it.getString(it.getColumnIndexOrThrow(COL_DIR)),
                    year = it.getInt(it.getColumnIndexOrThrow(COL_YEAR)),
                    rating = it.getFloat(it.getColumnIndexOrThrow(COL_RATING)),
                    notes = it.getString(it.getColumnIndexOrThrow(COL_NOTES))
                )
            }
        }
        movies
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        dbHelper.writableDatabase.delete(TABLE_MOVIES, "$COL_ID = ?", arrayOf(id.toString()))
    }
}
