package com.darren.mygame.states

data class GameState(private val status: Status = Status.RUNNING) {
    enum class Status {
        RUNNING,
        STOPPED,
        LOSING
    }

    fun isRunning() = status == Status.RUNNING
    fun isStopped() = status == Status.STOPPED
    fun isLOSING() = status == Status.LOSING
}