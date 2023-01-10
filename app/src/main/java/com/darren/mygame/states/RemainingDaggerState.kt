package com.darren.mygame.states

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

data class RemainingDaggerState(val image: ImageBitmap, val remainingDaggers: MutableState<Int>) {
    private val imgWidth = 30
    private val imgHeight = 30
    private val imgSize = IntSize(imgWidth, imgHeight)


}