package com.darren.mygame.screens

import android.os.SystemClock
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(navController: NavHostController) {
    val moveUp = remember { mutableStateOf(false) }
    val logoOffset = animateDpAsState(targetValue = if(moveUp.value) 10.dp else 0.dp, animationSpec = tween(durationMillis = 1000))
    var lastClickTime by remember { mutableStateOf(0L) }
    LaunchedEffect(true) { //Logo Anim
        while(true) {
            moveUp.value = true
            delay(1000)
            moveUp.value = false
            delay(1000)
        }
    }
    LaunchedEffect(true) {

    }
    DrawBackground()
    DrawTopFruit()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DrawLogo(modifier = Modifier
            .align(Alignment.Center)
            .size(300.dp)
            .offset(y = -screenHeightDp.times(0.25f) - logoOffset.value)
        )
        DrawSparkle(offsetX = 80.dp, offsetY = 60.dp, scale = 1f)
        DrawSparkle(offsetX = 60.dp, offsetY = 80.dp, scale = 1f)
        DrawSparkle(offsetX = 110.dp, offsetY = 80.dp, scale = 1f)
        DrawSparkle(offsetX = 130.dp, offsetY = 40.dp, scale = 1f)
        DrawSparkle(offsetX = 85.dp, offsetY = 70.dp, scale = 1f)
        DrawDagger(modifier = Modifier
            .size(140.dp)
            .offset(y = screenHeightDp.times(0.03f))
        )
        DrawButton(text = "PLAY", offsetY = screenHeightDp.times(0.22f)) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                gameState.value.setWipe()
                navController.navigate("game_screen")
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        DrawShopButton(offsetY = screenHeightDp.times(0.35f)) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                navController.navigate("shop_screen")
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
    }
}