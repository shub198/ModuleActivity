package com.rajotiyapawan.pokedex

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajotiyapawan.network.ApiResponse
import com.rajotiyapawan.network.NetworkRepository
import com.rajotiyapawan.network.POKE_BaseUrl
import com.rajotiyapawan.pokedex.model.NameItem
import com.rajotiyapawan.pokedex.model.PokemonBasicInfo
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.model.PokemonListData
import com.rajotiyapawan.pokedex.utility.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokeViewModel : ViewModel() {

    private var _pokemonList: MutableStateFlow<UiState<PokemonListData>> = MutableStateFlow(UiState.Idle)
    val pokemonList = _pokemonList.asStateFlow()

    // Cache detail per Pokémon name
    private val _pokemonDetails = mutableStateMapOf<String, PokemonBasicInfo>()
    val pokemonDetails: Map<String, PokemonBasicInfo> get() = _pokemonDetails

    init {
        getPokemonList()
//        getPokemonData()
    }

    private var _pokemonData = MutableStateFlow<UiState<PokemonData>>(UiState.Idle)
    val pokemonData = _pokemonData.asStateFlow()
    fun getPokemonData(item: NameItem) {
        viewModelScope.launch {
            when (val response = NetworkRepository.get<PokemonData>(item.url ?: "")) {
                is ApiResponse.Error -> {}
                is ApiResponse.Success<PokemonData> -> {
                    _pokemonData.value = UiState.Success(response.data)
                }
            }
        }
    }

    private fun getPokemonList() {
        viewModelScope.launch {
            _pokemonList.value = UiState.Loading
            when (val response = NetworkRepository.get<PokemonListData>("${POKE_BaseUrl}pokemon?offset=0&limit=2000")) {
                is ApiResponse.Success -> {
                    _pokemonList.value = UiState.Success(response.data)
                }

                is ApiResponse.Error -> {}
            }
        }
    }

    fun fetchBasicDetail(item: NameItem) {
        if (_pokemonDetails.containsKey(item.name)) return // already fetched

        viewModelScope.launch {
            delay(100L) // Give some time between requests
            val response = NetworkRepository.get<PokemonData>(item.url ?: "")
            if (response is ApiResponse.Success) {
                val detail = response.data
                item.name?.let { name ->
                    _pokemonDetails[name] = PokemonBasicInfo(
                        id = detail.id ?: 0,
                        imageUrl = detail.sprites?.other?.officialArtwork?.frontDefault ?: "",
                        types = detail.types?.map { it.type?.name ?: "" } ?: listOf()
                    )
                }
            } else if (response is ApiResponse.Error) {
                Log.e("FetchError", "Failed for ${item.name}: ${response.message}")
            }
        }
    }

}