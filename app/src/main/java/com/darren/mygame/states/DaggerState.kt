package com.darren.mygame.states

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
    val hitOffset: State<Float>,
    val sparkleImg: ImageBitmap,
) {
    private val imgWidth = 70
    private val imgHeight = 140
    private val imgSize = IntSize(imgWidth, imgHeight)

    private val shootVelocity = 175f
    private val rotationMargin = 8f
    private val dropVelocity = 60f

    private var currentDagger: Dagger = Dagger()

    private val daggerList: MutableList<Dagger> = emptyList<Dagger>().toMutableList()
    private val sparkleList: MutableList<Sparkle> = emptyList<Sparkle>().toMutableList()

    fun reset(): List<Float> {
        daggerList.clear()
        currentDagger = Dagger()
        val randomDagger = daggerUtil.value.getRandomDagger()
        val rotationList: MutableList<Float> = emptyList<Float>().toMutableList()
        (1..gameLevel.value+1).forEach{ _ ->
            val dagger = Dagger(randomDagger)
            dagger.rotation = rotationMargin * (1 until (360/rotationMargin.toInt())).random().toFloat()
            while(dagger.rotation in rotationList) {
                dagger.rotation = rotationMargin * (1 until (360/rotationMargin.toInt())).random().toFloat()
            }
            rotationList.add(dagger.rotation)
            daggerList.add(dagger)
        }
        return rotationList
    }

    fun shoot(daggerHit: () -> Unit) {
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
                (1..(2..4).random()).forEach {
                    val left = (0..1).random() != 1
                    when (it) {
                        1 -> sparkleList.add(Sparkle(true, -20f, -20f))
                        2 -> sparkleList.add(Sparkle(false, 20f, -20f))
                        else -> sparkleList.add(Sparkle(left, (-30..30).random().toFloat(), (-40..0).random().toFloat()))
                    }
                }
                daggerHit()
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

    private fun DrawScope.drawCanvas() {
        // Current Dagger
        drawDagger(currentDagger, currentDagger.translation, 0f, if (uiAlpha.value == 0f) 0f else 1f)

        // Daggers on the wood
        daggerList.forEach{ dagger ->
            dagger.rotation += spinSpeed.value
            drawDagger(dagger, -250f - hitOffset.value, 260f, uiAlpha.value)
        }

        val removeList: MutableList<Sparkle> = emptyList<Sparkle>().toMutableList()
        sparkleList.forEach{ sparkle ->
            sparkle.transX += if (sparkle.left) -sparkle.spreadFactor else sparkle.spreadFactor
            sparkle.transY += sparkle.fallFactor
            sparkle.scale *= sparkle.fadeFactor
            withTransform({
                translate(sparkle.transX, sparkle.transY)
                scale(sparkle.scale, sparkle.scale)
            }) {
                drawImage(
                    image = sparkleImg,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(23, 23),
                    dstOffset = IntOffset(midX().toInt() - 23, midY().toInt() - 23),
                    dstSize = IntSize(46, 46),
                )
            }
            if (sparkle.scale < 0.1f) removeList.add(sparkle)
        }
        removeList.forEach{ sparkle -> sparkleList.remove(sparkle) }
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

    data class Dagger(val image: ImageBitmap = daggerUtil.value.getDaggerBitmap()) {
        var rotation = 0f
        var translation: Float = 700f
    }

    data class Sparkle(val left: Boolean, var transX: Float = 0f, var transY: Float = 0f) {
        val spreadFactor = (10..30).random().toFloat()
        val fallFactor = (50..70).random().toFloat()
        val fadeFactor = (85..90).random().toFloat().div(100)
        var scale  = 1f
    }
}
