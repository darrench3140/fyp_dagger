package com.darren.mygame.states

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.daggerUtil
import com.darren.mygame.gameLevel
import com.darren.mygame.gameScore
import com.darren.mygame.gameState
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY
import kotlin.math.abs

data class DaggerState(
    val spinSpeed: MutableState<Float>,
    val remainingDaggers: MutableState<Int>,
    val uiAlpha: State<Float>,
    val hitOffset: State<Float>
) {
    private val imgWidth = 70
    private val imgHeight = 140
    private val imgSize = IntSize(imgWidth, imgHeight)

    private val shootVelocity = 175f
    private val rotationMargin = 7f
    private val dropVelocity = 60f

    private var currentDagger: Dagger = Dagger()

    private val daggerList: MutableList<Dagger> = emptyList<Dagger>().toMutableList()

    fun reset(): List<Float> {
        Log.d("game", "dagger state reset")
        daggerList.clear()
        currentDagger = Dagger()
        val randomDagger = daggerUtil.getRandomDagger()
        val rotationList: MutableList<Float> = emptyList<Float>().toMutableList()
        (1..gameLevel.value+1).forEach{ _ ->
            val dagger = Dagger(randomDagger)
            run { while (true) { //keep generate random float until not collided with any other daggers
                dagger.rotation = (10..349).random().toFloat()
                if (rotationList.isEmpty()) return@run
                else {
                    var crashed = false
                    rotationList.forEach{ if (!crashed && dagger.rotation in (it - rotationMargin.. it + rotationMargin)) crashed = true }
                    if (!crashed) return@run
                }
            }}
            rotationList.add(dagger.rotation)
            daggerList.add(dagger)
        }
        return rotationList
    }

    fun shoot(hit: () -> Unit) {
        currentDagger.translation -= shootVelocity
        if (gameState.value.isShooting() && currentDagger.translation <= 0f) { //Arrived wood
            //Check collision
            if (daggerList.any { dagger ->
                    val degree = abs(dagger.rotation % 360)
                    degree in 0f..rotationMargin || degree in (360f-rotationMargin)..360f
                }) {
                gameState.value.setLosing()
            } else {
                if (remainingDaggers.value <= 1) {
                    gameState.value.setLeveling()
                } else {
                    daggerList.add(currentDagger)
                    currentDagger = Dagger()
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
        drawDagger(currentDagger, currentDagger.translation, 0f, if (uiAlpha.value == 0f) 0f else 1f)

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
                image = dagger.image,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                dstSize = imgSize * 2,
                alpha = alpha
            )
        }
    }

    data class Dagger(val image: ImageBitmap = daggerUtil.getDaggerBitmap()) {
        var rotation = 0f
        var translation: Float = 700f
    }
}
