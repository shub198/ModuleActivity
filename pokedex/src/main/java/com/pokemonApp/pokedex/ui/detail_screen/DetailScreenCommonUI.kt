package com.pokemonApp.pokedex.ui.detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokemonApp.pokedex.utility.getFontFamily

@Composable
fun DetailCardWithTitle(modifier: Modifier = Modifier, title: String, color: Color, content: @Composable (() -> Unit)) {
    Box(
        modifier, contentAlignment = Alignment.TopCenter
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
            content()
        }
        Box(
            Modifier
                .border(width = 1.dp, color = color, shape = RoundedCornerShape(50))
                .background(color = Color.White, shape = RoundedCornerShape(50))
                .padding(horizontal = 12.dp)
        ) {
            Text(
                title,
                color = color,
                fontFamily = getFontFamily(weight = FontWeight.SemiBold),
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
        }
    }
}