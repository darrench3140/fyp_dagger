package com.darren.mygame.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.darren.mygame.R
import com.darren.mygame.ScreenManager
import com.darren.mygame.ui.DrawBackground
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000))
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        startAnimation = false
        delay(2000)
        navController.popBackStack()
        navController.navigate(ScreenManager.LandingScreen.route)
    }
    Load(alphaAnim.value)
}

@Composable
fun Load(alpha: Float) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFF))
            .fillMaxSize()
    ) {
        DrawBackground()
        Image(
            painter = painterResource(id = R.drawable.loading),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = alpha
        )
    }
}