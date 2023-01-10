package com.darren.mygame.screens

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*
import com.darren.mygame.R
import com.darren.mygame.states.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.delay

val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)
val gameLevel: MutableState<Int> = mutableStateOf(1)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(navController : NavHostController) {
    //Animations
    val animation = remember{ Animatable(initialValue = 0f) }
    var showScoreBoard by remember { mutableStateOf(false) }
    val uiAlpha = animateFloatAsState(targetValue = if(showScoreBoard || gameState.value.isNextLevel()) 0f else 1f, animationSpec = tween(durationMillis = 100))
    val topBarOffset = animateDpAsState(targetValue = if (showScoreBoard) (-200).dp else 0.dp, animationSpec = tween(durationMillis = 2000))
    val scoreBoardOffset = animateDpAsState(targetValue = if (showScoreBoard) (-70).dp else (-1000).dp, animationSpec = tween(durationMillis = 500))

    val spinSpeed = remember{ mutableStateOf(0f) }
    val randomSpeed = remember{ mutableStateOf(false) }
    val minSpeed = remember{ mutableStateOf(0) }
    val maxSpeed = remember{ mutableStateOf(0) }
    val remainingDaggers = remember{ mutableStateOf(0) }

    val dagger = ImageBitmap.imageResource(id = daggerImg)
    val spinner = ImageBitmap.imageResource(id = spinnerImg)
    val remainingDagger = ImageBitmap.imageResource(id = R.drawable.remaining_dagger)

    val daggerState = remember { DaggerState(dagger, spinSpeed, remainingDaggers, uiAlpha) }
    val spinnerState = remember { SpinnerState(spinner, spinSpeed, uiAlpha) }
    val remainingDaggerState = remember { RemainingDaggerState(remainingDagger, remainingDaggers) }

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
            spinSpeed.value = (minSpeed.value..maxSpeed.value).random().toFloat()
            delay(2000)
        }
    }
    // score board controller coroutine
    LaunchedEffect(gameState.value.isStopped()) {
        showScoreBoard = gameState.value.isStopped()
    }
    LaunchedEffect(gameState.value.isNextLevel() {
        if (gameState.value.isNextLevel()) {
            currentLevel.value++
            delay(1000)
            //random spinner
            gameState.value.setReset()
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

        if (gameState.value.isReset()) {
            LevelUtil.updateLevelInfo(randomSpeed, spinSpeed, minSpeed, maxSpeed, remainingDaggers)
            daggerState.reset()
            spinnerState.reset()
            gameState.value.setRunning()
            Log.d("game", "[Level Information]\nlevel: ${gameLevel.value}\nrandomSpeed: ${randomSpeed.value}\nspinSpeed: ${spinSpeed.value}\nminSpeed: ${minSpeed.value}\nmaxSpeed: ${maxSpeed.value}\nnumberOfDaggers: ${remainingDaggers.value}")
        } else if (gameState.value.isShooting() || gameState.value.isNextLevel()) {
            daggerState.shoot()
        } else if (gameState.value.isLosing()) {
            spinSpeed.value = 0f
            daggerState.drop()
        }

        spinnerState.spin()
        daggerState.draw(this)
        spinnerState.draw(this)
    }

    //Top Bar
    DrawTopBar(topBarOffset)

    //Score Board
    DrawScoreBoard(navController, scoreBoardOffset)
}

fun DrawScope.midX(): Float { return ((size.width) / 2) }
fun DrawScope.midY(): Float { return ((size.height) / 2) }

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun PreviewGame() {
    GameScreen(navController = rememberAnimatedNavController())
}
