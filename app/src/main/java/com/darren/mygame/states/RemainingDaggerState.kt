package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

data class RemainingDaggerState(val image: ImageBitmap, val remainingDaggers: MutableState<Int>, val uiAlpha: State<Float>) {
    private val imgWidth = 30
    private val imgHeight = 30
    private val imgSize = IntSize(imgWidth, imgHeight)

    fun draw(drawScope: DrawScope) {
        drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        (1..remainingDaggers.value).forEach{

            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = IntOffset((size.width * 0.05).toInt(), (size.height * 0.95).toInt() - it * 80),
                dstSize = imgSize * 3,
                alpha = uiAlpha.value
            )
        }
    }
}

