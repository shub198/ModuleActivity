package com.rajotiyapawan.pokedex.utility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.rajotiyapawan.pokedex.R

val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val montserratFont = GoogleFont("Montserrat")

fun getFontFamily(weight: FontWeight = FontWeight.Normal, font: GoogleFont = montserratFont): FontFamily {
    return FontFamily(Font(googleFont = font, fontProvider, weight = weight))
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Composable
fun Modifier.noRippleClick(delayMillis: Long = 500L, onClick: () -> Unit): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return this.then(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() }, indication = null
        ) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= delayMillis) {
                lastClickTime = currentTime
                onClick()
            }
        }
    )
}

@Composable
fun ImageFromUrl(modifier: Modifier = Modifier, imageUrl: String, contentScale: ContentScale = ContentScale.Fit) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl) // Your full TMDB image URL
            .crossfade(true)
//                .placeholder(R.mipmap.ic_placeholder_foreground) // Placeholder drawable
//                .error(R.mipmap.ic_placeholder_foreground)       // Show this if loading fails
            .build(),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        imageLoader = ImageLoader.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    )
}