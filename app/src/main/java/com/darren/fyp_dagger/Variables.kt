package com.darren.fyp_dagger

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darren.fyp_dagger.states.GameMode
import com.darren.fyp_dagger.states.GameState
import kotlinx.coroutines.sync.Mutex

//Game Data (temporary)
val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameMode: MutableState<GameMode> = mutableStateOf(GameMode())
val gameDifficulty: MutableState<Int> = mutableStateOf(0)
val gameScore: MutableState<Int> = mutableStateOf(0)
val gameLevel: MutableState<Int> = mutableStateOf(1)
val cameraReady: MutableState<Boolean> = mutableStateOf(false)
val showCamera: MutableState<Boolean> = mutableStateOf(false)
val leftP = mutableStateOf(1f)
val rightP = mutableStateOf(1f)
val smileP = mutableStateOf(0f)
val showTopScore = mutableStateOf(false)
val showScoreBoardButton = mutableStateOf(false)

//Game Data (Permanent)
val maxScore : MutableState<Int> = mutableStateOf(0)
val fruitCount : MutableState<Int> = mutableStateOf(0)
val fruitGained : MutableState<Int> = mutableStateOf(0)
val purchasedCount : MutableState<Int> = mutableStateOf(1)

//Utils
val spinnerUtil = mutableStateOf(SpinnerUtil())
val daggerUtil = mutableStateOf(DaggerUtil())

//Variables
val mutex = Mutex()
var screenHeightDp: Dp = 0.dp
var screenWidthDp: Dp = 0.dp
var screenHeightInt: Int = 0
var screenWidthInt: Int = 0

val myFont = FontFamily(Font(R.font.nineteenth))
val white = Color(0xFFD8D8D8)
val yellow = Color(0xFFFFB26B)

