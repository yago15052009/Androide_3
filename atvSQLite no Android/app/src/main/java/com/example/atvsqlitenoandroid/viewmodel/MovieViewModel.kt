package com.example.atvsqlitenoandroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atvsqlitenoandroid.model.Movie
import com.example.atvsqlitenoandroid.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovieRepository(app)

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    init {
        load()
    }

    fun add(movie: Movie) {
        viewModelScope.launch {
            repository.insert(movie)
            load()
        }
    }

    fun remove(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
            load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _movies.value = repository.getAll()
        }
    }
}
