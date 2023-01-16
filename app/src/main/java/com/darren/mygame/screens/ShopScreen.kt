package com.darren.mygame.screens

import android.os.SystemClock
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*

@Composable
fun ShopScreen(navController: NavHostController) {

    val pinkBoxID = remember { mutableStateOf(daggerUtil.getDaggerInUseID()) }
    val greenBoxID = remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }

    DrawBackground()
    DrawTopFruit()
    DrawReturnButton(offsetY = (-50).dp) {
        if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
            lastClickTime = SystemClock.elapsedRealtime()
            navController.popBackStack()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        DrawShopLights()
        DrawDagger(modifier = Modifier
            .align(Alignment.Center)
            .offset(y = -screenHeightDp.times(0.3f))
            .size(screenWidthDp.div(2.935f))
            .rotate(50f), daggerUtil.getDaggerResource(pinkBoxID.value))
        DrawShopBanner(-screenHeightDp.times(0.1f))
        Column( //4x4 Grid
            modifier = Modifier
                .align(Alignment.Center)
                .size(screenWidthDp.div(1.2088f))
                .offset(y = -screenHeightDp.times(0.1f) + (screenWidthDp.times(0.462296f) + 5.dp)),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            repeat(4) { x ->
                Row(
                    modifier = Modifier.size(screenWidthDp.div(1.2088f), screenWidthDp.div(4.8352f) - 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(4) { y ->
                        val id = x * 4 + y + 1
                        DrawShopItem(id, pinkBoxID, greenBoxID)
                    }
                }
            }
        }
    }
}

/* [Resizable]
    eg: screenWidth 411, screenHeight 843
    box size = 340, row height = (340 - 8) /4 = 83, spotlight dagger size = 140, spotlight = 180, item dagger = 75
    shop banner = 340 : 40
    scale factor approximately equals: 411/340 = 1.2088f
 */