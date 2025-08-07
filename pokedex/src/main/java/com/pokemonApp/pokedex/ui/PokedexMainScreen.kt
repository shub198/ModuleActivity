package com.pokemonApp.pokedex.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.pokemonApp.pokedex.PokeViewModel
import com.pokemonApp.pokedex.model.NameItem
import com.pokemonApp.pokedex.model.PokedexUserEvent
import com.pokemonApp.pokedex.utility.UiState
import com.pokemonApp.pokedex.utility.capitalize
import com.pokemonApp.pokedex.utility.getFontFamily
import com.pokemonApp.pokedex.utility.getTypeColor
import com.pokemonApp.pokedex.utility.noRippleClick
import java.util.Locale

@Composable
fun PokedexMainScreen(modifier: Modifier = Modifier, viewModel: PokeViewModel) {
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
            response.data.results?.let { MainScreenUI(modifier, viewModel) }
        }
    }
}

@Composable
private fun MainScreenUI(modifier: Modifier = Modifier, viewModel: PokeViewModel) {
    LaunchedEffect(Unit) { viewModel.onQueryChanged("") }
    val focusManager = LocalFocusManager.current
    Scaffold(modifier) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .noRippleClick { focusManager.clearFocus() }
        ) {
            Text(
                "Pokedex", Modifier
                    .fillMaxWidth(),
                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                fontSize = 28.sp, color = Color.Red, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SearchBar(
                    Modifier
                        .padding(end = 16.dp)
                        .weight(1f), viewModel
                )
                Icon(Icons.Outlined.Menu, contentDescription = null, tint = Color.Black)
            }
            PokemonListUI(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp), viewModel
            )
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, viewModel: PokeViewModel) {
    val query by viewModel.query.collectAsState()
    BasicTextField(
        value = query,
        onValueChange = { viewModel.onQueryChanged(it) },
        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontFamily = getFontFamily()),
        modifier = modifier
            .height(50.dp)
            .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(50)),
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(30.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp), contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            "Search for a Pokemon ...",
                            style = TextStyle(color = Color.Black, fontSize = 16.sp, fontFamily = getFontFamily())
                        )
                    }
                    innerTextField()
                }
            }
        })
}

@Composable
private fun PokemonListUI(modifier: Modifier = Modifier, viewModel: PokeViewModel) {
    val list by viewModel.searchResults.collectAsState()

    LazyColumn(
        modifier
            .padding(top = 12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(list) { index, item ->
            PokemonListItem(Modifier.fillMaxWidth(), item, viewModel, itemSelected = {
                viewModel.sendUserEvent(PokedexUserEvent.OpenDetail(item))
            })
        }
    }
}

@Composable
private fun PokemonListItem(modifier: Modifier = Modifier, item: NameItem, viewModel: PokeViewModel, itemSelected: () -> Unit) {
    val detail = viewModel.pokemonDetails[item.name]
    val width = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current

    LaunchedEffect(item.name) {
        if (detail == null) viewModel.fetchBasicDetail(item)
    }
    Log.d("PokemonUI", "Composing ${item.name} | detail loaded = ${detail != null}")

    val typeColors = detail?.types?.map { getTypeColor(it) } ?: listOf()
    val gradientBrush = remember(detail?.types) {
        when (typeColors.size) {
            1 -> {
                val base = typeColors[0]
                val darker = Color.Black.copy(alpha = 0.3f).compositeOver(base.copy(alpha = 1f)) // Slight dark blend
                Brush.linearGradient(
                    colors = listOf(base.copy(alpha = 0.8f), darker),
                    start = Offset.Zero,
                    end = Offset(width * 3.6f, 0f)
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
                end = Offset(width * 3.6f, 0f)
            )

            else -> Brush.verticalGradient(colors = listOf(Color.LightGray, Color.DarkGray))
        }
    }

    Row(
        modifier
            .background(
                brush = gradientBrush,
                shape = RoundedCornerShape(12.dp)
            )
            .noRippleClick {
                itemSelected()
            }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        if (detail != null) {
            Box(Modifier.size(56.dp)) {
                val request = ImageRequest.Builder(context)
                    .data(detail.imageUrl) // must be the high-res artwork URL
                    .crossfade(true)
                    .size(Size.ORIGINAL) // do not scale down
                    .build()
                AsyncImage(
                    model = request, contentDescription = null, contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
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
