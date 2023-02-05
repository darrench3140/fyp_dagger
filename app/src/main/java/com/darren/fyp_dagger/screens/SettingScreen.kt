package com.darren.fyp_dagger.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.darren.fyp_dagger.R
import com.darren.fyp_dagger.utils.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Settings(navController: NavController, gameData: GameData) {
    DrawBackground()
    Box(modifier = Modifier.fillMaxSize()) {
        DrawShopLights()
        Image(
            painter = painterResource(id = R.drawable.settings3d),
            contentDescription = "Settings 3D icon",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -screenHeightDp.times(0.3f))
                .size(screenWidthDp.div(4.935f))
        )
        val sections = listOf("Facial Detection", "Camera")
        Box(modifier = Modifier
            .align(Alignment.Center)
            .offset(y = screenHeightDp.times(0.13f))
            .size(screenWidthDp, screenHeightDp.times(0.6f))
            ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(2.dp, 2.dp, 2.dp, 2.dp)
            ) {
                sections.forEach { section ->
                    stickyHeader {
                        Box(modifier = Modifier
                            .size(screenWidthDp, 70.dp)
                            .background(Color(0xFF181D31))) {
                            Text(
                                text = section,
                                fontSize = 30.sp,
                                color = yellow,
                                fontFamily = myFont,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    val sectionItems = SettingsUtil.getSectionItems(section)
                    items(items = sectionItems) { settingsItem ->
                        Box(modifier = Modifier
                            .size(screenWidthDp, 100.dp)
                            .background(color = Color(0x4F181D31))) {
                            Text(
                                text = settingsItem.title,
                                fontSize = 20.sp,
                                color = white,
                                fontFamily = myFont,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterStart).padding(30.dp, 0.dp, 0.dp, 0.dp)
                            )
                            when (settingsItem.type) {
                                SettingsUtil.Type.Switch -> {
                                    Switch2 {

                                    }
                                }
                                SettingsUtil.Type.Slider -> {

                                }
                                SettingsUtil.Type.Button -> {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
    DrawReturnButton(offsetY = (-50).dp) {
        navController.popBackStack()
    }
}

@Composable
fun Switch2(
    checkedText: String = "ON",
    uncheckedText: String = "OFF",
    onClick: () -> Unit
) {
    val switchON = remember { mutableStateOf(true) }
    val checkedTrackColor by remember { mutableStateOf(Color(0xFF35898F)) }
    val uncheckedTrackColor by remember { mutableStateOf(Color(0xFFe0e0e0)) }
    val animatePosition = animateFloatAsState(
        targetValue = if (switchON.value) with(LocalDensity.current) { 26.dp.toPx() }
        else with(LocalDensity.current) { 10.dp.toPx() }
    )
    Box(modifier = Modifier.size(screenWidthDp, screenHeightDp.times(0.6f))) {
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(0.dp, 0.dp, 30.dp, 0.dp)
                .size(width = 36.dp, height = 20.dp)
                .scale(scale = 1.5f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            switchON.value = !switchON.value
                        }
                    )
                }
        ) {
            drawRoundRect(
                color = if (switchON.value) checkedTrackColor else uncheckedTrackColor,
                cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = if (switchON.value) checkedTrackColor else uncheckedTrackColor,
                radius = 6.dp.toPx(),
                center = Offset(
                    x = animatePosition.value,
                    y = size.height / 2
                )
            )
        }
        Text(
            text = if (switchON.value) checkedText else uncheckedText,
            fontSize = 20.sp,
            color = white,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterEnd).padding(0.dp, 65.dp, 30.dp, 0.dp)
        )
    }
}

object SettingsUtil {
    enum class Type {
        Button,
        Slider,
        Switch
    }
    data class SettingsItem(
        var title: String,
        var type: Type,
        var value: Number = 0,
        var description: String = "",
        var interval: Number = 0,
        var range: IntRange = 0..1,
    )
    fun getSectionItems(section: String): List<SettingsItem> {
        when (section) {
            "Camera" -> {
                return listOf(
                    SettingsItem(
                        title = "Show Camera in Game",
                        type = Type.Switch,
                    ),
                    SettingsItem(
                        title = "Rotation",
                        type = Type.Slider,
                        description = "Modify the rotation of camera",
                        interval = 90,
                        range = 0..360
                    ),
                    SettingsItem(
                        title = "Outer Scale",
                        type = Type.Slider,
                        description = "Modify the outer scale of camera",
                        interval = 0.1f,
                        range = 1..2
                    ),
                    SettingsItem(
                        title = "Inner Scale",
                        type = Type.Slider,
                        description = "Modify the inner scale of camera",
                        interval = 0.1f,
                        range = 1..2
                    ),
                    SettingsItem(
                        title = "Facing",
                        type = Type.Switch,
                        description = "Set the Facing of camera"
                    ),
                    SettingsItem(
                        title = "Position",
                        type = Type.Button,
                        description = "Click to modify the position of camera to be placed"
                    ),
                    SettingsItem(
                        title = "Permission",
                        type = Type.Button,
                        description = "Click to enter permission settings of this app"
                    )
                )
            }
            "Facial Detection" -> {
                return listOf(
                    SettingsItem(
                        title = "Left Eye Sensitivity",
                        type = Type.Slider,
                        description = "Increasing it allows less eye blinking motion",
                        interval = 0.1f
                    ),
                    SettingsItem(
                        title = "Right Eye Sensitivity",
                        type = Type.Slider,
                        description = "Increasing it allows less eye blinking motion",
                        interval = 0.1f
                    ),
                    SettingsItem(
                        title = "Smiling Sensitivity",
                        type = Type.Slider,
                        description = "Decreasing it allows less smiling motion",
                        interval = 0.1f
                    )
                )
            }
            else -> {
                return listOf()
            }
        }
    }
}

/*
Camera settings:
    show camera (switch)
    rotation (slider)
    outer scale (slider)
    inner scale (slider)
    facing (switch)
    position (button -> then drag)
    get permission (button)
Facial Detection Sensitivity:
    Left Eye sensitivity (slider)
    right Eye sensitivity (slider)
    Smiling sensitivity (slider)
General:
    sound (slider)
    exit button
 */