package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import com.darren.mygame.screens.gameState

data class GameState(private val status: Status = Status.RUNNING) {
    enum class Status {
        RESET,
        RUNNING,
        SHOOTING,
        LOSING,
        STOPPED,
    }

    fun isReset() = status == Status.RESET
    fun isRunning() = status == Status.RUNNING
    fun isShooting() = status == Status.SHOOTING
    fun isLosing() = status == Status.LOSING
    fun isStopped() = status == Status.STOPPED

    fun setReset() { gameState.value = GameState(Status.RESET) }
    fun setRunning() { gameState.value = GameState(Status.RUNNING) }
    fun setShooting() { gameState.value = GameState(Status.SHOOTING) }
    fun setLosing() { gameState.value = GameState(Status.LOSING) }
    fun setStopped() { gameState.value = GameState(Status.STOPPED) }
}