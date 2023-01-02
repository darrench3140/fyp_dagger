package com.darren.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.screens.GameScreen
import com.darren.mygame.screens.LandingScreen
import com.darren.mygame.screens.LoadingScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = ScreenManager.LoadingScreen.route
            ) {
                composable(route = ScreenManager.LoadingScreen.route) {
                    LoadingScreen(navController = navController)
                }
                composable(route = ScreenManager.LandingScreen.route) {
                    LandingScreen(navController = navController)
                }
                composable(route = ScreenManager.GameScreen.route) {
                    GameScreen(navController = navController)
                }
            }
        }
    }
}