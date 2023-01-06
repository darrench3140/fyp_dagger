package com.darren.mygame.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.darren.mygame.R
import com.darren.mygame.ScreenManager
import com.darren.mygame.DrawBackground
import com.darren.mygame.DrawDagger

@Composable
fun LandingScreen(navController : NavHostController) {
    DrawBackground()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(500.dp)
                .offset(y = (-60).dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo"
        )
        DrawDagger(modifier = Modifier
            .size(100.dp)
            .offset(y = (-60).dp))

        val gradientColors = listOf(Color(0xee7B4397), Color(0xee3A4F7A))
        val roundedCornerShape = RoundedCornerShape(corner = CornerSize(35.dp))
        Box( //Button
            modifier = Modifier
                .shadow(elevation = 30.dp, shape = roundedCornerShape)
                .clip(roundedCornerShape)
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors),
                )
                .padding(PaddingValues(horizontal = 70.dp, vertical = 16.dp))
                .clickable {
                    navController.navigate(ScreenManager.GameScreen.route)
                }
        ) {
            Text(
                text = "Play",
                fontSize = 26.sp,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun PreviewAct() {
    LandingScreen(navController = rememberNavController())
}