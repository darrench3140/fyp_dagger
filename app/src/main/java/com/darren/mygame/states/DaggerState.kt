package com.darren.mygame.states

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.R
import com.darren.mygame.screens.gameScore
import com.darren.mygame.screens.gameState
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY
import kotlin.math.abs

var daggerImg = R.drawable.d1

data class DaggerState(val image: ImageBitmap, val spinSpeed: MutableState<Float>, val remainingDaggers: MutableState<Int>, val uiAlpha: State<Float>) {
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
    }

    fun shoot() {
        currentDagger.translation -= shootVelocity
        if (gameState.value.isShooting && currentDagger.translation <= 0f) { //Arrived wood
            //Check collision
            if (daggerList.any { dagger ->
                    val degree = abs(dagger.rotation % 360)
                    degree in 0f..6f || degree in 354f..360f
                }) {
                gameState.value.setLosing()
            } else {
                gameScore.value++
                if (remainingDaggers.value <= 1) {
                    gameState.value.setNextLevel()
                } else {
                    remainingDaggers.value--
                    daggerList.add(currentDagger)
                    noOfDagger++
                    currentDagger = Dagger(noOfDagger)
                    gameState.value.setRunning()
                }
            }
        }
    }

    fun drop() {
        currentDagger.rotation += 10f
        currentDagger.translation += dropVelocity
        if (currentDagger.translation > 1200f) {
            gameState.value.setStopped()
        }
    }

    fun draw(drawScope: DrawScope) {
        return drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas(){
        // Current Dagger
        currentDagger.dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight)
        drawDagger(currentDagger, currentDagger.translation, 0f)

        // Daggers on the wood
        daggerList.forEach{ dagger ->
            dagger.rotation += spinSpeed.value
            val offset = if(gameState.value.isShooting()) 20f else 0f
            drawDagger(dagger, -250f - offset, 260f)
        }
    }

    private fun DrawScope.drawDagger(dagger: Dagger, trans1: Float, trans2: Float) {
        withTransform({
            translate(0f, trans1) //mark to center
            rotate(dagger.rotation)
            translate(0f, trans2) //provide offset
        }) {
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = imgSize,
                dstOffset = dagger.dstOffset,
                dstSize = imgSize * 2,
                alpha = uiAlpha.value
            )
        }
    }

    data class Dagger(val refNo: Int) {
        var rotation = 0f
        var translation: Float = 700f
        var dstOffset = IntOffset.Zero
    }
}
