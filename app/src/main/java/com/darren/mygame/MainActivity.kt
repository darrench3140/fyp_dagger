package com.darren.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darren.mygame.screens.GameScreen
import com.darren.mygame.screens.LandingScreen
import com.darren.mygame.screens.LoadingScreen
import com.darren.mygame.screens.ScoreScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

var screenHeight: Dp = 0.dp
var screenWidth: Dp = 0.dp

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val splashScreen = installSplashScreen()
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            val configuration = LocalConfiguration.current
            screenHeight = configuration.screenHeightDp.dp
            screenWidth = configuration.screenWidthDp.dp

            val navController = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navController,
                startDestination = ScreenManager.LandingScreen.route
            ) {
                composable(route = ScreenManager.LoadingScreen.route) {
                    LoadingScreen(navController = navController)
                }
                composable(route = ScreenManager.LandingScreen.route) {
                    LandingScreen(navController = navController)
                }
                composable(
                    route = ScreenManager.GameScreen.route,
//                    exitTransition = {
//                        slideOutVertically(
//                            targetOffsetY = {1000},
//                            animationSpec = tween(durationMillis = 100, easing = LinearEasing)
//                        )
//                    }
                ) {
                    GameScreen(navController = navController)
                }
            }
        }
    }
}