package com.darren.mygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darren.mygame.R
import com.darren.mygame.states.daggerImg

@Composable
fun DrawBackground() {
    Image( //Background
        painter = painterResource(id = R.drawable.bg),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds,
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