package com.darren.mygame

data class GameState(private val status: Status = Status.RUNNING) {
    enum class Status {
        RUNNING,
        STOPPED
    }

    fun isRunning() = status == Status.RUNNING
    fun isStopped() = status == Status.STOPPED
}