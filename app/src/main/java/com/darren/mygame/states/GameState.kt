package com.darren.mygame.states

import com.darren.mygame.screens.gameState

data class GameState(private val status: Status = Status.RESET) {
    enum class Status {
        RESET,
        RUNNING,
        SHOOTING,
        NEXTLEVEL,
        LOSING,
        STOPPED,
    }

    fun isReset() = status == Status.RESET
    fun isRunning() = status == Status.RUNNING
    fun isShooting() = status == Status.SHOOTING
    fun isNextLevel() = status == Status.NEXTLEVEL
    fun isLosing() = status == Status.LOSING
    fun isStopped() = status == Status.STOPPED

    fun setReset() { gameState.value = GameState(Status.RESET) }
    fun setRunning() { gameState.value = GameState(Status.RUNNING) }
    fun setShooting() { gameState.value = GameState(Status.SHOOTING) }
    fun setNextLevel() { gameState.value = GameState(Status.NEXTLEVEL }
    fun setLosing() { gameState.value = GameState(Status.LOSING) }
    fun setStopped() { gameState.value = GameState(Status.STOPPED) }
}
