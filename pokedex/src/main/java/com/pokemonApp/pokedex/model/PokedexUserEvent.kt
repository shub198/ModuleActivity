package com.pokemonApp.pokedex.model

sealed class PokedexUserEvent {
    data object BackBtnClicked : PokedexUserEvent()
    data class OpenDetail(val nameItem: NameItem) : PokedexUserEvent()
}