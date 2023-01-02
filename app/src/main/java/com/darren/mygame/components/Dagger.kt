package com.darren.mygame.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darren.mygame.R

var dagger = R.drawable.d1

@Composable
fun Dagger(modifier: Modifier = Modifier.size(100.dp)) {
    Image(
        modifier = modifier,
        painter = painterResource(id = dagger),
        contentDescription = "dagger"
    )
}