package com.darren.mygame.states

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.darren.mygame.screenHeightInt
import com.darren.mygame.screenWidthInt
import com.darren.mygame.screens.midX
import com.darren.mygame.screens.midY
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class FruitState(val image: ImageBitmap, val image_crack: ImageBitmap, val spinSpeed: MutableState<Float>, val uiAlpha: State<Float>, val hitOffset: State<Float>) {
    private val imgWidth = 56
    private val imgHeight = 56
    private val imgSize = IntSize(imgWidth, imgHeight)
    private val rotationMargin = 20f
    private val fruitList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()
    private val completedFruitList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()

    fun reset(rotationList: List<Float>) {
        fruitList.clear()
        completedFruitList.clear()
        (1..(1..4).random()).forEach { _ ->
            var rotation: Float
            run { while (true) {
                rotation = (20..339).random().toFloat()
                var crashed = false
                rotationList.forEach{ if (!crashed && rotation in (it - rotationMargin .. it + rotationMargin)) crashed = true }
                fruitList.forEach{ if (!crashed && rotation in (it.rotation - rotationMargin .. it.rotation + rotationMargin)) crashed = true }
                if (!crashed) return@run
            } }
            val fruit = Fruit(rotation)
            fruitList.add(fruit)
        }
    }

    fun checkCollision(fruitHit: () -> Unit) {
        val remove: MutableList<Fruit> = emptyList<Fruit>().toMutableList()
        fruitList.forEach{ fruit ->
            val degree = abs(fruit.rotation % 360)
            if (degree in 0f..rotationMargin || degree in (360f-rotationMargin)..360f) {
                remove.add(fruit) //so that multiple fruit being hit can be handled correctly
                if (fruit.rotation < 0) fruit.rotation = 360 - abs(fruit.rotation % 360)
                else fruit.rotation = abs(fruit.rotation % 360)
                completedFruitList.add(fruit)
                fruitHit()
            }
        }
        remove.forEach{ fruit -> fruitList.remove(fruit) }
    }

    fun draw(drawScope: DrawScope) {
        drawScope.drawCanvas()
    }

    private fun DrawScope.drawCanvas() {
        fruitList.forEach{ fruit ->
            fruit.rotation += spinSpeed.value
            withTransform({
                translate(0f, -250f - hitOffset.value)
                rotate(180f + fruit.rotation)
                translate(0f, -285f)
            }){
                drawImage(
                    image = image,
                    srcOffset = IntOffset.Zero,
                    srcSize = imgSize,
                    dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                    dstSize = imgSize * 2,
                    alpha = uiAlpha.value
                )
            }
        }
        completedFruitList.forEach{ fruit ->
            withTransform({
                translate(0f + fruit.transX, -250f + fruit.transY)
                rotate(fruit.rotation)
                translate(0f, 285f)
                rotate(-fruit.rotation)
                scale(fruit.scale, fruit.scale)
            }) {
                drawImage(
                    image = image_crack,
                    srcOffset = IntOffset.Zero,
                    srcSize = imgSize,
                    dstOffset = IntOffset(midX().toInt() - imgWidth, midY().toInt() - imgHeight),
                    dstSize = imgSize * 2,
                )
            }
            if (fruit.rotation % 360 in (160f..300f)) {
                val rotation = 160f
                val transX = ((screenWidthInt - 20.dp.toPx()) - (screenWidthInt/2 - 285*sin(180 - rotation))) / 20
                val transY = ((screenHeightInt/2 - 250 - 285 * cos(180 - rotation)) - (45.dp.toPx())) / 20
                Log.d("game", "rotation: ${rotation}, $transX, $transY")
                fruit.transX += transX
                fruit.transY -= transY
            } else fruit.rotation += 10
            if (fruit.scale > 0.5f) fruit.scale *= 0.95f
        }
    }

    data class Fruit(var rotation: Float) {
        var transX: Float = 0f
        var transY: Float = 0f
        var scale: Float = 1f
    }
}