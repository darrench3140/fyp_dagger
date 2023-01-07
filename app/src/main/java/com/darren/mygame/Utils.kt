package com.darren.mygame

import android.app.Activity
import android.graphics.Color
import android.view.WindowManager

object StatusBarUtil {
    fun transparentStatusBar(activity: Activity) {
        with(activity) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }
}