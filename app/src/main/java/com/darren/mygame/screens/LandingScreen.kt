package com.darren.mygame.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(navController : NavHostController) {
    val moveUp = remember { mutableStateOf(false) }
    val logoOffset = animateDpAsState(targetValue = if(moveUp.value) 10.dp else 0.dp, animationSpec = tween(durationMillis = 1000))

    LaunchedEffect(true) {
        while(true) {
            moveUp.value = true
            delay(1000)
            moveUp.value = false
            delay(1000)
        }
    }
    DrawBackground()
    DrawTopFruit()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DrawLogo(modifier = Modifier.align(Alignment.Center)
            .size(300.dp)
            .offset(y = (-180).dp - logoOffset.value)
        )
        DrawDagger(modifier = Modifier
            .size(120.dp)
            .offset(y = 80.dp)
        )
        DrawButton(text = "PLAY", offsetY = 200.dp) {
            gameReset()
            navController.navigate("game_screen")
        }
    }
}



@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun PreviewAct() {
    LandingScreen(navController = rememberAnimatedNavController())
}
