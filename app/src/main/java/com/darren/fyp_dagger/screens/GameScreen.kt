package com.darren.fyp_dagger.screens

import android.os.SystemClock
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.fyp_dagger.R
import com.darren.fyp_dagger.states.DaggerState
import com.darren.fyp_dagger.states.FruitState
import com.darren.fyp_dagger.states.RemainingDaggerState
import com.darren.fyp_dagger.states.SpinnerState
import com.darren.fyp_dagger.utils.*
import kotlinx.coroutines.delay

@Composable
fun GameScreen(navController: NavHostController, gameData: GameData) {
    //UI
    var lastClickTime by remember { mutableStateOf(0L) }
    DrawBackground()
    GameConsole(navController, gameData)
    //Top and bottom Bar
    DrawTopBar()
    DrawTopFruit()
    DrawReturnButton(offsetY = (-50).dp) {
        if (SystemClock.elapsedRealtime() - lastClickTime > 500L) {
            navController.popBackStack()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }
    //Camera
    DrawCamera()
    if (textHelperOption.value) DrawControllerIcons()
}

@Composable
fun GameConsole(navController: NavHostController, gameData: GameData) {
    //Animations
    val animation = remember{ Animatable(initialValue = 0f) }
    val uiAlpha = animateFloatAsState(targetValue = if(gameState.value.isOver() || gameState.value.isLeveling()) 0f else 1f, animationSpec = tween(durationMillis = 500))
    val uiAlpha2 = animateFloatAsState(targetValue = if(gameState.value.isOver() || gameState.value.isLeveling()) 0f else 1f, animationSpec = tween(durationMillis = 100))
    val daggerHit = remember{ mutableStateOf(false) }
    val hitOffset = animateFloatAsState(targetValue = if (daggerHit.value) 20f else 0f, animationSpec = tween(durationMillis = if (!daggerHit.value) 100 else 0))
    val hitAlpha = animateFloatAsState(targetValue = if (daggerHit.value) 0.6f else 0f, animationSpec = tween(durationMillis = if (!daggerHit.value) 10 else 0))

    //Game Settings
    val randomSpeed = remember{ mutableStateOf(false) }
    val clockwise = remember{ mutableStateOf(false) }
    val spinSpeed = remember{ mutableStateOf(0f) }
    val minSpeed = remember{ mutableStateOf(0) }
    val maxSpeed = remember{ mutableStateOf(0) }
    val remainingDaggers = remember{ mutableStateOf(0) }

    //Game Resources
    val spinner = remember{ mutableStateOf(spinnerUtil.value.getRandomSpinner()) }
    val cover = ImageBitmap.imageResource(id = R.drawable.spinner0)
    val remainingDagger = ImageBitmap.imageResource(id = R.drawable.remaining_dagger)
    val fruit = ImageBitmap.imageResource(id = R.drawable.fruit)
    val fruitCrack = ImageBitmap.imageResource(id = R.drawable.fruit_crack)
    val sparkle = ImageBitmap.imageResource(id = R.drawable.sparkle)
    val daggerState = remember { DaggerState(spinSpeed, remainingDaggers, uiAlpha2, hitOffset, sparkle) }
    val spinnerState = remember { SpinnerState(spinner, cover, spinSpeed, uiAlpha, hitAlpha, hitOffset) }
    val remainingDaggerState = remember { RemainingDaggerState(remainingDagger, remainingDaggers, uiAlpha) }
    val fruitState = remember { FruitState(fruit, fruitCrack, spinSpeed, uiAlpha, hitOffset, gameData)}

    // game animation and camera controller coroutine
    LaunchedEffect(true) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = 250, easing = LinearEasing))
        )
    }
    // game state handling for important events
    LaunchedEffect(gameState.value) {
        if (gameState.value.isWipe()) {
            gameLevel.value = 1
            showCamera.value = gameDifficulty.value != 0
            cameraReady.value = false
            showScoreBoardButton.value = false
            delay(100) //Delay for score board scores to show longer
            showTopScore.value = false
            gameScore.value = 0
            fruitGained.value = 0
            gameState.value.setReset()
        } else if (gameState.value.isReset()) {
            GameUtil.updateLevelInfo(randomSpeed, clockwise, spinSpeed, minSpeed, maxSpeed, remainingDaggers)
            spinSpeed.value *= if(clockwise.value) 1f else -1f
            spinner.value = spinnerUtil.value.getRandomSpinner()
            val rotationList = daggerState.reset()
            fruitState.reset(rotationList)
            spinnerState.reset()
            gameState.value.setRunning()
            Log.d("game", "[Level Information]\nlevel: ${gameLevel.value}\nrandomSpeed: ${randomSpeed.value}\nspinSpeed: ${spinSpeed.value}\nminSpeed: ${minSpeed.value}\nmaxSpeed: ${maxSpeed.value}\nnumberOfDaggers: ${remainingDaggers.value}")
        } else if (gameState.value.isLeveling()) {
            gameLevel.value++
            var loopCnt = when (gameDifficulty.value) { 1 -> 2; 2 -> 3; 3 -> 5; else -> 0 }
            while(loopCnt > 0) {
                fruitState.addBonusFruit()
                delay(100)
                loopCnt--
            }
            delay(500)
            gameState.value.setReset()
        } else if (gameState.value.isOver()) {
            if (maxScore.value < gameScore.value) {
                maxScore.value = gameScore.value
                gameData.saveMaxScore(maxScore.value)
                showTopScore.value = true
            }
        }
    }
    // Smile Control
    LaunchedEffect( mlModeOption.value && gameMode.value.isSmile() && smileP.value > faceSmileSensitivity.value) {
        if (gameMode.value.isSmile() && smileP.value > 0.5f && gameState.value.isRunning()) gameState.value.setShooting()
    }
    // Landmark Smile Control
    LaunchedEffect(!mlModeOption.value && gameMode.value.isSmile() && smiling.value) {
        if (gameMode.value.isSmile() && smiling.value && gameState.value.isRunning()) gameState.value.setShooting()
    }
    // Both Eye Control
    LaunchedEffect(gameMode.value.isBoth() && leftP.value < faceLeftSensitivity.value && rightP.value < faceRightSensitivity.value) {
        if (gameMode.value.isBoth() && leftP.value < 0.2f && rightP.value < 0.2f && gameState.value.isRunning()) gameState.value.setShooting()
    }
    // spin speed controller coroutine
    LaunchedEffect(randomSpeed.value) {
        while(randomSpeed.value) {
            spinSpeed.value = (minSpeed.value..maxSpeed.value).random().toFloat() * (if(clockwise.value) 1 else -1)
            delay((1..6).random() * 500L)
        }
    }
    // hit animation
    LaunchedEffect(daggerHit.value) {
        if (daggerHit.value) {
            delay(50)
            daggerHit.value = false
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (gameMode.value.isTap() && gameState.value.isRunning()) gameState.value.setShooting()
                }
            )
        }
    ) {
        animation.value //use to maintain animation loop
        if (gameState.value.isShooting() || gameState.value.isLeveling()) {
            daggerState.shoot {
                daggerHit.value = true
                fruitState.hit()
            }
        } else if (gameState.value.isLosing()) {
            spinSpeed.value = 0f
            daggerState.drop()
        }
        daggerState.draw(this)
        fruitState.draw(this)
        spinnerState.draw(this)
        remainingDaggerState.draw(this)
    }
    //Score Board
    DrawScoreBoard(navController, fruitState)
}

fun DrawScope.midX(): Float { return ((size.width) / 2) }
fun DrawScope.midY(): Float { return ((size.height) / 2) }