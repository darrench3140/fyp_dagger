package com.darren.mygame.states

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.darren.mygame.R

var daggerImg = R.drawable.d1

data class DaggerState(val image: ImageBitmap) {
    private val imgWidth = 84
    private val imgHeight = 435
    private val daggerList: MutableList<Dagger> = emptyList<Dagger>().toMutableList()

    fun init() {
        daggerList.clear()
        daggerList.add(Dagger(true))
    }

    fun shoot() {
        
    }

    fun draw(drawScope: DrawScope, timeTick: Float) {
        return drawScope.draw()
    }

    private fun DrawScope.draw(){
        daggerList.forEach{

        }

        withTransform({
            scale(0.6f, 0.6f)
            rotate(0f)
        }) {
            drawImage(image = image,
//                srcOffset = IntOffset()
            )
        }
    }

    data class Dagger(var hi : Boolean = true) {

    }
}