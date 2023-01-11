package com.darren.mygame

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.darren.mygame.screens.gameLevel

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

data class SpinnerUtil(val totalSpinners: Int = 16) {
    private val spinnerList: MutableList<ImageBitmap> = emptyList<ImageBitmap>().toMutableList()

    private fun getSpinner(id: Int): Int {
        return when(id) {
            1 -> R.drawable.spinner1
            2 -> R.drawable.spinner2
            3 -> R.drawable.spinner3
            4 -> R.drawable.spinner4
            5 -> R.drawable.spinner5
            6 -> R.drawable.spinner6
            7 -> R.drawable.spinner7
            8 -> R.drawable.spinner8
            9 -> R.drawable.spinner9
            10 -> R.drawable.spinner10
            11 -> R.drawable.spinner11
            12 -> R.drawable.spinner12
            13 -> R.drawable.spinner13
            14 -> R.drawable.spinner14
            15 -> R.drawable.spinner15
            16 -> R.drawable.spinner16
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