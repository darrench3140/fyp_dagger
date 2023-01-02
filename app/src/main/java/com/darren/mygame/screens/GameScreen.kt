package com.darren.mygame.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.GameState
import com.darren.mygame.components.Background
import com.darren.mygame.components.dagger

val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)

@Composable
fun GameScreen(navController : NavHostController) {

    val dagger = ImageBitmap.imageResource(id = dagger)


    Background()
    Canvas(modifier = Modifier.fillMaxSize()) {
        withTransform({
            scale(0.6f, 0.6f)
            translate(1000f, 2000f)
            rotate(0f)
        }) {
            drawImage(image = dagger)
        }
    }

}

@Preview
@Composable
fun PreviewGame() {
    GameScreen(navController = rememberNavController())
}