package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY

data class SpinnerState(val image: ImageBitmap, var spinSpeed: Float) {
    private val imgWidth = 220
    private val imgHeight = 220

    private var currentRotation = 0f
    private var randomSpeed = true //need fix this bug
    private var minSpeed = 1
    private var maxSpeed = 10

    fun spin() {
        if (randomSpeed) spinSpeed = (minSpeed..maxSpeed).random().toFloat()
        currentRotation += spinSpeed
    }

    fun draw(drawScope: DrawScope) {
        return drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        withTransform({
            translate(0f, -250f)
            rotate(currentRotation)
        }) {
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(imgWidth, imgHeight),
                dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                dstSize = IntSize(imgWidth * 2, imgHeight * 2)
            )
        }
    }
}