package com.darren.mygame.screens

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.darren.mygame.DrawBackground
import com.darren.mygame.R
import com.darren.mygame.myFont
import com.darren.mygame.states.DaggerState
import com.darren.mygame.states.GameState
import com.darren.mygame.states.SpinnerState
import com.darren.mygame.states.daggerImg
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.delay

val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(navController : NavHostController) {

    val startGameAnim = remember{ mutableStateOf(true) }
    val animation = remember{ Animatable(initialValue = 0f) }
    val spinSpeed = remember{ mutableStateOf(3f) }
    val currentLevel = remember { mutableStateOf(1) }

    val randomSpeed = remember{ mutableStateOf(false) }
    var minSpeed = 1
    var maxSpeed = 5

    val dagger = ImageBitmap.imageResource(id = daggerImg)
    val spinner = ImageBitmap.imageResource(id = R.drawable.spinner1)

    val daggerState = remember { DaggerState(dagger, spinSpeed) }
    val spinnerState = remember { SpinnerState(spinner, spinSpeed) }

    // game animation controller coroutine
    LaunchedEffect(startGameAnim.value) {
        if (startGameAnim.value) {
            animation.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 250, easing = LinearEasing)
                )
            )
        } else {
            animation.stop()
        }
    }
    // spin speed controller coroutine
    LaunchedEffect(randomSpeed.value) {
        while(randomSpeed.value) {
            spinSpeed.value = (minSpeed..maxSpeed).random().toFloat()
            delay(2000)
        }
        if (!randomSpeed.value) { spinSpeed.value = 3f }
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
        Log.d("game", "recompose ${gameState.value}")
        if (gameState.value.isReset()) {
            daggerState.reset()
            gameState.value.setRunning()
        } else if (gameState.value.isShooting()) {
            daggerState.shoot()
        } else if (gameState.value.isLosing()) {
            spinSpeed.value = 0f
            daggerState.drop(navController)
        }

        spinnerState.spin()
        daggerState.draw(this)
        spinnerState.draw(this)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp)) {
        Spacer(modifier = Modifier.weight(0.1f))
        Text( //Score
            text = gameScore.value.toString(),
            modifier = Modifier.weight(0.2f),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            color = Color(0xFFFFB26B),
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Text(
            text = "STAGE ${currentLevel.value}",
            fontSize = 30.sp,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1F6F5)
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }

}

fun DrawScope.midX(): Float { return ((size.width) / 2) }
fun DrawScope.midY(): Float { return ((size.height) / 2) }

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun PreviewGame() {
    GameScreen(navController = rememberAnimatedNavController())
}