package com.rajotiyapawan.pokedex.model

import com.google.gson.annotations.SerializedName

data class PokemonData(
    @SerializedName("abilities") var abilities: ArrayList<Abilities>? = null,
    @SerializedName("base_experience") var baseExperience: Int? = null,
    @SerializedName("cries") var cries: Cries? = Cries(),
    @SerializedName("forms") var forms: ArrayList<NameItem>?,
    @SerializedName("game_indices") var gameIndices: ArrayList<GameIndices>?,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("held_items") var heldItems: ArrayList<HeldItem>? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("is_default") var isDefault: Boolean? = null,
    @SerializedName("location_area_encounters") var locationAreaEncounters: String? = null,
    @SerializedName("moves") var moves: ArrayList<Moves>? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("order") var order: Int? = null,
    @SerializedName("past_abilities") var pastAbilities: ArrayList<PastAbilities>? = null,
    @SerializedName("past_types") var pastTypes: ArrayList<PastTypes>? = null,
    @SerializedName("species") var species: NameItem? = null,
    @SerializedName("sprites") var sprites: Sprites? = null,
    @SerializedName("stats") var stats: ArrayList<Stats>? = null,
    @SerializedName("types") var types: ArrayList<PokeTypes>? = null,
    @SerializedName("weight") var weight: Int? = null
)

data class Abilities(
    @SerializedName("ability") var ability: NameItem? = null,
    @SerializedName("is_hidden") var isHidden: Boolean? = null,
    @SerializedName("slot") var slot: Int? = null
)

data class Cries(
    @SerializedName("latest") var latest: String? = null,
    @SerializedName("legacy") var legacy: String? = null
)

data class GameIndices(
    @SerializedName("game_index") var gameIndex: Int? = null,
    @SerializedName("version") var version: NameItem? = null
)

data class VersionGroupDetails(
    @SerializedName("level_learned_at") var levelLearnedAt: Int? = null,
    @SerializedName("move_learn_method") var moveLearnMethod: NameItem? = null,
    @SerializedName("order") var order: Int? = null,
    @SerializedName("version_group") var versionGroup: NameItem? = null
)

data class Moves(
    @SerializedName("move") var move: NameItem? = null,
    @SerializedName("version_group_details") var versionGroupDetails: ArrayList<VersionGroupDetails>? = null
)

data class PastAbilities(
    @SerializedName("abilities") var abilities: ArrayList<Abilities>? = null,
    @SerializedName("generation") var generation: NameItem? = null
)

data class DreamWorld(
    @SerializedName("front_default") var frontDefault: String? = null,
    @SerializedName("front_female") var frontFemale: String? = null
)

data class Home(
    @SerializedName("front_default") var frontDefault: String? = null,
    @SerializedName("front_female") var frontFemale: String? = null,
    @SerializedName("front_shiny") var frontShiny: String? = null,
    @SerializedName("front_shiny_female") var frontShinyFemale: String? = null
)

data class Sprites(
    @SerializedName("back_default") val backDefault: String?,
    @SerializedName("back_female") val backFemale: String?,
    @SerializedName("back_shiny") val backShiny: String?,
    @SerializedName("back_shiny_female") val backShinyFemale: String?,
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_female") val frontFemale: String?,
    @SerializedName("front_shiny") val frontShiny: String?,
    @SerializedName("front_shiny_female") val frontShinyFemale: String?,
    val other: Other?,
//    val versions: PokeVersions?
)

data class Stats(
    @SerializedName("base_stat") val baseStat: Int?,
    @SerializedName("effort") val effort: Int?,
    @SerializedName("stat") val stat: NameItem?
)

data class PokeTypes(
    val slot: Int?,
    val type: NameItem?
)

data class Other(
    @SerializedName("dream_world") val dreamWorld: DreamWorld?,
    val home: Home?,
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?,
    val showDown: Sprites?
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_shiny") val frontShiny: String?,
)

data class HeldItem(
    val item: NameItem
)

data class PastTypes(
    val generation: NameItem?,
    val types: List<PokeTypes>?
)