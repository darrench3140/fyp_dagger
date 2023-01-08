package com.darren.mygame.screens

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.DrawBackground
import com.darren.mygame.R
import com.darren.mygame.states.DaggerState
import com.darren.mygame.states.GameState
import com.darren.mygame.states.SpinnerState
import com.darren.mygame.states.daggerImg

val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(navController : NavHostController) {

    val animation = remember{ Animatable(initialValue = 0f) }
    val spinSpeed by remember{ mutableStateOf(3f) }

    val dagger = ImageBitmap.imageResource(id = daggerImg)
    val spinner = ImageBitmap.imageResource(id = R.drawable.spinner1)

    val daggerState = remember { DaggerState(dagger, spinSpeed) }
    val spinnerState = remember { SpinnerState(spinner, spinSpeed) }

    LaunchedEffect(animation) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 250, easing = LinearEasing)
            )
        )
    }

    DrawBackground()
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_UP -> {
                    if (gameState.value.isRunning()) {
                        daggerState.shoot()
                    }
                }
            }
            true
        }) {
        animation.value //use to maintain animation loop

        spinnerState.spin()

        daggerState.draw(this)
        spinnerState.draw(this)

    }
}

fun DrawScope.fullX(): Float { return size.width }
fun DrawScope.fullY(): Float { return size.height }
fun DrawScope.midX(): Float { return ((size.width) / 2) }
fun DrawScope.midY(): Float { return ((size.height) / 2) }

@Preview
@Composable
fun PreviewGame() {
    GameScreen(navController = rememberNavController())
}