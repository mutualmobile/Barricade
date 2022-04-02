package com.mutualmobile.barricadeSample.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mutualmobile.barricade.Barricade
import com.mutualmobile.barricadeSample.R
import com.mutualmobile.barricadeSample.states.ResponseState
import com.mutualmobile.barricadeSample.viewmodels.LandingScreenVM

object LandingScreen {
    const val HorizontalPadding = 16
    const val VerticalPadding = 4
    const val RoundedCornerPercent = 15
    const val ImageSize = 50
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LandingScreen(
    landingScreenVM: LandingScreenVM
) {
    val ctx = LocalContext.current
    val barricade by remember { mutableStateOf(Barricade.getInstance()) }
    val currentJokeState = landingScreenVM.jokeResponseState
    val currentJokeCategoriesState = landingScreenVM.jokeCategoriesState
    val isChecked = landingScreenVM.barricadeStatus

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { barricade.launchConfigActivity(ctx) }) {
                        Icon(Icons.Default.Settings, stringResource(R.string.configBtnDesc))
                    }
                }
            )
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = LandingScreen.HorizontalPadding.dp,
                        vertical = LandingScreen.VerticalPadding.dp
                    ),
            ) {
                Text(stringResource(R.string.barricade))
                Switch(checked = isChecked, onCheckedChange = { switchChecked ->
                    landingScreenVM.setBarricadeEnabled(switchChecked)
                })
            }
            Button(onClick = {
                landingScreenVM.fetchJoke()
            }) {
                Text(stringResource(R.string.getJoke))
            }
            Button(onClick = {
                landingScreenVM.fetchJokeCategories()
            }) {
                Text(stringResource(R.string.getJokeCategories))
            }
            AnimatedContent(
                targetState = currentJokeState,
                transitionSpec = { fadeIn() + slideInVertically() with fadeOut() },
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { jokeState ->
                when (jokeState) {
                    is ResponseState.Empty -> {}
                    is ResponseState.Loading -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    is ResponseState.Success -> {
                        Row(
                            modifier = Modifier.padding(horizontal = LandingScreen.HorizontalPadding.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(LandingScreen.RoundedCornerPercent),
                                modifier = Modifier.size(LandingScreen.ImageSize.dp)
                            ) {
                                AsyncImage(
                                    model = jokeState.data.icon_url,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                            Text(
                                jokeState.data.value,
                                modifier = Modifier
                                    .padding(
                                        horizontal = LandingScreen.HorizontalPadding.div(2).dp
                                    )
                            )
                        }
                    }
                    is ResponseState.Failure -> {
                        Text(jokeState.reason, color = MaterialTheme.colors.error)
                    }
                }
            }
            AnimatedContent(
                targetState = currentJokeCategoriesState,
                transitionSpec = { fadeIn() + slideInVertically() with fadeOut() },
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { categoryState ->
                when (categoryState) {
                    is ResponseState.Empty -> {}
                    is ResponseState.Loading -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    is ResponseState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = LandingScreen.HorizontalPadding.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            categoryState.data.forEach { jokeCategory ->
                                item {
                                    Text(
                                        jokeCategory,
                                        modifier = Modifier.padding(
                                            horizontal = LandingScreen.HorizontalPadding.div(2).dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    is ResponseState.Failure -> {
                        Text(categoryState.reason, color = MaterialTheme.colors.error)
                    }
                }
            }
        }
    }
}
