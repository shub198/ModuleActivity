package com.rajotiyapawan.pokedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajotiyapawan.network.ApiResponse
import com.rajotiyapawan.network.NetworkRepository
import com.rajotiyapawan.network.POKE_BaseUrl
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.utility.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokeViewModel : ViewModel() {

    init {
        getPokemonData()
    }

    private var _pokemonData = MutableStateFlow<UiState<PokemonData>>(UiState.Idle)
    val pokemonData = _pokemonData.asStateFlow()
    fun getPokemonData() {
        viewModelScope.launch {
            when (val response = NetworkRepository.get<PokemonData>("${POKE_BaseUrl}pokemon/milotic")) {
                is ApiResponse.Error -> {}
                is ApiResponse.Success<PokemonData> -> {
                    _pokemonData.value = UiState.Success(response.data)
                }
            }
        }
    }
}