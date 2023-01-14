package com.darren.mygame

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darren.mygame.screens.*
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
            OrientationUtil.LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

            val configuration = LocalConfiguration.current
            screenHeight = configuration.screenHeightDp.dp
            screenWidth = configuration.screenWidthDp.dp

            val context = LocalContext.current
            val gameData = GameSetUpUtil.loadSettings(context)

            // 3 gameModes, "god" "reset" "normal"
            GameSetUpUtil.SetGameMode(gameData, mode = "normal")

            val savedDaggerInUseID = gameData.getDaggerInUseID.collectAsState(initial = 1)
            daggerUtil.Init(savedDaggerInUseID.value)
            spinnerUtil.Init()

            val navController = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navController,
                startDestination = "landing_screen"
            ) {
                composable(
                    route = "loading_screen",
                    exitTransition = { fadeOut(animationSpec = tween(durationMillis = 100, 500)) },
                ) {
                    LoadingScreen(navController)
                }
                composable(
                    route = "landing_screen",
                    enterTransition = { when (initialState.destination.route) {
                        "game_screen" -> slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
                        else -> fadeIn(animationSpec = tween(500))
                    }},
                    exitTransition = { when (targetState.destination.route) {
                        "game_screen" -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
                        else -> fadeOut(animationSpec = tween(100, 500))
                    }}
                ) {
                    LandingScreen(navController)
                }
                composable(
                    route = "game_screen",
                    enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))},
                    exitTransition = { fadeOut(animationSpec = tween(100, 500)) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))},
                ) {
                    GameScreen(navController, gameData)
                }
                composable(
                    route = "shop_screen",
                    enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(500)) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(500, 500))}
                ) {
                    ShopScreen(navController)
                }
            }
        }
    }
}