package com.example.atvroomandroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atvroomandroid.data.AppDatabase
import com.example.atvroomandroid.data.MovieEntity
import com.example.atvroomandroid.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SyncState {
    data object Idle : SyncState
    data object Loading : SyncState
    data class Error(val message: String) : SyncState
}

class MovieViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MovieRepository(
        AppDatabase.getInstance(app).movieDao()
    )

    val movies: StateFlow<List<MovieEntity>> = repository.observeMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    init {
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            _syncState.value = try {
                repository.syncFromApi()
                SyncState.Idle
            } catch (e: Exception) {
                SyncState.Error(e.message ?: "Erro ao sincronizar")
            }
        }
    }
}
