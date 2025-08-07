package com.pokemonApp.pokedex.ui.detail_screen.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemonApp.pokedex.PokeViewModel
import com.pokemonApp.pokedex.model.PokeTypes
import com.pokemonApp.pokedex.model.PokemonData
import com.pokemonApp.pokedex.ui.detail_screen.DetailCardWithTitle
import com.pokemonApp.pokedex.utility.TypeIcon
import com.pokemonApp.pokedex.utility.convertHeightToFeetInches
import com.pokemonApp.pokedex.utility.convertWeightToKg
import com.pokemonApp.pokedex.utility.convertWeightToLbs
import com.pokemonApp.pokedex.utility.getFontFamily
import java.util.Locale

@Composable
fun AboutSpecies(
    modifier: Modifier = Modifier,
    data: PokemonData,
    color: List<Color>,
    viewModel: PokeViewModel
) {
    LaunchedEffect(data.species?.name) { viewModel.fetchPokemonAbout(data.species) }
    val aboutData by viewModel.aboutData.collectAsState()

    DetailCardWithTitle(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp), title = "Species", color = color[0]
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

            // pokemon types
            AboutPokemonTypes(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp), types = data.types, color = color
            )

            // body measurements
            AboutBodyMeasurements(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                data.height,
                data.weight,
                malePercentage = aboutData.malePercentage,
                femalePercentage = aboutData.femalePercentage
            )
        }
    }
}

@Composable
private fun AboutPokemonTypes(modifier: Modifier = Modifier, types: ArrayList<PokeTypes>?, color: List<Color>) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        types?.forEachIndexed { index, type ->
            TypeIcon(
                type.type?.name ?: "", Modifier
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun AboutBodyMeasurements(
    modifier: Modifier = Modifier,
    height: Int?,
    weight: Int?,
    malePercentage: Double,
    femalePercentage: Double
) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        height?.let {
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
                "${malePercentage}% / ${femalePercentage}%",
                fontSize = 12.sp,
                lineHeight = 13.sp,
                fontFamily = getFontFamily()
            )
        }
        weight?.let {
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
