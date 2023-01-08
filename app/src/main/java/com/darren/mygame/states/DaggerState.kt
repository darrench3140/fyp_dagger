package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.R
import com.darren.mygame.screens.gameScore
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY

var daggerImg = R.drawable.d1

data class DaggerState(val image: ImageBitmap, var spinSpeed: Float) {
    private val imgWidth = 69
    private val imgHeight = 148
    private val imgSize = IntSize(imgWidth, imgHeight)

    private val shootVelocity = 10f

    private var noOfDagger = 1

    private var currentDagger: Dagger = Dagger(noOfDagger)

    private val daggerList: MutableList<Dagger> = emptyList<Dagger>().toMutableList()

    fun reset() {
        daggerList.clear()
        gameScore.value = daggerList.size
    }

    fun shoot() {
//        check collision, if angle between 0 - 3 and 357 - 360, = collided
        daggerList.add(currentDagger)
        currentDagger = Dagger(noOfDagger++)
        gameScore.value = daggerList.size
//        else gameState -> Losing
    }

    fun draw(drawScope: DrawScope) {
        return drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas(){
        // Current Dagger
        currentDagger.dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight)
        withTransform({
            translate(0f, 700f)
        }) {
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = currentDagger.dstOffset,
                dstSize = imgSize * 2
            )
        }
        // Daggers on the wood
        daggerList.forEach{ dagger ->
            dagger.rotation += spinSpeed
            withTransform({
                translate(0f, -250f) //mark to center
                rotate(dagger.rotation)
                translate(0f, 260f) //provide offset
            }) {
                drawImage(
                    image = image,
                    srcOffset = IntOffset.Zero,
                    srcSize = imgSize,
                    dstOffset = dagger.dstOffset,
                    dstSize = imgSize * 2
                )
            }
        }
    }

    data class Dagger(val refNo: Int) {
        var rotation = 0f
        var dstOffset = IntOffset.Zero
    }
}