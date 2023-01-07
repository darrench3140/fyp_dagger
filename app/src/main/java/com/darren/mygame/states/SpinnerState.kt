package com.darren.mygame.states

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform

data class SpinnerState(val image: ImageBitmap) {
    private val imgWidth = 220
    private val imgHeight = 220

    private var currentRotation = 0f
    private var randomSpeed = false
    private var minSpeed = 1
    private var maxSpeed = 2
    private var spinSpeed = 1f

    fun spin() {
        if (randomSpeed) spinSpeed = (minSpeed..maxSpeed).random().toFloat()
        currentRotation += spinSpeed
    }

    fun draw(drawScope: DrawScope) {
        return drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        withTransform({
            rotate(currentRotation)
        }) {
            drawImage(image = image)
        }
    }
}