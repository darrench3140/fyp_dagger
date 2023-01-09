package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavHostController
import com.darren.mygame.R
import com.darren.mygame.ScreenManager
import com.darren.mygame.screens.gameScore
import com.darren.mygame.screens.gameState
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY

var daggerImg = R.drawable.d1

data class DaggerState(val image: ImageBitmap, var spinSpeed: MutableState<Float>) {
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
        gameScore.value = daggerList.size
    }

    fun shoot() {
        currentDagger.translation -= shootVelocity
        if (currentDagger.translation <= 0f) { //Arrived wood
            //Check collision
            if (daggerList.any { dagger ->
                    val degree = dagger.rotation % 360
                    degree in 0f..6f || degree in 354f..360f
                }) {
                gameState.value.setLosing()
            } else {
                daggerList.add(currentDagger)
                noOfDagger++
                currentDagger = Dagger(noOfDagger)
                gameScore.value = daggerList.size
                gameState.value.setRunning()
            }
        }
    }

    fun drop(navController: NavHostController) {
        currentDagger.rotation += 10f
        currentDagger.translation += dropVelocity
        if (currentDagger.translation > 1200f) {
            gameState.value.setStopped()
            navController.popBackStack()
            navController.navigate(ScreenManager.ScoreScreen.route)
        }
    }

    fun draw(drawScope: DrawScope) {
        return drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas(){
        // Current Dagger
        currentDagger.dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight)
        withTransform({
            translate(0f, currentDagger.translation)
            rotate(currentDagger.rotation)
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
            dagger.rotation += spinSpeed.value
            val offset = if(gameState.value.isShooting()) 20f else 0f
            withTransform({
                translate(0f, -250f - offset) //mark to center
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
        var translation: Float = 700f
        var dstOffset = IntOffset.Zero
    }
}