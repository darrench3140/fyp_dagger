package com.darren.mygame.screens

import android.util.Log
import android.view.MotionEvent
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.DrawBackground
import com.darren.mygame.R
import com.darren.mygame.myFont
import com.darren.mygame.states.DaggerState
import com.darren.mygame.states.GameState
import com.darren.mygame.states.SpinnerState
import com.darren.mygame.states.daggerImg
import kotlinx.coroutines.delay

val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(navController : NavHostController) {

    val animation = remember{ Animatable(initialValue = 0f) }
    var spinSpeed by remember{ mutableStateOf(3f) }

    var currentLevel by remember { mutableStateOf(1) }

    var randomSpeed = true
    var minSpeed = 1
    var maxSpeed = 10

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
    LaunchedEffect(true) {
        while(true) {
            if (randomSpeed) {
                spinSpeed = (minSpeed..maxSpeed).random().toFloat()
                delay(1000)
            } else {
                delay(5000)
            }
        }
    }
    Log.d("logger", "1")
    DrawBackground()

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_UP -> {
                    if (gameState.value.isRunning()) {
//                        gameState.value.setShooting()
                        daggerState.shoot()
                    }
                }
            }
            true
        }) {
        animation.value //use to maintain animation loop

        if (gameState.value.isReset()) {
            daggerState.reset()
            gameState.value.setRunning()
        }
        spinnerState.spin()
        daggerState.draw(this)
        spinnerState.draw(this)
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
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
            text = "STAGE $currentLevel",
            fontSize = 30.sp,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(0.5f))
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