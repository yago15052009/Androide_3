package com.example.atvapis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atvapis.model.Character
import com.example.atvapis.network.RickAndMortyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface CharacterState {
    data object Idle : CharacterState
    data object Loading : CharacterState
    data class Success(
        val characters: List<Character>,
        val currentPage: Int,
        val totalPages: Int
    ) : CharacterState
    data class Error(val message: String) : CharacterState
}

class CharacterViewModel : ViewModel() {

    private val api = RickAndMortyApi.instance

    private val _state = MutableStateFlow<CharacterState>(CharacterState.Idle)
    val state: StateFlow<CharacterState> = _state

    private var currentQuery = ""
    private var currentPage = 1
    private var totalPages = 1

    init {
        search()
    }

    fun search(name: String = "", page: Int = 1) {
        currentQuery = name
        currentPage = page
        viewModelScope.launch {
            _state.value = CharacterState.Loading
            _state.value = try {
                val response = api.getCharacters(name = name, page = page)
                totalPages = response.info.pages
                CharacterState.Success(
                    characters = response.results,
                    currentPage = page,
                    totalPages = totalPages
                )
            } catch (e: Exception) {
                CharacterState.Error(
                    if (e.message?.contains("404") == true) "Nenhum personagem encontrado."
                    else "Erro de conexão. Verifique sua internet."
                )
            }
        }
    }

    fun nextPage() {
        val s = _state.value
        if (s is CharacterState.Success && s.currentPage < s.totalPages) {
            search(currentQuery, s.currentPage + 1)
        }
    }

    fun prevPage() {
        val s = _state.value
        if (s is CharacterState.Success && s.currentPage > 1) {
            search(currentQuery, s.currentPage - 1)
        }
    }
}
