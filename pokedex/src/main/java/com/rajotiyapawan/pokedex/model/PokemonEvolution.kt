package com.rajotiyapawan.pokedex.model

data class PokemonEvolutionDto(
    val baby_trigger_item: Any?,
    val chain: ChainDto?,
    val id: Int?
)

data class ChainDto(
    val evolution_details: List<EvolutionDetail>,
    val evolves_to: List<ChainDto>?,
    val is_baby: Boolean,
    val species: NameItem
)

fun ChainDto.toChain(): Chain {
    return Chain(
        evolves_to?.map { it.toChain() } ?: listOf(),
        species,
        evolution_details
    )
}

data class EvolutionDetail(
    val gender: Any,
    val held_item: Any,
    val item: Any,
    val known_move: Any,
    val known_move_type: Any,
    val location: Any,
    val min_affection: Any,
    val min_beauty: Any,
    val min_happiness: Any,
    val min_level: Int,
    val needs_overworld_rain: Boolean,
    val party_species: Any,
    val party_type: Any,
    val relative_physical_stats: Any,
    val time_of_day: String,
    val trade_species: Any,
    val trigger: NameItem,
    val turn_upside_down: Boolean
)

data class PokemonEvolution(
    val chain: Chain?
)

data class Chain(
    val evolvesTo: List<Chain>,
    val species: NameItem,
    val evolutionDetails: List<EvolutionDetail>
)
