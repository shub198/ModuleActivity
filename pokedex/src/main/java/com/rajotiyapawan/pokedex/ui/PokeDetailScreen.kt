package com.rajotiyapawan.pokedex.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.R
import com.rajotiyapawan.pokedex.model.Abilities
import com.rajotiyapawan.pokedex.model.PokeTypes
import com.rajotiyapawan.pokedex.model.PokedexUserEvent
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.utility.UiState
import com.rajotiyapawan.pokedex.utility.capitalize
import com.rajotiyapawan.pokedex.utility.convertHeightToFeetInches
import com.rajotiyapawan.pokedex.utility.convertWeightToKg
import com.rajotiyapawan.pokedex.utility.convertWeightToLbs
import com.rajotiyapawan.pokedex.utility.getFontFamily
import com.rajotiyapawan.pokedex.utility.getTypeColor
import com.rajotiyapawan.pokedex.utility.noRippleClick
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
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

    var animationDone by remember { mutableStateOf(false) }
    val startRect = viewModel.selectedItemBounds ?: return
    val offset = remember { Animatable(Offset(startRect.left.toFloat(), startRect.top.toFloat()), Offset.VectorConverter) }
    val scaleInX = remember { Animatable(1f) }
    val scaleInY = remember { Animatable(1f) }
    val startWidth = startRect.width().toFloat()

    val targetOffsetPx = remember { mutableStateOf(Offset.Zero) }
    val targetSizePx = remember { mutableStateOf(IntSize.Zero) }
    val targetOffset = remember { mutableStateOf(Offset.Zero) }
    var scaleFactorX = 0f
    var scaleFactorY = 0f
    LaunchedEffect(targetSizePx.value) {
        scaleFactorX = targetSizePx.value.width / startWidth
        scaleFactorY = targetSizePx.value.height / startWidth
        targetOffset.value = Offset(
            x = targetSizePx.value.width / 2 - (1.8f) * targetOffsetPx.value.x,
            y = targetSizePx.value.height / 2 - targetOffsetPx.value.y / 5
        )
        if (targetOffsetPx.value != Offset.Zero) {
            launch {
                offset.animateTo(targetOffset.value, tween(500))
            }
            launch {
                scaleInX.animateTo(scaleFactorX, tween(500))
            }
            launch {
                scaleInY.animateTo(scaleFactorY, tween(500))
            }.invokeOnCompletion {
                animationDone = true
            }
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
            Box {
                LazyColumn(
                    Modifier
                        .padding(padding)
                        .onGloballyPositioned {
                            width.value = it.size.width
                        }, contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    stickyHeader {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.noRippleClick { viewModel.sendUserEvent(PokedexUserEvent.BackBtnClicked) })
                            Icon(Icons.Default.FavoriteBorder, contentDescription = null)
                        }
                    }
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
                                        targetOffsetPx.value = layoutCoordinates.localToWindow(Offset.Zero)
                                        targetSizePx.value = layoutCoordinates.size
                                    }
                            ) {
                                val request = ImageRequest.Builder(context)
                                    .data(data.sprites?.other?.officialArtwork?.frontDefault) // must be the high-res artwork URL
                                    .crossfade(true)
                                    .size(coil.size.Size.ORIGINAL) // do not scale down
                                    .build()
                                AsyncImage(
                                    request,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .graphicsLayer {
                                            translationX = offset.value.x
                                            translationY = offset.value.y
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
                        TabBarRow(selectedTab.value.ordinal) { selectedTab.value = it }
                    }
                    when (selectedTab.value) {
                        PokemonDataTabs.About -> {
                            item {
                                AboutSpecies(viewModel = viewModel, data = data, color = typeColors, types = data.types)
                            }
                            item {
                                data.abilities?.let {
                                    AboutAbilities(
                                        color = typeColors[0],
                                        viewModel = viewModel,
                                        modifier = Modifier,
                                        abilities = it
                                    )
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

@Composable
private fun AboutSpecies(
    modifier: Modifier = Modifier,
    data: PokemonData,
    color: List<Color>,
    viewModel: PokeViewModel,
    types: ArrayList<PokeTypes>?
) {
    LaunchedEffect(data.species?.name) { viewModel.fetchPokemonAbout(data.species) }
    val aboutData by viewModel.aboutData.collectAsState()
    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp), contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
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
                Text(
                    aboutData.genus,
                    fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                    color = Color.Black,
                    fontSize = 15.sp
                )
                Text(
                    aboutData.flavourText,
                    fontFamily = getFontFamily(),
                    color = Color.Black,
                    fontSize = 12.sp,
                    lineHeight = 13.sp
                )
                Spacer(Modifier.height(12.dp))

                // pokemon types
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    types?.forEachIndexed { index, type ->
                        Box(
                            Modifier
                                .padding(horizontal = 4.dp)
                                .background(color = color[index], shape = RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "${type.type?.name?.capitalize()}",
                                color = Color.White,
                                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                                fontSize = 12.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                // body measurements
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    data.height?.let {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val (feet, inches) = convertHeightToFeetInches(it)
                            Text(
                                "Height",
                                fontSize = 12.sp,
                                lineHeight = 13.sp,
                                fontFamily = getFontFamily(weight = FontWeight.SemiBold)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("$feet' $inches\"", fontSize = 12.sp, lineHeight = 13.sp, fontFamily = getFontFamily())
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Gender Ratio",
                            fontSize = 12.sp,
                            lineHeight = 13.sp,
                            fontFamily = getFontFamily(weight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${aboutData.malePercentage}% / ${aboutData.femalePercentage}%",
                            fontSize = 12.sp,
                            lineHeight = 13.sp,
                            fontFamily = getFontFamily()
                        )
                    }
                    data.weight?.let {
                        val kg = convertWeightToKg(it)
                        val lbs = convertWeightToLbs(it)
                        val weight = String.format(Locale.US, "%.1f kg \n(%.1f lbs)", kg, lbs)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Weight",
                                fontSize = 12.sp,
                                lineHeight = 13.sp,
                                fontFamily = getFontFamily(weight = FontWeight.SemiBold)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                weight,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                lineHeight = 13.sp,
                                fontFamily = getFontFamily()
                            )
                        }
                    }
                }
            }
        }
        Box(
            Modifier
                .border(width = 1.dp, color = color[0], shape = RoundedCornerShape(50))
                .background(color = Color.White, shape = RoundedCornerShape(50))
                .padding(horizontal = 12.dp)
        ) {
            Text(
                "Species",
                color = color[0],
                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutAbilities(
    modifier: Modifier = Modifier,
    color: Color,
    abilities: ArrayList<Abilities>,
    viewModel: PokeViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showAbilityDescription by remember { mutableStateOf("") }
    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp), contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
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
                Text(
                    stringResource(R.string.ability_intro),
                    color = Color.Black,
                    fontFamily = getFontFamily(),
                    fontSize = 12.sp,
                    lineHeight = 13.sp
                )
                Spacer(Modifier.height(18.dp))
                abilities.forEach { ability ->
                    val detail = viewModel.abilityDetails[ability.ability?.name ?: ""]
                    LaunchedEffect(Unit) {
                        if (detail == null) viewModel.getAbilityEffect(ability.ability)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                (ability.ability?.name ?: "").capitalize(),
                                color = color,
                                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                                fontSize = 12.sp,
                                lineHeight = 13.sp
                            )
                            if (ability.isHidden == true) {
                                Text(
                                    " - Hidden",
                                    color = color.copy(alpha = 0.5f),
                                    fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                                    fontSize = 12.sp,
                                    lineHeight = 13.sp
                                )

                            }
                        }
                        Icon(
                            Icons.Outlined.Info, contentDescription = null, tint = color, modifier = Modifier
                                .size(16.dp)
                                .noRippleClick { showAbilityDescription = ability.ability?.name ?: "" })
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(detail?.flavor_text ?: "", modifier = Modifier.fillMaxWidth(), fontSize = 12.sp, lineHeight = 13.sp)
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
            Text(
                "Abilities",
                color = color,
                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
        }
    }
    if (showAbilityDescription.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showAbilityDescription = "" },
            sheetState = sheetState,
            containerColor = Color(0xfff5f5f5)
        ) {
            val detail = viewModel.abilityDetails[showAbilityDescription]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "ABILITY",
                    color = Color(0xff909090),
                    fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                    fontSize = 10.sp,
                    lineHeight = 11.sp
                )
                Text(showAbilityDescription.capitalize(), fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                Box(
                    modifier
                        .fillMaxWidth()
                        .padding(16.dp), contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .padding(4.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
                        ) {
                            Text("Description", fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                            Text(detail?.flavor_text ?: "", fontSize = 12.sp, lineHeight = 13.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("Effect", fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                            Text(detail?.short_effect ?: "", fontSize = 12.sp, lineHeight = 13.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("Details", fontFamily = getFontFamily(weight = FontWeight.SemiBold))
                            Text(detail?.effect ?: "", fontSize = 12.sp, lineHeight = 13.sp)
                        }
                    }
                    Box(
                        Modifier
                            .border(width = 1.dp, color = color, shape = RoundedCornerShape(50))
                            .background(color = Color.White, shape = RoundedCornerShape(50))
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            "Details",
                            color = color,
                            fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                            fontSize = 14.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }

}