package com.pokemonApp.pokedex.ui.detail_screen.about

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pokemonApp.pokedex.PokeViewModel
import com.pokemonApp.pokedex.model.NameItem
import com.pokemonApp.pokedex.model.PokedexUserEvent
import com.pokemonApp.pokedex.ui.detail_screen.DetailCardWithTitle
import com.pokemonApp.pokedex.utility.capitalize
import com.pokemonApp.pokedex.utility.getFontFamily
import com.pokemonApp.pokedex.utility.noRippleClick

@Composable
fun AboutEvolution(modifier: Modifier = Modifier, color: Color, viewModel: PokeViewModel) {
    val aboutData by viewModel.aboutData.collectAsState()
    LaunchedEffect(Unit) { viewModel.getEvolutionChain(aboutData.evolutionChain) }
    val evolutionChain by viewModel.evolutionChain.collectAsState()
    val firstPokemon = evolutionChain.chain?.species

    DetailCardWithTitle(modifier, "Evolution chain", color) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EvolvePokemon(modifier = Modifier.weight(1f), pokemon = firstPokemon, viewModel = viewModel)
                if (evolutionChain.chain?.evolvesTo?.isNotEmpty() == true) {
                    val secondPokemon = evolutionChain.chain?.evolvesTo?.get(0)?.species
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        EvolvePokemon(modifier = Modifier, pokemon = secondPokemon, viewModel = viewModel)
                        Text(
                            "Level ${
                                evolutionChain.chain?.evolvesTo?.get(0)?.evolutionDetails?.takeIf { it.isNotEmpty() }
                                    ?.get(0)?.min_level
                            }",
                            fontSize = 12.sp,
                            lineHeight = 13.sp,
                            fontFamily = getFontFamily()
                        )
                    }
                    if (evolutionChain.chain?.evolvesTo?.get(0)?.evolvesTo?.isNotEmpty() == true) {
                        val thirdPokemon = evolutionChain.chain?.evolvesTo?.get(0)?.evolvesTo?.get(0)?.species
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            EvolvePokemon(modifier = Modifier, pokemon = thirdPokemon, viewModel = viewModel)
                            Text(
                                "Level ${
                                    evolutionChain.chain?.evolvesTo?.get(0)?.evolvesTo?.get(0)?.evolutionDetails?.takeIf { it.isNotEmpty() }
                                        ?.get(0)?.min_level
                                }",
                                fontSize = 12.sp,
                                lineHeight = 13.sp,
                                fontFamily = getFontFamily()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvolvePokemon(modifier: Modifier = Modifier, pokemon: NameItem?, viewModel: PokeViewModel) {
    pokemon?.let {
        val detail = viewModel.pokemonDetails[it.name]
        Log.d("Evolution", "Pokemon name = ${it.name} and url = ${it.url}")
        LaunchedEffect(pokemon.name) {
            if (detail == null) {
                viewModel.fetchBasicDetailByName(it.name)
            }
        }
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = detail?.imageUrl, contentDescription = null, contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(56.dp)
                    .noRippleClick {
                        viewModel.sendUserEvent(PokedexUserEvent.OpenDetail(pokemon))
                    }
            )
            Text(
                (it.name ?: "").capitalize(),
                fontSize = 12.sp,
                lineHeight = 13.sp,
                fontFamily = getFontFamily(weight = FontWeight.SemiBold)
            )
        }
    }
}