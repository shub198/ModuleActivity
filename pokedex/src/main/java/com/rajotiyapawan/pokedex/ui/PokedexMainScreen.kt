package com.rajotiyapawan.pokedex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.model.NameItem
import com.rajotiyapawan.pokedex.utility.UiState
import com.rajotiyapawan.pokedex.utility.capitalize
import com.rajotiyapawan.pokedex.utility.getFontFamily
import com.rajotiyapawan.pokedex.utility.getTypeColor
import com.rajotiyapawan.pokedex.utility.noRippleClick
import java.util.Locale

@Composable
fun PokedexMainScreen(modifier: Modifier = Modifier, viewModel: PokeViewModel, itemSelected: (NameItem) -> Unit) {
    val pokeData = viewModel.pokemonList.collectAsState()
    when (val response = pokeData.value) {
        is UiState.Error -> {}
        UiState.Idle -> {}
        UiState.Loading -> {
            Box(modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(56.dp))
            }
        }

        is UiState.Success -> {
            response.data.results?.let { PokemonListUI(modifier, viewModel, it, itemSelected) }
        }
    }
}

@Composable
private fun PokemonListUI(modifier: Modifier = Modifier, viewModel: PokeViewModel, list: List<NameItem>, itemSelected: (NameItem) -> Unit) {
    LazyColumn(modifier.background(color = Color.Black), contentPadding = PaddingValues(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(list) { index, item ->
            PokemonListItem(Modifier, item, viewModel, itemSelected = {
                viewModel.getPokemonData(item)
                itemSelected(item)
            })
        }
    }
}

@Composable
private fun PokemonListItem(modifier: Modifier = Modifier, item: NameItem, viewModel: PokeViewModel, itemSelected: () -> Unit) {
    val detail = viewModel.pokemonDetails[item.name]

    LaunchedEffect(Unit) {
        if (detail == null) viewModel.fetchBasicDetail(item)
    }

    val typeColors = detail?.types?.map { getTypeColor(it) } ?: listOf()
    val gradientBrush = remember(detail?.types) {
        when (typeColors.size) {
            1 -> {
                val base = typeColors[0]
                val darker = base.copy(alpha = 1f).compositeOver(Color.Black.copy(alpha = 0.2f)) // Slight dark blend
                Brush.linearGradient(
                    colors = listOf(base.copy(alpha = 0.8f), darker),
                    start = Offset.Zero,
                    end = Offset(1450f, 0f)
                )
            }

            2 -> Brush.linearGradient(
                colors = listOf(
                    typeColors[0].copy(alpha = 0.9f),
                    typeColors[0].copy(alpha = 0.6f),
                    typeColors[1].copy(alpha = 0.6f),
                    typeColors[1].copy(alpha = 0.9f)
                ),
//                colorStops = floatArrayOf(0.0f, 0.4f, 0.55f, 1.0f), // skewed to right
                start = Offset.Zero,
                end = Offset(1450f, 0f)
            )

            else -> Brush.verticalGradient(colors = listOf(Color.LightGray, Color.DarkGray))
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                brush = gradientBrush,
                shape = RoundedCornerShape(12.dp)
            )
            .noRippleClick { itemSelected() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        if (detail != null) {
            AsyncImage(model = detail.imageUrl, contentDescription = null, modifier = Modifier.size(56.dp))
        } else {
            Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        Column(Modifier.padding(start = 8.dp)) {
            Text((item.name ?: "").capitalize(), fontFamily = getFontFamily(weight = FontWeight.SemiBold), fontSize = 18.sp)
            Text(text = detail?.types?.joinToString(", ", transform = { it.capitalize() }) ?: "")
        }
        val id = detail?.id ?: 0
        val formatted = if (id < 1000) {
            String.format(Locale.US, "%03d", id)
        } else {
            id.toString()
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Icon(Icons.Default.FavoriteBorder, contentDescription = null)
            Text("#$formatted", fontFamily = getFontFamily(weight = FontWeight.SemiBold), fontSize = 20.sp)
        }
    }
}