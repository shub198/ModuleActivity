package com.rajotiyapawan.pokedex.model

data class PokemonAboutDto(
    val flavor_text_entries: List<FlavourEntry>,
    val genera: List<Genus>,
    val gender_rate: Int
)

data class FlavourEntry(
    val flavor_text: String,
    val language: NameItem,
    val version: NameItem,
    val version_group: NameItem,
)

data class Genus(
    val genus: String,
    val language: NameItem
)

data class PokemonAbout(
    val flavourText: String,
    val genus: String,
    val femalePercentage: Double,
    val malePercentage: Double
) {
    companion object {
        fun init(): PokemonAbout {
            return PokemonAbout("", "", 0.0, 0.0)
        }
    }
}

data class PokemonAbilityDto(
    val effect_entries: List<AbilityEffect>,
    val flavor_text_entries: List<FlavourEntry>
)

data class AbilityEffect(
    val effect: String,
    val short_effect: String,
    val flavor_text: String,
    val language: NameItem
)