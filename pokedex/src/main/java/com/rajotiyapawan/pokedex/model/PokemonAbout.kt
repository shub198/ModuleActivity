package com.rajotiyapawan.pokedex.model

data class PokemonAboutDto(
    val flavor_text_entries: List<FlavourEntry>,
    val genera: List<Genus>
)

data class FlavourEntry(
    val flavor_text: String,
    val language: NameItem,
    val version: NameItem
)

data class Genus(
    val genus: String,
    val language: NameItem
)

data class PokemonAbout(
    val flavourText: String,
    val genus: String
)