package com.rajotiyapawan.pokedex.ui.detail_screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rajotiyapawan.pokedex.PokeViewModel
import com.rajotiyapawan.pokedex.model.PokemonData
import com.rajotiyapawan.pokedex.ui.detail_screen.DetailCardWithTitle
import com.rajotiyapawan.pokedex.utility.capitalize
import com.rajotiyapawan.pokedex.utility.getFontFamily

@Composable
fun AboutTraining(modifier: Modifier = Modifier, color: Color, data: PokemonData, viewModel: PokeViewModel) {
    val aboutData by viewModel.aboutData.collectAsState()
    DetailCardWithTitle(modifier, "Training", color) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .padding(12.dp)
        ) {
            TrainingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), "GrowthRate", listOf(aboutData.growthRate.capitalize())
            )
            val evYieldList = data.stats?.filter { (it.effort ?: 0) > 0 }?.map { "${it.effort} ${it.stat?.name?.capitalize()}" } ?: listOf()
            TrainingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), "EV Yield", evYieldList
            )
            TrainingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), "Base Exp", listOf(data.baseExperience?.toString() ?: "")
            )
            TrainingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), "Base Friendship", listOf(aboutData.baseFriendship.toString())
            )
        }
    }
}

@Composable
private fun TrainingItem(modifier: Modifier, itemName: String, items: List<String>) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$itemName:",
            modifier = Modifier
                .weight(0.6f)
                .padding(end = 8.dp),
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
            fontFamily = getFontFamily(),
            fontSize = 12.sp,
            lineHeight = 13.sp
        )
        Column(Modifier.weight(1f)) {
            items.forEach {
                Text(
                    text = it,
                    textAlign = TextAlign.Start,
                    fontFamily = getFontFamily(),
                    fontSize = 12.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}