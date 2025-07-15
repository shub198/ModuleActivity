package com.rajotiyapawan.pokedex.ui.detail_screen.about

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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.ui.detail_screen.DetailCardWithTitle
import com.rajotiyapawan.pokedex.utility.capitalize

@Composable
fun AboutEvolution(modifier: Modifier = Modifier, color: Color, viewModel: PokeViewModel) {
    val aboutData by viewModel.aboutData.collectAsState()
    LaunchedEffect(aboutData.evolutionChain.name) { viewModel.getEvolutionChain(aboutData.evolutionChain) }
    val evolutionChain by viewModel.evolutionChain.collectAsState()
    DetailCardWithTitle(modifier, "Evolution", color) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val firstPokemon = evolutionChain.chain?.species?.name ?: ""
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val detail = viewModel.pokemonDetails[firstPokemon]
                    AsyncImage(
                        model = detail?.imageUrl, contentDescription = null, contentScale = ContentScale.FillWidth,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(firstPokemon.capitalize())
                }
                val secondPokemon = evolutionChain.chain?.evolvesTo?.takeIf { it.isNotEmpty() }?.get(0)?.species?.name ?: ""
                if (secondPokemon.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val detail = viewModel.pokemonDetails[secondPokemon]
                        AsyncImage(
                            model = detail?.imageUrl, contentDescription = null, contentScale = ContentScale.FillWidth,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(secondPokemon.capitalize())
                    }
                }
                val thirdPokemon =
                    evolutionChain.chain?.evolvesTo?.takeIf { it.isNotEmpty() }?.get(0)?.evolvesTo?.takeIf { it.isNotEmpty() }
                        ?.get(0)?.species?.name ?: ""
                if (thirdPokemon.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val detail = viewModel.pokemonDetails[thirdPokemon]
                        AsyncImage(
                            model = detail?.imageUrl, contentDescription = null, contentScale = ContentScale.FillWidth,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(thirdPokemon.capitalize())
                    }
                }
            }
        }
    }
}