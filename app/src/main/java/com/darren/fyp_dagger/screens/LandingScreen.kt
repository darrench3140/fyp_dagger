package com.darren.fyp_dagger.screens

import android.os.SystemClock
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.darren.fyp_dagger.utils.*
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
    DrawSettingsIcon {
        navController.navigate("settings_screen")
    }
    //Main Landing Screen
    Box(modifier = Modifier
        .fillMaxSize()
        .alpha(landingScreenAlpha.value)
        .offset(y = screenHeightDp + 20.dp - levelMenuOffset.value), contentAlignment = Alignment.Center) {
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
    //Game Mode Screen
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = levelMenuOffset.value)) 
    {
        Text(
            text = "GAME MODE",
            fontSize = 40.sp,
            color = white,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -screenHeightDp * 0.3f)
        )
        DrawGameModeItem(buttonText = "TAP", descriptionText = "Tap screen to shoot", rewardText = "x1", offsetY = -screenHeightDp * 0.18f) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 500L) {
                gameDifficulty.value = 0
                gameMode.value.setTap()
                gameState.value.setWipe()
                navController.navigate("game_screen")
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        DrawGameModeItem(buttonText = "SMILE", descriptionText = "Smile to shoot", rewardText = "x2", offsetY = screenHeightDp * 0.05f) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 500L) {
                if (PermissionUtil.hasPermission()) {
                    gameDifficulty.value = 1
                    gameMode.value.setSmile()
                    gameState.value.setWipe()
                    navController.navigate("game_screen")
                    lastClickTime = SystemClock.elapsedRealtime()
                } else PermissionUtil.requestCameraPermission()
            }
        }
        DrawGameModeItem(buttonText = "BLINK", descriptionText = "Blink both eyes to shoot", rewardText = "x3", offsetY = screenHeightDp * 0.23f) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 500L) {
                if (PermissionUtil.hasPermission()) {
                    gameDifficulty.value = 2
                    gameMode.value.setBoth()
                    gameState.value.setWipe()
                    navController.navigate("game_screen")
                    lastClickTime = SystemClock.elapsedRealtime()
                } else PermissionUtil.requestCameraPermission()
            }
        }
        DrawReturnButton(offsetY = (-50).dp) {
            showLevelMenu.value = false
        }
    }
}