package com.rajotiyapawan.pokedex.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.model.NameItem
import com.rajotiyapawan.pokedex.model.PokedexUserEvent
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.ui.detail_screen.about.aboutTabUI
import com.rajotiyapawan.pokedex.utility.UiState
import com.rajotiyapawan.pokedex.utility.capitalize
import com.rajotiyapawan.pokedex.utility.getFontFamily
import com.rajotiyapawan.pokedex.utility.getTypeColor
import com.rajotiyapawan.pokedex.utility.noRippleClick
import kotlinx.coroutines.launch
import java.util.Locale

enum class PokemonDataTabs {
    About, Stats, Moves, Other
}

@Composable
fun PokemonDetailScreen(modifier: Modifier = Modifier, nameItem: NameItem, viewModel: PokeViewModel) {
    // Trigger fetch only when name changes
    LaunchedEffect(nameItem.name) {
        viewModel.getPokemonData(nameItem)
    }
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
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val statusBarColor = remember { mutableStateOf(Color.Transparent) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                statusBarColor.value = when (index) {
                    2 -> Color.White
                    1 -> Color.Transparent
                    else -> statusBarColor.value // or set default
                }
            }
    }
    val width = remember { mutableStateOf(0) }
    val id = remember(data) {
        val id = data.id ?: 0
        if (id < 1000) {
            String.format(Locale.US, "%03d", id)
        } else {
            id.toString()
        }
    }

    val typeColors = data.types?.map { getTypeColor(it.type?.name ?: "") } ?: listOf()

    val scaleInX = remember { Animatable(1f) }
    val scaleInY = remember { Animatable(1f) }
    val startWidth = with(LocalDensity.current) { (56.dp).toPx() }

    val targetSizePx = remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(targetSizePx.value) {
        val scaleFactorX = targetSizePx.value.width / startWidth
        val scaleFactorY = targetSizePx.value.height / startWidth
        launch {
            scaleInX.animateTo(scaleFactorX, tween(500))
        }
        launch {
            scaleInY.animateTo(scaleFactorY, tween(500))
        }
    }

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
            Box(Modifier.padding(padding)) {
                LazyColumn(
                    Modifier
                        .onGloballyPositioned {
                            width.value = it.size.width
                        }, contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 36.dp)
                ) {
                    item {
                        Column(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Text("#$id", fontFamily = getFontFamily(weight = FontWeight.SemiBold), color = Color.White)
                            Text(
                                (data.name ?: "").capitalize(),
                                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                                fontSize = 24.sp,
                                color = Color.White
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { layoutCoordinates ->
                                        targetSizePx.value = layoutCoordinates.size
                                    }, contentAlignment = Alignment.Center
                            ) {
                                val request = ImageRequest.Builder(context)
                                    .data(data.sprites?.other?.officialArtwork?.frontDefault) // must be the high-res artwork URL
                                    .crossfade(true)
                                    .size(Size.ORIGINAL) // do not scale down
                                    .build()
                                AsyncImage(
                                    request,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .graphicsLayer {
                                            scaleX = scaleInX.value
                                            scaleY = scaleInX.value
                                        },
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth
                                )
                                AsyncImage(
                                    request,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(0f),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }
                    stickyHeader {
                        TabBarRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 36.dp)
                                .background(Color(0xfff5f5f5)), selectedTab.value.ordinal
                        ) { selectedTab.value = it }
                    }
                    when (selectedTab.value) {
                        PokemonDataTabs.About -> {
                            aboutTabUI(modifier = Modifier.fillMaxWidth(), viewModel, data, typeColors)
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
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(statusBarColor.value)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .noRippleClick { viewModel.sendUserEvent(PokedexUserEvent.BackBtnClicked) })
                    Icon(
                        Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier
                            .padding(8.dp)
                            .noRippleClick {
                                viewModel.toggleFavourites(NameItem(data.name, ""))
                            })
                }
            }
        }
    }

}

@Composable
private fun TabBarRow(modifier: Modifier, selectedTabIndex: Int, onTabSelected: (PokemonDataTabs) -> Unit) {
    TabRow(
        modifier = modifier,
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
                text = {
                    Text(
                        tab.name,
                        color = Color.Black,
                        fontFamily = getFontFamily(FontWeight.SemiBold),
                        fontSize = 14.sp,
                        lineHeight = 15.sp
                    )
                }
            )
        }
    }
}