package com.darren.fyp_dagger.screens

import android.os.SystemClock
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.fyp_dagger.*
import com.darren.fyp_dagger.R
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(navController: NavHostController) {
    val moveUp = remember { mutableStateOf(false) }
    val logoOffset = animateDpAsState(targetValue = if(moveUp.value) 10.dp else 0.dp, animationSpec = tween(durationMillis = 1000))
    val showLevelMenu = remember { mutableStateOf(false) }
    val landingScreenAlpha = animateFloatAsState(targetValue = if (showLevelMenu.value) 0f else 1f, animationSpec = tween(500))
    val levelMenuOffset = animateDpAsState(targetValue = if (showLevelMenu.value) 0.dp else screenHeightDp + 20.dp, animationSpec = tween(500))
    val sparkleAnim = remember { mutableStateOf(1) }
    var lastClickTime by remember { mutableStateOf(0L) }
    LaunchedEffect(true) { //Logo Anim
        while(true) {
            moveUp.value = true
            delay(1000)
            moveUp.value = false
            delay(1000)
        }
    }
    LaunchedEffect(true) { //Sparkles Anim
        while(true) {
            delay(500)
            sparkleAnim.value = (sparkleAnim.value + 1) % 2
        }
    }

    DrawBackground()
    DrawTopFruit()
    Box(modifier = Modifier
        .fillMaxSize()
        .alpha(landingScreenAlpha.value), contentAlignment = Alignment.Center) {
        DrawLogo(modifier = Modifier
            .align(Alignment.Center)
            .size(300.dp)
            .offset(y = -screenHeightDp.times(0.25f) - logoOffset.value)
        )
        DrawSparkle(id = 0, sparkleAnim)
        DrawSparkle(id = 1, sparkleAnim)
        DrawDagger(modifier = Modifier
            .size(140.dp)
            .offset(y = screenHeightDp.times(0.03f))
        )
        DrawButton(text = "PLAY", offsetY = screenHeightDp.times(0.22f)) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 1000L) {
                showLevelMenu.value = true
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        DrawShopButton(offsetY = screenHeightDp.times(0.35f)) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 1000L) {
                navController.navigate("shop_screen")
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = levelMenuOffset.value)) 
    {
        DrawButton(text = "Easy", offsetY = -screenHeightDp * 0.15f, id = R.drawable.button1) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 500L) {
                gameState.value.setWipe()
                navController.navigate("game_screen")
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        DrawButton(text = "Normal", id = R.drawable.button1) {

        }
        DrawButton(text = "Hard", offsetY = screenHeightDp * 0.15f, id = R.drawable.button1) {

        }
        DrawButton(text = "Crazy", offsetY = screenHeightDp * 0.3f, id = R.drawable.button1) {

        }
        DrawReturnButton(offsetY = (-50).dp) {
            showLevelMenu.value = false
        }
    }
    val use = remember { mutableStateOf(false) }
    val leftEyeProbability = remember { mutableStateOf(0f) }
    val rightEyeProbability = remember { mutableStateOf(0f) }
    val smileProbability = remember { mutableStateOf(0f) }
//    DrawCamera(leftEyeProbability, rightEyeProbability, smileProbability)
}