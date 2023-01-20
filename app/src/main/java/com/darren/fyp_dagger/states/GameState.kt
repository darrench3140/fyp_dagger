package com.darren.fyp_dagger.states

import com.darren.fyp_dagger.gameMode
import com.darren.fyp_dagger.gameState

data class GameState(private val status: Status = Status.RESET) {
    enum class Status {
        WIPE,
        RESET,
        RUNNING,
        SHOOTING,
        LEVELING,
        LOSING,
        OVER,
    }

    fun isWipe() = status == Status.WIPE
    fun isReset() = status == Status.RESET
    fun isRunning() = status == Status.RUNNING
    fun isShooting() = status == Status.SHOOTING
    fun isLeveling() = status == Status.LEVELING
    fun isLosing() = status == Status.LOSING
    fun isOver() = status == Status.OVER

    fun setWipe() { gameState.value = GameState(Status.WIPE)}
    fun setReset() { gameState.value = GameState(Status.RESET) }
    fun setRunning() { gameState.value = GameState(Status.RUNNING) }
    fun setShooting() { gameState.value = GameState(Status.SHOOTING) }
    fun setLeveling() { gameState.value = GameState(Status.LEVELING) }
    fun setLosing() { gameState.value = GameState(Status.LOSING) }
    fun setOver() { gameState.value = GameState(Status.OVER) }
}

data class GameMode(private val mode: Mode = Mode.TAP) {

    enum class Mode {
        TAP,
        SMILE,
        BLINK
    }

    fun isTap() = mode == Mode.TAP
    fun isSmile() = mode == Mode.SMILE
    fun isBlink() = mode == Mode.BLINK

    fun setTap() { gameMode.value = GameMode(Mode.TAP) }
    fun setSmile() { gameMode.value = GameMode(Mode.SMILE) }
    fun setBlink() { gameMode.value = GameMode(Mode.BLINK) }
}