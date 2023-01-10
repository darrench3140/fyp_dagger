package com.darren.mygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.darren.mygame.screens.gameLevel
import com.darren.mygame.screens.gameScore
import com.darren.mygame.states.daggerImg

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
fun DrawDagger(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(100.dp),
        painter = painterResource(id = daggerImg),
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
fun DrawReturnButton(offsetX: Dp = 0.dp, offsetY: Dp = 0.dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)) {
        Image(
            painter = painterResource(id = R.drawable.return_button),
            contentDescription = "return menu",
            modifier = Modifier
                .align(Alignment.Center)
                .scale(2.5f)
                .clickable { onClick() }
        )
    }
}

@Composable
fun DrawTopBar(topBarOffset: State<Dp>) {
    Row(
        Modifier
            .fillMaxWidth()
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
fun DrawScoreBoard(navController: NavHostController, scoreBoardOffset: State<Dp>) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = scoreBoardOffset.value)
    ) {
        Image(
            painter = painterResource(id = R.drawable.score_board),
            contentDescription = "score_board",
            modifier = Modifier
                .align(Alignment.Center)
                .size(screenWidth - 50.dp)
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
                text = gameScore.value.toString(),
                fontSize = 60.sp,
                color = Color(0xFFF1F6F5),
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
        }
        DrawButton(text = "RESTART", offsetY = 280.dp) {
            gameReset()
        }
        DrawReturnButton(offsetX = -(150).dp, offsetY = 400.dp) {
            navController.popBackStack()
            navController.navigate(ScreenManager.LandingScreen.route)
        }
    }
}
