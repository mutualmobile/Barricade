package com.mutualmobile.barricadeSample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.mutualmobile.barricadeSample.data.JokeApi
import com.mutualmobile.barricadeSample.ui.screens.LandingScreen
import com.mutualmobile.barricadeSample.ui.theme.barricadeTheme
import com.mutualmobile.barricadeSample.viewmodels.LandingScreenVM

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val landingScreenVM by viewModels<LandingScreenVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            JokeApi.instance.getRandomJoke().body()?.let { nnJokeBody ->
                Log.d(TAG, "onCreate: $nnJokeBody")
            }
        }
        setContent {
            barricadeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LandingScreen(landingScreenVM)
                }
            }
        }
    }
}
