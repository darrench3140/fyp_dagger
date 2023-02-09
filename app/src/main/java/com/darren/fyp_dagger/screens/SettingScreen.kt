package com.darren.fyp_dagger.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
            .offset(y = screenHeightDp.times(0.16f) - 45.dp)
            .size(screenWidthDp - 10.dp, screenHeightDp.times(0.68f) - 90.dp)
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
                    val sectionItems = settingsUtil.value.getSectionItems(section)
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
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(30.dp, 0.dp, 0.dp, 0.dp)
                            )
                            when (settingsItem.type) {
                                SettingsUtil.Type.Switch -> {
                                    DrawSwitch(settingsItem.onText, settingsItem.offText, settingsItem.switchOn) { switchOn->
                                        Log.d("game", "$switchOn")
                                    }
                                }
                                SettingsUtil.Type.Slider -> {
                                    Slider(
                                        modifier = Modifier.width(screenWidthDp.times(0.8f)).align(Alignment.Center).offset(y = 25.dp),
                                        value = settingsItem.value.value,
                                        onValueChange = { settingsItem.value.value = it },
                                        valueRange = settingsItem.min..settingsItem.max,
                                        steps = ((settingsItem.max - settingsItem.min) / settingsItem.interval).toInt() - 1
                                    )
                                    Text(
                                        text = ((settingsItem.value.value / settingsItem.interval).toInt() * settingsItem.interval).toString(),
                                        fontSize = 20.sp,
                                        color = white,
                                        fontFamily = myFont,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.CenterEnd).offset(x = (-30).dp)
                                    )
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
fun DrawSwitch(
    checkedText: String = "ON",
    uncheckedText: String = "OFF",
    switchON: MutableState<Boolean>,
    onClick: (Boolean) -> Unit
) {
    val checkedTrackColor by remember { mutableStateOf(Color(0xFF35898F)) }
    val uncheckedTrackColor by remember { mutableStateOf(Color(0xFFe0e0e0)) }
    val animatePosition = animateFloatAsState(
        targetValue = if (switchON.value) with(LocalDensity.current) { 26.dp.toPx() }
        else with(LocalDensity.current) { 10.dp.toPx() }
    )
    Box(modifier = Modifier.size(screenWidthDp, 100.dp)) {
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
                            onClick(switchON.value)
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
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(0.dp, 65.dp, 30.dp, 0.dp)
        )
    }
}

data class SettingsUtil(var initialized: Boolean = false) {
    private val cameraSettings: MutableList<SettingsItem> = emptyList<SettingsItem>().toMutableList()
    private val facialSettings: MutableList<SettingsItem> = emptyList<SettingsItem>().toMutableList()

    enum class Type {
        Button,
        Slider,
        Switch
    }

    data class SettingsItem(
        var title: String,
        var type: Type,
        //slider
        var interval: Float = 0f,
        var min: Float = 0f, var max: Float = 0f,
        //Switch
        var onText: String = "On", var offText: String = "Off",
    ) {
        val value = mutableStateOf(0f)
        val switchOn = mutableStateOf(false)
        //button
        fun onClick() {

        }
    }

    fun getSectionItems(section: String): List<SettingsItem> {
        return when(section) {
            "Camera" -> cameraSettings
            "Facial Detection" -> facialSettings
            else -> emptyList()
        }
    }

    fun initializeSettings() {
        cameraSettings.clear()
        facialSettings.clear()
        cameraSettings.add(SettingsItem(
            title = "Show Camera in Game",
            type = Type.Switch,
            onText = "Yes", offText = "No"
        ))
        cameraSettings.add(SettingsItem(
            title = "Rotation",
            type = Type.Slider,
            interval = 90f, min = 0f, max = 360f
        ))
        cameraSettings.add(SettingsItem(
            title = "Outer Scale",
            type = Type.Slider,
            interval = 0.1f, min = 0.5f, max = 2f
        ))
        cameraSettings.add(SettingsItem(
            title = "Inner Scale",
            type = Type.Slider,
            interval = 0.1f, min = 0.5f, max = 2f
        ))
        cameraSettings.add(SettingsItem(
            title = "Facing",
            type = Type.Switch,
            onText = "Front", offText = "Back"
        ))
        cameraSettings.add(SettingsItem(
            title = "Position",
            type = Type.Button,
        ))
        cameraSettings.add(SettingsItem(
            title = "Permission",
            type = Type.Button,
        ))
        facialSettings.add(SettingsItem(
            title = "Left Eye Sensitivity",
            type = Type.Slider,
            interval = 0.1f, min = 0f, max = 1f
        ))
        facialSettings.add(SettingsItem(
            title = "Right Eye Sensitivity",
            type = Type.Slider,
            interval = 0.1f, min = 0f, max = 1f
        ))
        facialSettings.add(SettingsItem(
            title = "Smiling Sensitivity",
            type = Type.Slider,
            interval = 0.1f, min = 0f, max = 1f
        ))
        initialized = true
    }

}

/*
Camera settings:
    show camera (switch)
    rotation (slider) 0 - 360
    outer scale (float slider) 1 - 2
    inner scale (float slider) 1 - 2
    facing (switch)
    position (button -> then drag)
    get permission (button)
Facial Detection Sensitivity:
    Left Eye sensitivity (float slider) 0 - 1
    right Eye sensitivity (float slider) 0 - 1
    Smiling sensitivity (float slider) 0 - 1
General:
    sound (slider) 0 - 100
    exit (button)
 */