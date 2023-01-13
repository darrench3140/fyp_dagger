package com.darren.mygame.screens

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*
import com.darren.mygame.R
import com.darren.mygame.states.*
import kotlinx.coroutines.delay

//Global variables
val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)
val gameLevel: MutableState<Int> = mutableStateOf(1)

//Game Settings
val maxScore: MutableState<Int> = mutableStateOf(0)
val fruitCount: MutableState<Int> = mutableStateOf(0)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(navController: NavHostController) {
    //Animations
    val animation = remember{ Animatable(initialValue = 0f) }
    val uiAlpha = animateFloatAsState(targetValue = if(gameState.value.isOver() || gameState.value.isLeveling()) 0f else 1f, animationSpec = tween(durationMillis = 500))
    val uiAlpha2 = animateFloatAsState(targetValue = if(gameState.value.isOver() || gameState.value.isLeveling()) 0f else 1f, animationSpec = tween(durationMillis = 100))
    val topBarOffset = animateDpAsState(targetValue = if (gameState.value.isOver()) (-200).dp else 0.dp, animationSpec = tween(durationMillis = 2000))
    val scoreBoardOffset = animateDpAsState(targetValue = if (gameState.value.isOver()) (-100).dp else (-1000).dp, animationSpec = tween(durationMillis = 500))
    val showTopScore = remember{ mutableStateOf(false) }
    val hit = remember{ mutableStateOf(false) }
    val hitOffset = animateFloatAsState(targetValue = if (hit.value) 20f else 0f, animationSpec = tween(durationMillis = if (!hit.value) 100 else 0))
    val hitAlpha = animateFloatAsState(targetValue = if (hit.value) 0.6f else 0f, animationSpec = tween(durationMillis = if (!hit.value) 10 else 0))
    //Game Settings
    val randomSpeed = remember{ mutableStateOf(false) }
    val clockwise = remember{ mutableStateOf(false) }
    val spinSpeed = remember{ mutableStateOf(0f) }
    val minSpeed = remember{ mutableStateOf(0) }
    val maxSpeed = remember{ mutableStateOf(0) }
    val remainingDaggers = remember{ mutableStateOf(0) }
    //Game Resources
    val daggerState = remember { DaggerState(spinSpeed, remainingDaggers, uiAlpha2, hitOffset) }
    val spinner = remember{ mutableStateOf(spinnerUtil.getRandomSpinner()) }
    val cover = ImageBitmap.imageResource(id = R.drawable.spinner0)
    val spinnerState = remember { SpinnerState(spinner, cover, spinSpeed, uiAlpha, hitAlpha, hitOffset) }
    val remainingDagger = ImageBitmap.imageResource(id = R.drawable.remaining_dagger)
    val remainingDaggerState = remember { RemainingDaggerState(remainingDagger, remainingDaggers, uiAlpha) }
    val lastScore = remember{ mutableStateOf(0) }
    // game animation controller coroutine
    LaunchedEffect(true) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 250, easing = LinearEasing)
            )
        )
    }
    // spin speed controller coroutine
    LaunchedEffect(randomSpeed.value) {
        while(randomSpeed.value) {
            spinSpeed.value = (minSpeed.value..maxSpeed.value).random().toFloat() * (if(clockwise.value) 1 else -1)
            delay(2000)
        }
    }
    // game state handling for important events
    LaunchedEffect(gameState.value) {
        if (gameState.value.isReset()) {
            LevelUtil.updateLevelInfo(randomSpeed, clockwise, spinSpeed, minSpeed, maxSpeed, remainingDaggers)
            spinSpeed.value *= if(clockwise.value) 1f else -1f
            spinner.value = spinnerUtil.getRandomSpinner()
            daggerState.reset()
            spinnerState.reset()
            gameState.value.setRunning()
            Log.d("game", "[Level Information]\nlevel: ${gameLevel.value}\nrandomSpeed: ${randomSpeed.value}\nspinSpeed: ${spinSpeed.value}\nminSpeed: ${minSpeed.value}\nmaxSpeed: ${maxSpeed.value}\nnumberOfDaggers: ${remainingDaggers.value}")
        } else if (gameState.value.isLeveling()) {
            gameLevel.value++
            delay(700)
            gameState.value.setReset()
        } else if (gameState.value.isOver()) {
            lastScore.value = gameScore.value
            if (maxScore.value < gameScore.value) {
                maxScore.value = gameScore.value
                showTopScore.value = true
            }
        }
    }
    // hit animation
    LaunchedEffect(hit.value) {
        if (hit.value) {
            delay(50)
            hit.value = false
        }
    }

    DrawBackground()
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_UP -> {
                    if (gameState.value.isRunning()) {
                        gameState.value.setShooting()
                    }
                }
            }
            true
        }) {
        animation.value //use to maintain animation loop
//        Log.d("game", "ticking with state: ${gameState.value}")

        if (gameState.value.isShooting() || gameState.value.isLeveling()) {
            daggerState.shoot {
                hit.value = true
            }
        } else if (gameState.value.isLosing()) {
            spinSpeed.value = 0f
            daggerState.drop()
        }
        daggerState.draw(this)
        spinnerState.draw(this)
        remainingDaggerState.draw(this)
    }

    //Top Bar
    DrawTopBar(topBarOffset)
    DrawTopFruit()

    //Score Board
    DrawScoreBoard(navController, scoreBoardOffset, showTopScore, lastScore)
}

fun DrawScope.midX(): Float { return ((size.width) / 2) }
fun DrawScope.midY(): Float { return ((size.height) / 2) }

fun gameReset() {
    gameScore.value = 0
    gameLevel.value = 1
    gameState.value.setReset()
}