package com.rajotiyapawan.pokedex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rajotiyapawan.pokedex.ui.PokedexMainScreen
import com.rajotiyapawan.pokedex.ui.PokemonDetailScreen
import com.rajotiyapawan.pokedex.ui.theme.ModuleActivityTheme

class PokedexMainActivity : ComponentActivity() {

    private val viewModel: PokeViewModel by viewModels()

    companion object {
        fun launchPokedex(context: Context) {
            context.startActivity(Intent(context, PokedexMainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModuleActivityTheme {
                Scaffold { innerPadding ->
                    MainViews(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    private fun MainViews(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        val startDestination = "main"
        PrepareNavGraph(modifier, navController, startDestination)
    }

    @Composable
    private fun PrepareNavGraph(modifier: Modifier = Modifier, navController: NavHostController, startDestination: String) {
        NavHost(
            navController = navController, startDestination = startDestination,
            modifier = modifier
        ) {
            composable("main") {
                PokedexMainScreen(Modifier.fillMaxSize(), viewModel) {
                    navController.navigate("detail")
                }
            }

            composable("detail") {
                PokemonDetailScreen(Modifier.fillMaxSize(), viewModel)
            }

        }
    }
}