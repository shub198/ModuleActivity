package com.pokemonApp.pokedex.model

data class PokemonAboutDto(
    val flavor_text_entries: List<FlavourEntry>,
    val genera: List<Genus>,
    val gender_rate: Int,
    val base_happiness: Int,
    val order: Int,
    val capture_rate: Int,
    val hatch_counter: Int,
    val is_baby: Boolean,
    val is_legendary: Boolean,
    val is_mythical: Boolean,
    val egg_groups: List<NameItem>,
    val evolution_chain: NameItem,
    val growth_rate: NameItem,
    val habitat: NameItem,
    val shape: NameItem,
    val pokedex_numbers: List<PokemonNumber>,

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
    val growthRate: String,
    val femalePercentage: Double,
    val malePercentage: Double,
    val baseFriendship: Int,
    val hatchCounter: Int,
    val eggGroups: List<NameItem>,
    val evolutionChain: NameItem
) {
    companion object {
        fun init(): PokemonAbout {
            return PokemonAbout("", "", "", 0.0, 0.0, 0, 0, listOf(), NameItem("", ""))
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

data class PokemonNumber(
    val entry_number: Int,
    val pokedex: NameItem,
)