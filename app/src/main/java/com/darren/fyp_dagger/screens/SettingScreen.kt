package com.darren.fyp_dagger.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darren.fyp_dagger.DrawBackground
import com.darren.fyp_dagger.DrawReturnButton
import com.darren.fyp_dagger.GameData

@Composable
fun Settings(navController: NavController, gameData: GameData) {
    DrawBackground()



    DrawReturnButton(offsetY = (-50).dp) {
        navController.popBackStack()
    }
}

/*
Camera settings:
    rotation
    position
    scale
Facial Detection Sensitivity:
    Left right Eye blink probability
    Smiling probability

 */