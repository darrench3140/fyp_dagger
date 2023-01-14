package com.darren.mygame.states

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY

data class FruitState(val image: ImageBitmap, val spinSpeed: MutableState<Float>) {
    private val imgWidth = 56
    private val imgHeight = 56
    private val imgSize = IntSize(imgWidth, imgHeight)
    private val rotationMargin = 10f
    private val fruitList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()

    fun reset(rotationList: List<Float>) {
        Log.d("game", "fruit state reset")
        fruitList.clear()
        (1..(1..4).random()).forEach { _ ->
            var rotation: Float
            run { while (true) {
                rotation = (10..349).random().toFloat()
                var crashed = false
                rotationList.forEach{ if (!crashed && rotation in (it - rotationMargin .. it + rotationMargin)) crashed = true }
                fruitList.forEach{ if (!crashed && rotation in (it.rotation - rotationMargin .. it.rotation + rotationMargin)) crashed = true }
                if (!crashed) return@run
            } }
            val fruit = Fruit(rotation)
            fruitList.add(fruit)
        }
    }

    fun hit() {

    }

    fun draw(drawScope: DrawScope) {
        drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        fruitList.forEach{ fruit ->
            fruit.rotation += spinSpeed.value
            withTransform({
                translate(0f, -250f)
                rotate(fruit.rotation)
                translate(0f, 300f)
            }){
                drawImage(
                    image = image,
                    srcOffset = IntOffset.Zero,
                    srcSize = imgSize,
                    dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                    dstSize = imgSize * 2
                )
            }
        }
    }

    data class Fruit(var rotation: Float)
}