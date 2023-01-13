package com.darren.mygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darren.mygame.screens.GameScreen
import com.darren.mygame.screens.LandingScreen
import com.darren.mygame.screens.LoadingScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

var screenHeight: Dp = 0.dp
var screenWidth: Dp = 0.dp
val spinnerUtil = SpinnerUtil()
val daggerUtil = DaggerUtil()

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
            spinnerUtil.Init()
            daggerUtil.Init(16)
            val navController = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navController,
                startDestination = "loading_screen"
            ) {
                composable(
                    route = "loading_screen",
                    exitTransition = { fadeOut(animationSpec = tween(durationMillis = 5000)) },
                ) {
                    LoadingScreen(navController = navController)
                }
                composable(
                    route = "landing_screen",
                    enterTransition = { when (initialState.destination.route) {
                        "game_screen" -> slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
                        else -> fadeIn(animationSpec = tween(500))
                    }},
                    exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))},
                ) {
                    LandingScreen(navController = navController)
                }
                composable(
                    route = "game_screen",
                    enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))},
                    popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))},
                ) {
                    GameScreen(navController = navController)
                }
            }
        }
    }
}