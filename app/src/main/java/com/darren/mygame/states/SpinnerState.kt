package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.screens.gameState
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY

data class SpinnerState(
    val image: MutableState<ImageBitmap>,
    val cover: ImageBitmap,
    var spinSpeed: MutableState<Float>,
    val uiAlpha: State<Float>,
    val hitOffset: State<Float>
) {
    private val imgWidth = 220
    private val imgHeight = 220
    private val imgSize = IntSize(imgWidth, imgHeight)

    private var currentRotation = 0f

    fun reset() {
        currentRotation = 0f
    }

    fun draw(drawScope: DrawScope) {
        drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        currentRotation += spinSpeed.value
        withTransform({
            translate(0f, -250f - hitOffset.value)
            rotate(currentRotation)
        }) {
            drawImage(
                image = image.value,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                dstSize = imgSize * 2,
                alpha = uiAlpha.value
            )
            drawImage(
                image = cover,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                dstSize = imgSize * 2,
                alpha = if(gameState.value.isShooting()) 0.6f else 0f
            )
        }
    }
}