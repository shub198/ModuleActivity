package com.rajotiyapawan.pokedex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.ui.theme.waterBorder
import com.rajotiyapawan.pokedex.ui.theme.waterType
import com.rajotiyapawan.pokedex.ui.theme.waterTypeLight
import com.rajotiyapawan.pokedex.utility.ImageFromUrl
import com.rajotiyapawan.pokedex.utility.UiState
import com.rajotiyapawan.pokedex.utility.capitalize
import com.rajotiyapawan.pokedex.utility.getFontFamily
import java.util.Locale

@Composable
fun PokemonDetailScreen(modifier: Modifier = Modifier, viewModel: PokeViewModel) {
    val pokeData = viewModel.pokemonData.collectAsState()
    when (val response = pokeData.value) {
        is UiState.Error -> {}
        UiState.Idle -> {}
        UiState.Loading -> {
            CircularProgressIndicator(modifier)
        }

        is UiState.Success -> {
            DetailMainUI(modifier, viewModel, response.data)
        }
    }
}


@Composable
private fun DetailMainUI(modifier: Modifier = Modifier, viewModel: PokeViewModel, data: PokemonData) {
    val id = remember(data) {
        val id = data.id ?: 0
        if (id < 1000) {
            String.format(Locale.US, "%03d", id)
        } else {
            id.toString()
        }
    }
    Column(
        modifier
            .padding(8.dp)
            .border(width = 1.dp, color = waterBorder, shape = RoundedCornerShape(10.dp))
            .background(
                color = waterType,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    color = waterTypeLight,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(6.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(4.dp)
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp)), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text((data.name ?: "").capitalize(), fontFamily = getFontFamily(weight = FontWeight.Bold), fontSize = 24.sp)
                }
                Box(
                    Modifier
                        .padding(start = 4.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment
                        .Center
                ) {
                    Text("#$id", fontFamily = getFontFamily(weight = FontWeight.Bold), fontSize = 24.sp)
                }
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(4.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                ImageFromUrl(imageUrl = data.sprites?.other?.officialArtwork?.frontDefault ?: "", modifier = Modifier.fillMaxSize())
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    color = waterTypeLight,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Type", fontFamily = getFontFamily(weight = FontWeight.Bold))
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    (data.types?.get(0)?.type?.name ?: "").capitalize(), fontFamily = getFontFamily(weight = FontWeight.SemiBold), modifier = Modifier
                        .background(color = waterType, shape = RoundedCornerShape(4.dp))
                        .padding(vertical = 2.dp, horizontal = 8.dp), color = Color.White
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    color = waterTypeLight,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Abilities", fontFamily = getFontFamily(weight = FontWeight.Bold))
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp), contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp), columns = GridCells.Fixed(2)
                ) {
                    data.abilities?.let {
                        items(it) { ability ->
                            Column(Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text((ability.ability?.name ?: "").capitalize(), textAlign = TextAlign.Center, fontFamily = getFontFamily(weight = FontWeight.SemiBold), fontSize = 16.sp)
                                if (ability.isHidden == true) {
                                    Text("(Hidden Ability)", textAlign = TextAlign.Center, fontSize = 12.sp, fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                                }
                            }
                        }
                    }
                }
            }
        }
        Row {
            Column(
                Modifier
                    .weight(1f)
                    .background(
                        color = waterTypeLight,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Height", fontFamily = getFontFamily(weight = FontWeight.Bold))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp), contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text((data.height?.toDouble()?.div(10)).toString() + " m", fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                    }
                }
            }
            Column(
                Modifier
                    .padding(start = 4.dp)
                    .weight(1f)
                    .background(
                        color = waterTypeLight,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Type", fontFamily = getFontFamily(weight = FontWeight.Bold))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 10.dp), contentAlignment = Alignment.Center
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Text((data.weight?.div(10)).toString() + " kg", fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                    }
                }
            }
        }
    }
}