package com.rajotiyapawan.pokedex.model

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
