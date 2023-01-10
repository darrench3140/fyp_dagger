package com.darren.mygame

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.MutableState

object StatusBarUtil {
    fun transparentStatusBar(activity: Activity) {
        with(activity) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val vis = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = option or vis
            window.statusBarColor = Color.TRANSPARENT
        }
    }
}

object LevelUtil {
    fun updateLevelInfo(randomSpeed: MutableState<Boolean>, spinSpeed: MutableState<Float>, minSpeed: MutableState<Int>, maxSpeed: MutableState<Int>, remainingDaggers: MutableState<Int>) {
        val level = gameLevel.value
        var clockwise = if((0..1).random() == 0) -1 else 1
        randomSpeed.value = level >= 5
        spinSpeed.value = ((level + (2..4).random()) * clockwise).toFloat()
        minSpeed.value = (level - 4) * clockwise
        maxSpeed.value = level * clockwise
        remainingDaggers.value = level + (5..7).random()
    }
}
