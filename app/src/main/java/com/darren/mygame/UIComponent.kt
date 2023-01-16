package com.darren.mygame

import android.os.SystemClock
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.darren.mygame.screens.gameReset
import kotlinx.coroutines.delay

val myFont = FontFamily(Font(R.font.nineteenth))

@Composable
fun DrawBackground() {
    Image( //Background
        painter = painterResource(id = R.drawable.bg),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun DrawSword(alpha: Float) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.loading),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = alpha
        )
    }
}

@Composable
fun DrawLogo(modifier: Modifier, alpha: Float = 1f) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "logo",
        alpha = alpha
    )
}

@Composable
fun DrawDagger(modifier: Modifier, daggerID: Int = daggerUtil.getDaggerResource()) {
    Image(
        modifier = modifier.size(100.dp),
        painter = painterResource(id = daggerID),
        contentDescription = "dagger"
    )
}

@Composable
fun DrawButton(text: String, offsetX: Dp = 0.dp, offsetY: Dp = 0.dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)
    ) {
        Image(
            painter = painterResource(id = R.drawable.button),
            contentDescription = "button",
            modifier = Modifier
                .align(Alignment.Center)
                .scale(2.5f)
                .clickable { onClick() }
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = text,
            fontSize = 35.sp,
            color = Color(0xFFF1F6F5),
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun DrawReturnButton(offsetX: Dp = 50.dp, offsetY: Dp = 0.dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)) {
        Image(
            painter = painterResource(id = R.drawable.return_button),
            contentDescription = "return menu",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .scale(2.5f)
                .clickable { onClick() }
        )
    }
}

@Composable
fun DrawTopBar(topBarOffset: State<Dp>) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp)
            .offset(0.dp, topBarOffset.value)
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Text( //Score
            text = gameScore.value.toString(),
            modifier = Modifier.weight(0.2f),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            color = Color(0xFFFFB26B),
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Text(
            text = "STAGE ${gameLevel.value}",
            fontSize = 30.sp,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF1F6F5)
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
fun DrawTopFruit() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = fruitCount.value.toString(),
            modifier = Modifier.offset(x = (-25).dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            color = Color(0xFFF1F6F5),
        )
        Image(
            painter = painterResource(id = R.drawable.fruit_crack),
            contentDescription = "fruit_top_bar",
            modifier = Modifier
                .size(30.dp)
                .offset(x = (-20).dp)
        )
    }
}

@Composable
fun DrawScoreBoard(
    navController: NavHostController,
    scoreBoardOffset: State<Dp>,
    showTopScore: MutableState<Boolean>,
    lastScore: MutableState<Int>
) {
    var lastClickTime by remember { mutableStateOf(0L) }
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = scoreBoardOffset.value)
    ) {
        Image(
            painter = painterResource(id = R.drawable.score_board),
            contentDescription = "score_board",
            modifier = Modifier
                .align(Alignment.Center)
                .size(345.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.top_score),
            contentDescription = "top_score",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-5).dp, y = 100.dp)
                .size(130.dp),
            alpha = if(showTopScore.value) 1f else 0f
        )
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = "SCORE",
                fontSize = 30.sp,
                color = Color(0xFFFFB26B),
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = lastScore.value.toString(),
                fontSize = 60.sp,
                color = Color(0xFFF1F6F5),
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
        }
        DrawButton(text = "RESTART", offsetY = screenHeightDp.div(3.12222f)) { //screenHeight 843 -> offset 270
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                lastClickTime = SystemClock.elapsedRealtime()
                gameReset()
                showTopScore.value = false
            }
        }
        DrawShopButton(offsetY = screenHeightDp.div(2.2784f)) { //screenHeight 843 -> offset 370
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                lastClickTime = SystemClock.elapsedRealtime()
                navController.navigate("shop_screen")
            }
        }
        DrawReturnButton(offsetY = 50.dp) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                lastClickTime = SystemClock.elapsedRealtime()
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun DrawShopButton(offsetY: Dp, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.shop_button_bg),
            contentDescription = "shop_bg",
            modifier = Modifier
                .align(Alignment.Center)
                .size(55.dp)
                .offset(y = offsetY)
                .clickable { onClick() }
        )
        Image(
            painter = painterResource(id = R.drawable.shop_button_fg),
            contentDescription = "shop_fg",
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp)
                .offset(y = offsetY)
        )
    }
}
@Composable
fun DrawShopLight(lightAlpha: State<Float> = mutableStateOf(1f), rotation: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        val shopLightFilter = floatArrayOf(
            1f, 0f, 0f, 0f, 255f,
            0f, 1f, 0f, 0f, 255f,
            0f, 0f, 1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
        Image(
            painter = painterResource(id = R.drawable.shop_light),
            contentDescription = "shop_light",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -screenHeightDp.times(0.3f))
                .size(screenWidthDp.div(2.2833f))
                .rotate(rotation),
            colorFilter = ColorFilter.colorMatrix(ColorMatrix(shopLightFilter)),
            alpha = lightAlpha.value
        )
    }
}

@Composable
fun DrawShopLights() {
    val lightControl = remember{ mutableStateOf(false) }
    val lightAlpha1 = animateFloatAsState(targetValue = if (!lightControl.value) 1f else 0f, animationSpec = tween(500))
    val lightAlpha2 = animateFloatAsState(targetValue = if (lightControl.value) 1f else 0f, animationSpec = tween(500))

    LaunchedEffect(true) {
        while(true) {
            lightControl.value = !lightControl.value
            delay(500)
        }
    }
    DrawShopLight(lightAlpha = lightAlpha1, rotation = 0f)
    DrawShopLight(lightAlpha = lightAlpha2, rotation = 20f)
    DrawShopLight(rotation = 40f)
}

@Composable
fun DrawShopBanner(offset: Dp) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = offset)) {
        Image(
            painter = painterResource(id = R.drawable.shop_banner),
            contentDescription = "shop_banner",
            modifier = Modifier
                .align(Alignment.Center)
                .size(screenWidthDp.div(1.2088f), screenWidthDp.div(10.2748f))
        )
        Text(
            text = "Dagger Shop",
            fontSize = 30.sp,
            color = Color(0xFFF1F6F5),
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DrawShopItem(id: Int, pinkBoxID: MutableState<Int>, greenBoxID: MutableState<Int>) {
    val size = screenWidthDp.div(4.8352f) - 2.dp //size of an item box
    Box(modifier = Modifier.size(size)) {
        Image(
            painter = painterResource(id = R.drawable.shop_grid_bg),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size)
                .clickable {
                    if (id <= purchasedCount.value) {
                        greenBoxID.value = 0
                        pinkBoxID.value = id
                        daggerUtil.setDaggerInUseID(id)
                    } else {
                        greenBoxID.value = id
                    }
                }
        )
        DrawDagger(modifier = Modifier
            .align(Alignment.Center)
            .size(size.div(1.10667f))
            .rotate(45f), daggerID = if (id <= purchasedCount.value) daggerUtil.getDaggerResource(id) else daggerUtil.getLockedResource(id))

        Image(
            painter = painterResource(id = R.drawable.shop_pink_box),
            contentDescription = "selected_dagger",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size),
            alpha = if (id == pinkBoxID.value) 1f else 0f
        )
        Image(
            painter = painterResource(id = R.drawable.shop_green_box),
            contentDescription = "view_dagger",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size),
            alpha = if (id == greenBoxID.value) 1f else 0f
        )
    }
}