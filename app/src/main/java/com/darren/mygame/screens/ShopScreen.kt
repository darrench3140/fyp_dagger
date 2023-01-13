package com.darren.mygame.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darren.mygame.*
import com.darren.mygame.R
import kotlinx.coroutines.delay

@Composable
fun ShopScreen(navController: NavHostController) {
    val lightControl = remember{ mutableStateOf(false) }
    val lightAlpha1 = animateFloatAsState(targetValue = if (!lightControl.value) 1f else 0f, animationSpec = tween(500))
    val lightAlpha2 = animateFloatAsState(targetValue = if (lightControl.value) 1f else 0f, animationSpec = tween(500))

    LaunchedEffect(true) {
        while(true) {
            lightControl.value = !lightControl.value
            delay(500)
        }
    }

    DrawBackground()
    DrawTopFruit()
    DrawReturnButton(offsetY = (-50).dp) {
        navController.popBackStack()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        DrawShopLight(lightAlpha = lightAlpha1, rotation = 0f)
        DrawShopLight(lightAlpha = lightAlpha2, rotation = 20f)
        DrawShopLight(rotation = 40f)
        DrawDagger(modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = screenHeight.times(0.13f))
            .size(140.dp)
            .rotate(50f))
        Image(
            painter = painterResource(id = R.drawable.shop_banner),
            contentDescription = "shop_banner",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(340.dp)
                .offset(y = screenHeight.times(0.16f))
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(340.dp, 340.dp)
                .offset(y = screenHeight.times(0.41f)),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.shop_grid_bg),
                contentDescription = "",
                modifier = Modifier.size(83.dp)
            )
        }


    }

}