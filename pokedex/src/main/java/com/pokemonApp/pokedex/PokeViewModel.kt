package com.pokemonApp.pokedex

import android.graphics.Rect
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemonApp.network.ApiResponse
import com.pokemonApp.network.NetworkRepository
import com.pokemonApp.network.POKE_BaseUrl
import com.pokemonApp.pokedex.model.AbilityEffect
import com.pokemonApp.pokedex.model.NameItem
import com.pokemonApp.pokedex.model.PokedexUserEvent
import com.pokemonApp.pokedex.model.PokemonAbilityDto
import com.pokemonApp.pokedex.model.PokemonAbout
import com.pokemonApp.pokedex.model.PokemonAboutDto
import com.pokemonApp.pokedex.model.PokemonBasicInfo
import com.pokemonApp.pokedex.model.PokemonData
import com.pokemonApp.pokedex.model.PokemonEvolution
import com.pokemonApp.pokedex.model.PokemonEvolutionDto
import com.pokemonApp.pokedex.model.PokemonListData
import com.pokemonApp.pokedex.model.toChain
import com.pokemonApp.pokedex.utility.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class PokeViewModel : ViewModel() {

    private var _userEvent = MutableSharedFlow<PokedexUserEvent>()
    val userEvent = _userEvent

    private var _pokemonList: MutableStateFlow<UiState<PokemonListData>> = MutableStateFlow(UiState.Idle)
    val pokemonList = _pokemonList.asStateFlow()

    // details for animation
    var selectedItemBounds by mutableStateOf<Rect?>(null)
    var selectedItemOffset by mutableStateOf(Offset.Zero)

    // Cache detail per Pokémon name
    private val _pokemonDetails = mutableStateMapOf<String, PokemonBasicInfo>()
    val pokemonDetails = _pokemonDetails
    private val _pokemonCache = mutableMapOf<String, PokemonData>()
    val pokemonCache = _pokemonCache

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    private var _searchResults = MutableStateFlow<List<NameItem>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    init {
        getPokemonList()
        initializeSearch()
//        getPokemonData()
    }

    fun sendUserEvent(event: PokedexUserEvent) {
        viewModelScope.launch {
            _userEvent.emit(event)
        }
    }

    private fun initializeSearch() {
        viewModelScope.launch {
            _query.debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    when (val response = pokemonList.value) {
                        is UiState.Success -> {
                            response.data.results?.let { results ->
                                _searchResults.value = results.filter { it.name?.contains(query) == true || query.isEmpty() }
                            }
                        }

                        else -> {}
                    }
                }
        }
    }

    private var _pokemonData = MutableStateFlow<UiState<PokemonData>>(UiState.Idle)
    val pokemonData = _pokemonData.asStateFlow()
    fun getPokemonData(item: NameItem) {
        val name = item.name?.lowercase()

        // Use cached data if available
        val cached = _pokemonCache[name]
        if (cached != null) {
            _pokemonData.value = UiState.Success(cached)
            return
        }
        viewModelScope.launch {
            when (val response = NetworkRepository.get<PokemonData>("https://pokeapi.co/api/v2/pokemon/${name}")) {
                is ApiResponse.Error -> {}
                is ApiResponse.Success<PokemonData> -> {
                    _pokemonData.value = UiState.Success(response.data)
                    _pokemonCache[name ?: ""] = response.data
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
                    _searchResults.value = response.data.results ?: emptyList()
                }

                is ApiResponse.Error -> {}
            }
        }
    }

    fun fetchBasicDetail(item: NameItem?) {
        if (_pokemonDetails.containsKey(item?.name)) return // already fetched

        viewModelScope.launch {
            delay(100L) // Give some time between requests
            val response = NetworkRepository.get<PokemonData>(item?.url ?: "")
            if (response is ApiResponse.Success) {
                val detail = response.data
                item?.name?.let { name ->
                    _pokemonDetails[name] = PokemonBasicInfo(
                        id = detail.id ?: 0,
                        imageUrl = detail.sprites?.other?.officialArtwork?.frontDefault ?: "",
                        types = detail.types?.map { it.type?.name ?: "" } ?: listOf()
                    )
                }
            } else if (response is ApiResponse.Error) {
                Log.e("FetchError", "Failed for ${item?.name}: ${response.message}")
            }
        }
    }
    fun fetchBasicDetailByName(name: String?) {
        if (name == null) return
        if (_pokemonDetails.containsKey(name)) return // already fetched

        viewModelScope.launch {
            delay(100L) // Give some time between requests
            val response = NetworkRepository.get<PokemonData>("https://pokeapi.co/api/v2/pokemon/$name")
            if (response is ApiResponse.Success) {
                val detail = response.data
                name?.let { name ->
                    _pokemonDetails[name] = PokemonBasicInfo(
                        id = detail.id ?: 0,
                        imageUrl = detail.sprites?.other?.officialArtwork?.frontDefault ?: "",
                        types = detail.types?.map { it.type?.name ?: "" } ?: listOf()
                    )
                }
            } else if (response is ApiResponse.Error) {
                Log.e("FetchError", "Failed for ${name}: ${response.message}")
            }
        }
    }

    private var _aboutData = MutableStateFlow(PokemonAbout.init())
    val aboutData get() = _aboutData
    fun fetchPokemonAbout(item: NameItem?) {
        viewModelScope.launch {
            delay(100L) // Give some time between requests
            val response = NetworkRepository.get<PokemonAboutDto>(item?.url ?: "")
            if (response is ApiResponse.Success) {
                val detail = response.data
                val femalePercentage = (detail.gender_rate / 8.0) * 100
                _aboutData.value = PokemonAbout(
                    flavourText = detail.flavor_text_entries
                        .firstOrNull { it.language.name == "en" && it.version.name == "ruby" }
                        ?.flavor_text
                        ?.replace("\n", " ") ?: "",
                    genus = detail.genera
                        .firstOrNull { it.language.name == "en" }
                        ?.genus ?: "",
                    growthRate = detail.growth_rate.name ?: "",
                    femalePercentage = femalePercentage,
                    malePercentage = 100 - femalePercentage,
                    baseFriendship = detail.base_happiness,
                    hatchCounter = detail.hatch_counter,
                    eggGroups = detail.egg_groups,
                    evolutionChain = detail.evolution_chain
                )
            } else if (response is ApiResponse.Error) {
                Log.e("FetchError", "Failed for ${item?.name}: ${response.message}")
            }
        }
    }

    // Cache detail per Pokémon name
    private val _abilityDetails = mutableStateMapOf<String, AbilityEffect>()
    val abilityDetails: Map<String, AbilityEffect> get() = _abilityDetails

    fun getAbilityEffect(item: NameItem?) {
        if (_abilityDetails.containsKey(item?.name)) return // already fetched

        viewModelScope.launch {
            delay(100L) // Give some time between requests
            val response = NetworkRepository.get<PokemonAbilityDto>(item?.url ?: "")
            if (response is ApiResponse.Success) {
                val detail = response.data
                item?.name?.let { name ->
                    _abilityDetails[name] = AbilityEffect(
                        effect = detail.effect_entries
                            .firstOrNull { it.language.name == "en" && it.effect.isNotBlank()}
                            ?.effect ?: "",
                        short_effect = detail.effect_entries
                            .firstOrNull { it.language.name == "en" && it.short_effect.isNotBlank()}
                            ?.short_effect ?: "",
                        flavor_text = detail.flavor_text_entries
                            .lastOrNull { it.language.name == "en" }
                            ?.flavor_text
                            ?.replace("\n", " ") ?: "",
                        language = NameItem("", "")
                    )
                }
            } else if (response is ApiResponse.Error) {
                Log.e("FetchError", "Failed for ${item?.name}: ${response.message}")
            }
        }
    }

    private val _evolutionChain = MutableStateFlow(PokemonEvolution(null))
    val evolutionChain = _evolutionChain.asStateFlow()

    fun getEvolutionChain(item: NameItem?) {
        viewModelScope.launch {
            val response = NetworkRepository.get<PokemonEvolutionDto>(item?.url ?: "")
            if (response is ApiResponse.Success) {
                val detail = response.data
                _evolutionChain.value = PokemonEvolution(
                    chain = detail.chain?.toChain()
                )
            } else if (response is ApiResponse.Error) {
                Log.e("FetchError", "Failed for ${item?.name}: ${response.message}")
            }
        }
    }


    fun toggleFavourites(item: NameItem) {

    }
}