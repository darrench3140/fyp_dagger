package com.darren.mygame

sealed class ScreenManager(val route: String) {
    object LoadingScreen : ScreenManager("loading_screen")
    object LandingScreen : ScreenManager("landing_screen")
    object GameScreen : ScreenManager("game_screen")
    object ScoreScreen : ScreenManager("score_screen")
}