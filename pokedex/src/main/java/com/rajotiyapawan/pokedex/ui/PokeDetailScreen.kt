package com.rajotiyapawan.pokedex.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.model.Abilities
import com.rajotiyapawan.pokedex.model.NameItem
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.utility.UiState
import com.rajotiyapawan.pokedex.utility.capitalize
import com.rajotiyapawan.pokedex.utility.getFontFamily
import com.rajotiyapawan.pokedex.utility.getTypeColor
import java.util.Locale

enum class PokemonDataTabs {
    About, Stats, Moves, Other
}

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

@OptIn(ExperimentalFoundationApi::class)
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

    val typeColors = data.types?.map { getTypeColor(it.type?.name ?: "") } ?: listOf()
    Scaffold { padding ->
        Box(
            modifier
                .fillMaxSize()
                .background(Color(0xfff5f5f5))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                val canvasWidth = size.width
                // === Create the arc path ===
                val arcHeight = 500f // height of the curved area
                val path = Path().apply {
                    moveTo(0f, 0f)
                    arcTo(
                        rect = Rect(
                            left = -canvasWidth / 2f,
                            top = 0f,
                            right = canvasWidth * 1.5f,
                            bottom = arcHeight * 2.5f
                        ),
                        startAngleDegrees = -180f,
                        sweepAngleDegrees = -180f,
                        forceMoveTo = false
                    )
                    lineTo(canvasWidth, 0f)
                    close()
                }

                // === Draw the upper arc gradient ===
                val gradientBrush =
                    when (typeColors.size) {
                        1 -> {
                            val base = typeColors[0]
                            val darker = base.copy(alpha = 1f).compositeOver(Color.Black.copy(alpha = 0.2f)) // Slight dark blend
                            Brush.linearGradient(
                                colors = listOf(base.copy(alpha = 0.8f), darker),
                                start = Offset(0f, size.height),
                                end = Offset(size.width, 0f)
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
                            start = Offset(0f, size.height),
                            end = Offset(size.width * 1.8f, -size.height * 0.2f)
                        )

                        else -> Brush.verticalGradient(colors = listOf(Color.LightGray, Color.DarkGray))
                    }
                drawPath(
                    path = path,
                    brush = gradientBrush
                )
            }
            val selectedTab = remember { mutableStateOf(PokemonDataTabs.About) }
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 16.dp)) {
                stickyHeader {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null)
                    }
                }
                item {
                    Column {
                        Text("#$id", fontFamily = getFontFamily(weight = FontWeight.SemiBold), color = Color.White)
                        Text((data.name ?: "").capitalize(), fontFamily = getFontFamily(weight = FontWeight.SemiBold), fontSize = 24.sp, color = Color.White)
                        AsyncImage(data.sprites?.other?.officialArtwork?.frontDefault, modifier = Modifier.fillMaxWidth(), contentDescription = null, contentScale = ContentScale.FillWidth)
                    }
                }
                stickyHeader {
                    TabBarRow(selectedTab.value.ordinal) { selectedTab.value = it }
                }
                when (selectedTab.value) {
                    PokemonDataTabs.About -> {
                        item {
                            data.species?.let { AboutSpecies(viewModel = viewModel, species = it, color = typeColors[0]) }
                        }
                        item {
                            data.abilities?.let {
                                AboutAbilities(color = typeColors[0], viewModel = viewModel, modifier = Modifier, abilities = it)
                            }
                        }
                    }

                    PokemonDataTabs.Stats -> {
                        item { Text("Stats") }
                    }

                    PokemonDataTabs.Moves -> {
                        item { Text("Moves") }
                    }

                    PokemonDataTabs.Other -> {
                        item { Text("Other") }
                    }
                }
            }
        }
    }

}

@Composable
private fun TabBarRow(selectedTabIndex: Int, onTabSelected: (PokemonDataTabs) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        divider = {},
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        }
    ) {
        PokemonDataTabs.entries.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.name, color = Color.Black, fontFamily = getFontFamily(FontWeight.SemiBold), fontSize = 16.sp) }
            )
        }
    }
}

@Composable
private fun AboutSpecies(modifier: Modifier = Modifier, species: NameItem, color: Color, viewModel: PokeViewModel) {
    LaunchedEffect(species.name) { viewModel.fetchPokemonAbout(species) }
    val aboutData by viewModel.aboutData.collectAsState()
    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp), contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(4.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(aboutData.genus, fontFamily = getFontFamily(weight = FontWeight.SemiBold), color = Color.Black, fontSize = 15.sp)
                Text(aboutData.flavourText, fontFamily = getFontFamily(), color = Color.Black, fontSize = 15.sp)
            }
        }
        Box(
            Modifier
                .border(width = 1.dp, color = color, shape = RoundedCornerShape(50))
                .background(color = Color.White, shape = RoundedCornerShape(50))
                .padding(horizontal = 12.dp)
        ) {
            Text("Species", color = color, fontFamily = getFontFamily(weight = FontWeight.SemiBold), fontSize = 14.sp)
        }
    }
}

@Composable
private fun AboutAbilities(modifier: Modifier = Modifier, color: Color, abilities: ArrayList<Abilities>, viewModel: PokeViewModel) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp), contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(4.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                abilities.forEach { ability ->
                    val detail = viewModel.abilityDetails[ability.ability?.name ?: ""]
                    LaunchedEffect(Unit) {
                        if (detail == null) viewModel.getAbilityEffect(ability.ability)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = color.copy(alpha = 0.5f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Text((ability.ability?.name ?: "").capitalize(), color = color, fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                            if (ability.isHidden == true) {
                                Text(" - Hidden", color = color.copy(alpha = 0.5f), fontFamily = getFontFamily(weight = FontWeight.SemiBold))

                            }
                        }
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = color)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(detail?.flavor_text ?: "", modifier = Modifier.fillMaxWidth(), fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
        Box(
            Modifier
                .border(width = 1.dp, color = color, shape = RoundedCornerShape(50))
                .background(color = Color.White, shape = RoundedCornerShape(50))
                .padding(horizontal = 12.dp)
        ) {
            Text("Abilities", color = color, fontFamily = getFontFamily(weight = FontWeight.SemiBold), fontSize = 14.sp)
        }
    }
}