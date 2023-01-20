package com.darren.fyp_dagger.screens

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.fyp_dagger.*
import com.darren.fyp_dagger.R
import com.darren.fyp_dagger.states.DaggerState
import com.darren.fyp_dagger.states.FruitState
import com.darren.fyp_dagger.states.RemainingDaggerState
import com.darren.fyp_dagger.states.SpinnerState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Composable
fun GameScreen(navController: NavHostController, gameData: GameData) {
    //Animations
    val animation = remember{ Animatable(initialValue = 0f) }
    val uiAlpha = animateFloatAsState(targetValue = if(gameState.value.isOver() || gameState.value.isLeveling()) 0f else 1f, animationSpec = tween(durationMillis = 500))
    val uiAlpha2 = animateFloatAsState(targetValue = if(gameState.value.isOver() || gameState.value.isLeveling()) 0f else 1f, animationSpec = tween(durationMillis = 100))
    val topBarOffset = animateDpAsState(targetValue = if (gameState.value.isOver()) (-200).dp else 0.dp, animationSpec = tween(durationMillis = 2000))
    val scoreBoardOffset = animateDpAsState(targetValue = if (gameState.value.isOver()) (-100).dp else -screenHeightDp-200.dp, animationSpec = tween(durationMillis = 500))
    val showTopScore = remember{ mutableStateOf(false) }
    val daggerHit = remember{ mutableStateOf(false) }
    val hitOffset = animateFloatAsState(targetValue = if (daggerHit.value) 20f else 0f, animationSpec = tween(durationMillis = if (!daggerHit.value) 100 else 0))
    val hitAlpha = animateFloatAsState(targetValue = if (daggerHit.value) 0.6f else 0f, animationSpec = tween(durationMillis = if (!daggerHit.value) 10 else 0))
    val fruitHit = remember{ mutableStateOf(0) }
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
    val fruitState = remember { FruitState(fruit, fruitCrack, spinSpeed, uiAlpha, hitOffset, fruitHit)}
    val showCamera = remember { mutableStateOf(false) }

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
            cameraReady.value = false
            gameLevel.value = 1
            GlobalScope.launch {
                delay(100)
                gameScore.value = 0
                fruitGained.value = 0
            }
            showCamera.value = !gameMode.value.isTap() || gameDifficulty.value == 3
            gameState.value.setReset()
        } else if (gameState.value.isReset()) {
            LevelUtil.updateLevelInfo(randomSpeed, clockwise, spinSpeed, minSpeed, maxSpeed, remainingDaggers)
            spinSpeed.value *= if(clockwise.value) 1f else -1f
            spinner.value = spinnerUtil.value.getRandomSpinner()
            val rotationList = daggerState.reset()
            fruitState.reset(rotationList)
            spinnerState.reset()
            gameState.value.setRunning()
            Log.d("game", "[Level Information]\nlevel: ${gameLevel.value}\nrandomSpeed: ${randomSpeed.value}\nspinSpeed: ${spinSpeed.value}\nminSpeed: ${minSpeed.value}\nmaxSpeed: ${maxSpeed.value}\nnumberOfDaggers: ${remainingDaggers.value}")
        } else if (gameState.value.isLeveling()) {
            gameLevel.value++
            delay(700)
            gameState.value.setReset()
        } else if (gameState.value.isOver()) {
            if (maxScore.value < gameScore.value) {
                maxScore.value = gameScore.value
                gameData.saveMaxScore(maxScore.value)
                showTopScore.value = true
            }
        }
    }
    // Special Handling for blink and crazy mode
    LaunchedEffect(gameDifficulty.value) {
        if (gameDifficulty.value == 3) { // Crazy Mode Handling
            while(true) {
                when ((1..5).random()) {
                    1 -> if (!gameMode.value.isTap()) gameMode.value.setTap() else gameMode.value.setSmile()
                    2 -> if (!gameMode.value.isSmile()) gameMode.value.setSmile() else gameMode.value.setLeft()
                    3 -> if (!gameMode.value.isLeft()) gameMode.value.setLeft() else gameMode.value.setRight()
                    4 -> if (!gameMode.value.isRight()) gameMode.value.setRight() else gameMode.value.setBoth()
                    5 -> if (!gameMode.value.isBoth()) gameMode.value.setBoth() else gameMode.value.setTap()
                }
                delay((3..7).random() * 1000L)
            }
        } else if (gameDifficulty.value == 2) {
            while(true) {
                when ((1..3).random()) {
                    1 -> if (!gameMode.value.isLeft()) gameMode.value.setLeft() else gameMode.value.setRight()
                    2 -> if (!gameMode.value.isRight()) gameMode.value.setRight() else gameMode.value.setBoth()
                    3 -> if (!gameMode.value.isBoth()) gameMode.value.setBoth() else gameMode.value.setLeft()
                }
                delay((3..7).random() * 1000L)
            }
        }
    }
    // Smile Control
    LaunchedEffect(gameMode.value.isSmile() && smileP.value > 0.5f) {
        if (gameMode.value.isSmile() && smileP.value > 0.5f && gameState.value.isRunning()) gameState.value.setShooting()
    }
    // Left Eye Control
    LaunchedEffect(gameMode.value.isLeft() && leftP.value < 0.2f && rightP.value > 0.5) {
        if (gameMode.value.isLeft() && leftP.value < 0.2f && rightP.value > 0.5 && gameState.value.isRunning()) gameState.value.setShooting()
    }
    // Right Eye Control
    LaunchedEffect(gameMode.value.isRight() && rightP.value < 0.2f && leftP.value > 0.5) {
        if (gameMode.value.isRight() && rightP.value < 0.2f && leftP.value > 0.5 && gameState.value.isRunning()) gameState.value.setShooting()
    }
    // Both Eye Control
    LaunchedEffect(gameMode.value.isBoth() && leftP.value < 0.2f && rightP.value < 0.2f) {
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
    // hit fruit
    LaunchedEffect(fruitHit.value) {
        fruitCount.value += fruitHit.value
        fruitGained.value += fruitHit.value
        fruitHit.value = 0
        gameData.saveFruitCount(fruitCount.value)
    }

    //UI
    DrawBackground()
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInteropFilter {
            if (gameMode.value.isTap()) {
                when (it.action) {
                    MotionEvent.ACTION_UP -> {
                        if (gameState.value.isRunning()) gameState.value.setShooting()
                    }
                }
            }
            true
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
    //Top Bar
    DrawTopBar(topBarOffset)
    DrawTopFruit()
    //Camera
    DrawCamera(showCamera)
    DrawControllerIcons()
    //Score Board
    DrawScoreBoard(navController, scoreBoardOffset, showTopScore)
}

fun DrawScope.midX(): Float { return ((size.width) / 2) }
fun DrawScope.midY(): Float { return ((size.height) / 2) }