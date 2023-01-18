package com.darren.mygame.screens

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import com.darren.mygame.DrawBackground
import com.darren.mygame.DrawLogo
import com.darren.mygame.DrawSword
import com.darren.mygame.screenHeightDp
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavHostController) {
    var showSword by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }
    var moveLogo by remember { mutableStateOf(false) }
    val swordAnim = animateFloatAsState(targetValue = if(showSword) 0.6f else 0f, animationSpec = tween(durationMillis = 2500))
    val logoAlphaAnim = animateFloatAsState(targetValue = if(showLogo) 1f else 0f, animationSpec = tween(durationMillis = 2000))
    val logoMoveAnim = animateDpAsState(
        targetValue = if(moveLogo) -screenHeightDp.times(0.25f) else 130.dp,
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
        showSword = false
        delay(1000)
        moveLogo = true
        delay(2100)
        navController.popBackStack()
        navController.navigate("landing_screen")
    }
    DrawBackground()
    DrawSword(swordAnim.value)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DrawLogo(modifier = Modifier
            .size(300.dp)
            .offset(y = logoMoveAnim.value), alpha = logoAlphaAnim.value
        )
    }
}