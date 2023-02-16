package com.mutualmobile.barricade.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mutualmobile.barricade.Barricade
import com.mutualmobile.barricade.R
import com.mutualmobile.barricade.ui.theme.ToolbarRed
import com.mutualmobile.barricade.utils.showToast

object BarricadeConfigScreen {
    const val CardRadiusPercentage = 10
    const val SpacerPadding = 8
    const val HorizontalPadding = 16
    const val VerticalPadding = 4
}

@Composable
fun BarricadeConfigScreen() {
    val ctx = LocalContext.current
    val barricade by remember { mutableStateOf(Barricade.getInstance()) }
    var isDelayDialogVisible by remember { mutableStateOf(false) }
    var isResetDialogVisible by remember { mutableStateOf(false) }
    var delayTfValue by remember { mutableStateOf(barricade.delay) }
    var barricadeConfigs = remember { barricade.getConfig() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.barricadeConfigTitle),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (ctx as Activity).onBackPressed()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            stringResource(R.string.backBtnCD),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        delayTfValue = barricade.delay
                        isDelayDialogVisible = true
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_timer),
                            stringResource(R.string.delayBtnCD),
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        isResetDialogVisible = true
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_undo),
                            stringResource(R.string.resetBtnCD),
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = ToolbarRed
            )
        }
    ) {
        AnimatedVisibility(visible = isDelayDialogVisible, enter = fadeIn(), exit = fadeOut()) {
            AlertDialog(
                onDismissRequest = { isDelayDialogVisible = false },
                confirmButton = {
                    Button(onClick = {
                        barricade.delay = delayTfValue
                        if (barricade.delay == delayTfValue) {
                            isDelayDialogVisible = false
                        } else {
                            ctx.showToast(ctx.resources.getString(R.string.updateFailedMsg))
                        }
                    }) {
                        Text(stringResource(R.string.delayDialogConfirmBtnText))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isDelayDialogVisible = false
                    }) {
                        Text(stringResource(R.string.delayDialogDismissBtnText))
                    }
                },
                title = { Text(stringResource(R.string.delayDialogTitle)) },
                text = {
                    TextField(
                        value = delayTfValue.toString(),
                        onValueChange = {
                            it.toLongOrNull()?.let { updatedTfValue ->
                                delayTfValue = updatedTfValue
                            } ?: run {
                                delayTfValue = 0
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent
                        )
                    )
                }
            )
        }
        AnimatedVisibility(visible = isResetDialogVisible, enter = fadeIn(), exit = fadeOut()) {
            AlertDialog(
                onDismissRequest = { isResetDialogVisible = false },
                confirmButton = {
                    TextButton(onClick = {
                        barricade.reset()
                        barricadeConfigs = barricade.getConfig()
                        isResetDialogVisible = false
                    }) {
                        Text(stringResource(R.string.resetDialogConfirmBtnText))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        isResetDialogVisible = false
                    }) {
                        Text(stringResource(R.string.resetDialogDismissBtnText))
                    }
                },
                text = {
                    Text(stringResource(R.string.resetDialogMsg))
                }
            )
        }
        LazyColumn {
            barricadeConfigs.forEach { config ->
                item {
                    config.value.responses[config.value.defaultIndex].let { defaultResponse ->
                        var isRowExpanded by remember { mutableStateOf(false) }
                        val arrowRotation by animateFloatAsState(targetValue = if (isRowExpanded) 180f else 0f)
                        val cardElevation by animateFloatAsState(targetValue = if (isRowExpanded) 8f else 0f)

                        Column(
                            modifier = Modifier.animateContentSize()
                        ) {
                            Card(
                                elevation = cardElevation.dp,
                                shape = RoundedCornerShape(BarricadeConfigScreen.CardRadiusPercentage),
                                modifier = Modifier.padding(cardElevation.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple()
                                        ) {
                                            isRowExpanded = !isRowExpanded
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Spacer(modifier = Modifier.padding(top = BarricadeConfigScreen.SpacerPadding.dp))
                                        Text(
                                            text = config.key,
                                            modifier = Modifier.padding(horizontal = BarricadeConfigScreen.HorizontalPadding.dp),
                                            style = MaterialTheme.typography.h6,
                                        )
                                        Text(
                                            text = defaultResponse.responseFileName,
                                            modifier = Modifier.padding(
                                                horizontal = BarricadeConfigScreen.HorizontalPadding.dp,
                                                vertical = BarricadeConfigScreen.VerticalPadding.dp
                                            ),
                                            style = MaterialTheme.typography.body2,
                                        )
                                        Spacer(modifier = Modifier.padding(top = BarricadeConfigScreen.SpacerPadding.dp))
                                    }
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = BarricadeConfigScreen.HorizontalPadding.dp)
                                            .rotate(arrowRotation)
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = isRowExpanded,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                Column {
                                    config.value.responses.forEachIndexed { index, response ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = cardElevation.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = rememberRipple()
                                                ) {
                                                    barricade.setResponse("random", index)
                                                    isRowExpanded = false
                                                }
                                        ) {
                                            Text(
                                                response.responseFileName,
                                                style = MaterialTheme.typography.body1,
                                                modifier = Modifier.padding(BarricadeConfigScreen.HorizontalPadding.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
