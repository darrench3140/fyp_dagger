package com.darren.mygame

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.darren.mygame.states.GameState

//Game Data (temporary)
val gameState: MutableState<GameState> = mutableStateOf(GameState())
val gameScore: MutableState<Int> = mutableStateOf(0)
val gameLevel: MutableState<Int> = mutableStateOf(1)
//Game Data (Permanent)
val maxScore : MutableState<Int> = mutableStateOf(0)
val fruitCount : MutableState<Int> = mutableStateOf(0)
val purchasedCount : MutableState<Int> = mutableStateOf(1)
//Utils
val spinnerUtil = SpinnerUtil()
val daggerUtil = DaggerUtil()