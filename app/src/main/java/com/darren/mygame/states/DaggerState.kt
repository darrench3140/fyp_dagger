package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.R
import com.darren.mygame.screens.*
import kotlin.math.abs

var daggerImg = R.drawable.d1

data class DaggerState(
    val image: ImageBitmap,
    val spinSpeed: MutableState<Float>,
    val remainingDaggers: MutableState<Int>,
    val uiAlpha: State<Float>,
    val hitOffset: State<Float>
) {
    private val imgWidth = 69
    private val imgHeight = 148
    private val imgSize = IntSize(imgWidth, imgHeight)

    private val shootVelocity = 175f
    private val dropVelocity = 60f

    private var noOfDagger = 1
    private var currentDagger: Dagger = Dagger(noOfDagger)

    private val daggerList: MutableList<Dagger> = emptyList<Dagger>().toMutableList()

    fun reset() {
        daggerList.clear()
        noOfDagger = 1
        currentDagger = Dagger(noOfDagger)
        (1..gameLevel.value+1).forEach{ _ ->
            val dagger = Dagger(0)
            dagger.rotation = (0..359).random().toFloat()
            daggerList.add(dagger)
        }
    }

    fun shoot(hit: () -> Unit) {
        currentDagger.translation -= shootVelocity
        if (gameState.value.isShooting() && currentDagger.translation <= 0f) { //Arrived wood
            //Check collision
            if (daggerList.any { dagger ->
                    val degree = abs(dagger.rotation % 360)
                    degree in 0f..7f || degree in 353f..360f
                }) {
                gameState.value.setLosing()
            } else {
                if (remainingDaggers.value <= 1) {
                    gameState.value.setLeveling()
                } else {
                    daggerList.add(currentDagger)
                    noOfDagger++
                    currentDagger = Dagger(noOfDagger)
                    gameState.value.setRunning()
                }
                gameScore.value++
                remainingDaggers.value--
                hit()
            }
        }
    }

    fun drop() {
        currentDagger.rotation += 10f
        currentDagger.translation += dropVelocity
        if (currentDagger.translation > 1200f) {
            gameState.value.setOver()
        }
    }

    fun draw(drawScope: DrawScope) {
        drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas(){
        // Current Dagger
        drawDagger(currentDagger, currentDagger.translation, 0f, 1f)

        // Daggers on the wood
        daggerList.forEach{ dagger ->
            dagger.rotation += spinSpeed.value
            drawDagger(dagger, -250f - hitOffset.value, 260f, uiAlpha.value)
        }
    }

    private fun DrawScope.drawDagger(dagger: Dagger, trans1: Float, trans2: Float, alpha: Float) {
        withTransform({
            translate(0f, trans1) //mark to center
            rotate(dagger.rotation)
            translate(0f, trans2) //provide offset
        }) {
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                dstSize = imgSize * 2,
                alpha = alpha
            )
        }
    }

    data class Dagger(val refNo: Int) {
        var rotation = 0f
        var translation: Float = 700f
    }
}
