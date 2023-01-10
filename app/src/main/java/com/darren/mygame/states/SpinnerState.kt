package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.R
import com.darren.mygame.screens.gameState
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY

var spinnerImg = R.drawable.spinner1

data class SpinnerState(val image: ImageBitmap, var spinSpeed: MutableState<Float>, val uiAlpha: State<Float>) {
    private val imgWidth = 220
    private val imgHeight = 220

    private var currentRotation = 0f

    fun reset() {
        currentRotation = 0f
    }

    fun spin() {
        currentRotation += spinSpeed.value
    }

    fun draw(drawScope: DrawScope) {
        return drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        val offset = if (gameState.value.isShooting()) 20f else 0f
        val saturation = if (gameState.value.isShooting()) 1.5f else 1f
        withTransform({
            translate(0f, -250f - offset)
            rotate(currentRotation)
        }) {
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(imgWidth, imgHeight),
                dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                dstSize = IntSize(imgWidth * 2, imgHeight * 2),
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(saturation) }),
                alpha = uiAlpha.value
            )
        }
    }
}