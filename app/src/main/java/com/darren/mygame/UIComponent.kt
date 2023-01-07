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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.darren.mygame.states.daggerImg

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
fun DrawButton(text: String, offset: Dp, navController: NavHostController, dest: String) {
    Image(
        painter = painterResource(id = R.drawable.button),
        contentDescription = "button",
        modifier = Modifier
            .offset(y = offset)
            .scale(2.9f)
            .clickable { navController.navigate(dest) },
    )
    Text(
        modifier = Modifier.offset(y = offset - 32.dp),
        text = text,
        fontSize = 26.sp,
        color = Color.White,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
    )
}