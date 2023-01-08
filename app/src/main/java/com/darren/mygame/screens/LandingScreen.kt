package com.darren.mygame.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.*
import com.darren.mygame.states.GameState

@Composable
fun LandingScreen(navController : NavHostController) {
    DrawBackground()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DrawLogo(modifier = Modifier
            .size(500.dp)
            .offset(y = (-60).dp))
        DrawDagger(modifier = Modifier
            .size(120.dp)
            .offset(y = (-80).dp))
        DrawButton("Play", 0.dp, (-90).dp) {
            gameState.value.setReset()
            navController.navigate(ScreenManager.GameScreen.route)
        }
    }
}



@Preview
@Composable
fun PreviewAct() {
    LandingScreen(navController = rememberNavController())
}