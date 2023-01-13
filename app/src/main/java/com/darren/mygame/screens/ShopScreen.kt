package com.darren.mygame.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*

@Composable
fun ShopScreen(navController: NavHostController) {

    val pinkBoxID = remember { mutableStateOf(daggerUtil.getDaggerInUseID()) }

    DrawBackground()
    DrawTopFruit()
    DrawReturnButton(offsetY = (-50).dp) {
        navController.popBackStack()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        DrawShopLights()
        DrawDagger(modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = screenHeight.times(0.13f))
            .size(140.dp)
            .rotate(50f), daggerUtil.getDaggerResource(pinkBoxID.value))
        DrawShopBanner()
        //4x4 Grid
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .size(340.dp, 340.dp)
                .offset(y = screenHeight.times(0.13f)),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            repeat(4) { x ->
                Row(
                    modifier = Modifier.size(340.dp, 83.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(4) { y ->
                        val id = x * 4 + y + 1
                        DrawShopItem(id, pinkBoxID)
                    }
                }
            }
        }
    }
}