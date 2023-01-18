package com.darren.mygame.states

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

data class FruitState(val image: ImageBitmap, val image_crack: ImageBitmap, val spinSpeed: MutableState<Float>, val uiAlpha: State<Float>, val hitOffset: State<Float>, val fruitHit: MutableState<Int>) {
    private val imgWidth = 56
    private val imgHeight = 56
    private val imgSize = IntSize(imgWidth, imgHeight)
    private val rotationMargin = 20f
    private val fruitList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()
    private val completedFruitList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()

    fun reset(rotationList: List<Float>) {
        fruitList.clear()
        // 40% chance 0, 20% chance 1 or 2, 10% chance 3 or 4
        var noOfFruit = (0 until 10).random()
        noOfFruit = when (noOfFruit) {
            in (0..3) -> return
            in (4..5) -> 1
            in (6..7) -> 2
            8 -> 3
            else -> 4
        }
        (1..noOfFruit).forEach { _ ->
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

    fun hit() { //check collision of fruit
        val removeList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()
        fruitList.forEach{ fruit ->
            val degree = abs(fruit.rotation % 360)
            if (degree in 0f..rotationMargin || degree in (360f-rotationMargin)..360f) {
                removeList.add(fruit) //so that multiple fruit being hit can be handled correctly
                if (fruit.rotation < 0) fruit.rotation = 360 - abs(fruit.rotation % 360)
                else fruit.rotation = abs(fruit.rotation % 360)
                completedFruitList.add(fruit)
            }
        }
        removeList.forEach{ fruit -> fruitList.remove(fruit) }
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
        val removeList: MutableList<Fruit> = emptyList<Fruit>().toMutableList()
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
                val rotation = fruit.rotation % 360
                val transX = ((screenWidthInt - 20.dp.toPx()) - (screenWidthInt/2 - 285*abs(sin(180 - rotation)))) / 20
                val transY = ((screenHeightInt/2 - 250 - 285 * abs(cos(180 - rotation))) - (45.dp.toPx())) / 20
                fruit.transX += transX
                fruit.transY -= transY
                if (fruit.transX > (screenWidthInt/2 - 20.dp.toPx())) {
                    removeList.add(fruit)
                }
            } else fruit.rotation += 10
            if (fruit.scale > 0.5f) fruit.scale *= 0.95f
        }
        removeList.forEach{ fruit ->
            completedFruitList.remove(fruit)
            fruitHit.value++
        }
    }

    data class Fruit(var rotation: Float) {
        var transX: Float = 0f
        var transY: Float = 0f
        var scale: Float = 1f
    }
}