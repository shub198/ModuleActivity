package com.pokemonApp.pokedex.model

data class PokemonListData(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NameItem>?
)

data class NameItem(
    val name: String?,
    val url: String?
)

data class PokemonBasicInfo(
    val id: Int,
    val imageUrl: String,
    val types: List<String>
)
