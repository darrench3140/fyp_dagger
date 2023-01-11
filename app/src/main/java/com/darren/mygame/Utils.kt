package com.darren.mygame

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.darren.mygame.screens.gameLevel
import com.darren.mygame.states.DaggerState

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
    fun updateLevelInfo(randomSpeed: MutableState<Boolean>, clockwise: MutableState<Boolean>, spinSpeed: MutableState<Float>, minSpeed: MutableState<Int>, maxSpeed: MutableState<Int>, remainingDaggers: MutableState<Int>) {
        val level = gameLevel.value
        clockwise.value = (0..1).random() == 1
        randomSpeed.value = level >= 3
        spinSpeed.value = (3..5).random().toFloat()
        minSpeed.value = ((1+level/4)..(3+level/4)).random()
        maxSpeed.value = ((5+level/4)..(7+level/4)).random()
        remainingDaggers.value = level / 2 + (5..7).random()
    }
}



//    fun getRandomSpinner(): ImageBitmap {
//        return when ((1..2).random()) {
//            1 -> ImageBitmap.imageResource(id = R.drawable.spinner1)
//            2 -> ImageBitmap.imageResource(id = R.drawable.spinner2)
//            else -> ImageBitmap.imageResource(id = R.drawable.spinner0)
//        }
//    }
data class SpinnerUtil(
    val totalSpinners: Int = 2,
) {
    private val spinnerList: MutableList<ImageBitmap> = emptyList<ImageBitmap>().toMutableList()

    private fun getSpinner(id: Int): Int {
        return when(id) {
            1 -> R.drawable.spinner1
            2 -> R.drawable.spinner2
            else -> R.drawable.spinner0
        }
    }

    @Composable
    fun initList() {
        spinnerList.clear()
        (1..totalSpinners).forEach{
            spinnerList.add(ImageBitmap.imageResource(id = getSpinner(it)))
        }
    }

    fun getRandomSpinner(): ImageBitmap {
        return spinnerList[(0 until totalSpinners).random()]
    }

}