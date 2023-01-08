package com.darren.mygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.darren.mygame.states.daggerImg

val myFont = FontFamily(Font(R.font.nineteenth))

@Composable
fun DrawBackground() {
    Image( //Background
        painter = painterResource(id = R.drawable.bg1),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
fun DrawSword(alpha: Float) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFF))
            .fillMaxSize()
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
fun DrawDagger(modifier: Modifier = Modifier.size(100.dp)) {
    Image(
        modifier = modifier,
        painter = painterResource(id = daggerImg),
        contentDescription = "dagger"
    )
}

@Composable
fun DrawButton(text: String, offsetX: Dp, offsetY: Dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)
    ) {
        Image(
            painter = painterResource(id = R.drawable.button),
            contentDescription = "button",
            modifier = Modifier
                .align(Alignment.Center)
                .scale(2.9f)
                .clickable { onClick() }
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = text,
            fontSize = 30.sp,
            color = Color(0xFFF1F6F5),
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            style = TextStyle(letterSpacing = 3.sp)
        )
    }

}