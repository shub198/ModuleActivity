package com.pokemonApp.pokedex.ui.detail_screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemonApp.pokedex.PokeViewModel
import com.pokemonApp.pokedex.ui.detail_screen.DetailCardWithTitle
import com.pokemonApp.pokedex.utility.capitalize
import com.pokemonApp.pokedex.utility.getFontFamily
import com.pokemonApp.pokedex.utility.noRippleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutBreeding(modifier: Modifier = Modifier, color: Color, viewModel: PokeViewModel) {
    val aboutData by viewModel.aboutData.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAbilityDescription by remember { mutableStateOf("") }
    DetailCardWithTitle(modifier, "Breeding", color) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .padding(12.dp)
        ) {
            Text(
                text = "Egg groups:",
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                fontFamily = getFontFamily(),
                fontSize = 12.sp,
                lineHeight = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                aboutData.eggGroups.forEach {
                    Row(
                        Modifier
                            .weight(1f)
                            .background(color, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 5.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${it.name?.capitalize()}",
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                            fontFamily = getFontFamily(),
                            fontSize = 12.sp,
                            lineHeight = 13.sp
                        )
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.Black, modifier = Modifier.noRippleClick { showAbilityDescription = it.name ?: "" })
                    }
                }
            }
            Text(
                text = "Egg cycles:",
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                fontFamily = getFontFamily(),
                fontSize = 12.sp,
                lineHeight = 13.sp
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("${aboutData.hatchCounter} ")
                    }
                    append("(${aboutData.hatchCounter * 256} Steps)")
                },
                textAlign = TextAlign.Start,
                fontFamily = getFontFamily(),
                fontSize = 12.sp,
                lineHeight = 13.sp
            )

        }
    }
    if (showAbilityDescription.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showAbilityDescription = "" },
            sheetState = sheetState,
            containerColor = Color(0xfff5f5f5)
        ) {
            Text("Show pokemon list for this egg group")
        }
    }
}