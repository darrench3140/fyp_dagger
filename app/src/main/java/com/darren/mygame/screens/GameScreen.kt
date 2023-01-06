package com.darren.mygame.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.states.GameState
import com.darren.mygame.states.DaggerState
import com.darren.mygame.DrawBackground
import com.darren.mygame.states.daggerImg

val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)

@Composable
fun GameScreen(navController : NavHostController) {

    val dagger = ImageBitmap.imageResource(id = daggerImg)
    val daggerState = remember(dagger) { DaggerState(dagger) }

    DrawBackground()
    Canvas(modifier = Modifier.fillMaxSize()) {

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