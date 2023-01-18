package com.darren.mygame

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darren.mygame.states.GameState

//Screen Variables
var screenHeightDp: Dp = 0.dp
var screenWidthDp: Dp = 0.dp
var screenHeightInt: Int = 0
var screenWidthInt: Int = 0
//Game Data (temporary)
val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)
val gameLevel: MutableState<Int> = mutableStateOf(1)
//Game Data (Permanent)
val maxScore : MutableState<Int> = mutableStateOf(0)
val fruitCount : MutableState<Int> = mutableStateOf(0)
val purchasedCount : MutableState<Int> = mutableStateOf(1)
//Utils
val spinnerUtil = mutableStateOf(SpinnerUtil())
val daggerUtil = mutableStateOf(DaggerUtil())