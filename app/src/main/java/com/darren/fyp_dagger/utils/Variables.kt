package com.darren.fyp_dagger.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darren.fyp_dagger.R
import com.darren.fyp_dagger.screens.SettingsUtil
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
val leftP : MutableState<Float> = mutableStateOf(1f)
val rightP : MutableState<Float> = mutableStateOf(1f)
val smileP : MutableState<Float> = mutableStateOf(0f)
val showTopScore : MutableState<Boolean> = mutableStateOf(false)
val showScoreBoardButton : MutableState<Boolean> = mutableStateOf(false)

//Game Data (Permanent)
val maxScore : MutableState<Int> = mutableStateOf(0)
val fruitCount : MutableState<Int> = mutableStateOf(0)
val fruitGained : MutableState<Int> = mutableStateOf(0)
val purchasedCount : MutableState<Int> = mutableStateOf(1)
val showCameraSettings: MutableState<Boolean> = mutableStateOf(true)
val cameraRotationSettings: MutableState<Float> = mutableStateOf(0f)
val cameraOutScaleSettings: MutableState<Float> = mutableStateOf(1f)
val cameraInScaleSettings: MutableState<Float> = mutableStateOf(1f)
val lensFacing : MutableState<Boolean> = mutableStateOf(false)
val faceLeftSensitivity: MutableState<Float> = mutableStateOf(0.2f)
val faceRightSensitivity: MutableState<Float> = mutableStateOf(0.2f)
val faceSmileSensitivity: MutableState<Float> = mutableStateOf(0.5f)
val textHelperOption: MutableState<Boolean> = mutableStateOf(false)

//Utils
val spinnerUtil = mutableStateOf(SpinnerUtil())
val daggerUtil = mutableStateOf(DaggerUtil())
val settingsUtil = mutableStateOf(SettingsUtil())

//Variables
val mutex = Mutex()
var screenHeightDp: Dp = 0.dp
var screenWidthDp: Dp = 0.dp
var screenHeightInt: Int = 0
var screenWidthInt: Int = 0

val myFont = FontFamily(Font(R.font.nineteenth))
val white = Color(0xFFD8D8D8)
val yellow = Color(0xFFFFB26B)

