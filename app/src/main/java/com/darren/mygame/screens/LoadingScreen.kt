package com.darren.mygame.screens

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.DrawBackground
import com.darren.mygame.DrawLogo
import com.darren.mygame.DrawSword
import com.darren.mygame.ScreenManager
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavHostController) {
    var showSword by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }
    var moveLogo by remember { mutableStateOf(false) }
    val swordAnim = animateFloatAsState(targetValue = if(showSword) 0.6f else 0f, animationSpec = tween(durationMillis = 3000))
    val logoAlphaAnim = animateFloatAsState(targetValue = if(showLogo) 1f else 0f, animationSpec = tween(durationMillis = 2000))
    val logoMoveAnim = animateDpAsState(
        targetValue = if(moveLogo) (-60).dp else 150.dp,
        animationSpec = tween(durationMillis = 2000, easing = Easing { fraction ->
        val n1 = 7.5625f
        val d1 = 2.75f
        var newFraction = fraction

        return@Easing if (newFraction < 1f / d1) {
            n1 * newFraction * newFraction
        } else if (newFraction < 2f / d1) {
            newFraction -= 1.5f / d1
            n1 * newFraction * newFraction + 0.75f
        } else if (newFraction < 2.5f / d1) {
            newFraction -= 2.25f / d1
            n1 * newFraction * newFraction + 0.9375f
        } else {
            newFraction -= 2.625f / d1
            n1 * newFraction * newFraction + 0.984375f
        }
    }))
    LaunchedEffect(true) {
        showSword = true
        delay(2000)
        showLogo = true
        delay(1000)
        showSword = false
        moveLogo = true
        delay(2100)
        navController.popBackStack()
        navController.navigate(ScreenManager.LandingScreen.route)
    }
    DrawBackground()
    DrawSword(swordAnim.value)
    DrawLogo(modifier = Modifier
        .size(500.dp)
        .offset(y = logoMoveAnim.value), alpha = logoAlphaAnim.value)
}

@Preview
@Composable
fun PreviewLoadingScreen() {
    LoadingScreen(navController = rememberNavController())
}