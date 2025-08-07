package com.pokemonApp.pokedex.utility

sealed class UiState<out R> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String, val code: Int? = null) : UiState<Nothing>()
}

val <T>UiState<T>.value: T?
    get() = (this as? UiState.Success)?.data
