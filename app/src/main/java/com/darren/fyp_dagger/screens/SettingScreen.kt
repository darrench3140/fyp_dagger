package com.darren.fyp_dagger.screens

import android.os.SystemClock
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    var lastClickTime by remember { mutableStateOf(0L) }
    val saveToDataStore = remember{ mutableStateOf(false) }

    LaunchedEffect(saveToDataStore.value) {
        if (saveToDataStore.value) {
            gameData.saveSettings()
            saveToDataStore.value = false
        }
    }

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
        val sections = listOf("Game", "Camera")
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
                                    DrawSwitch(settingsItem.onText, settingsItem.offText, settingsItem.switchOn) {
                                        saveToDataStore.value = true
                                    }
                                }
                                SettingsUtil.Type.Slider -> {
                                    Slider(
                                        modifier = Modifier
                                            .width(screenWidthDp.times(0.8f))
                                            .align(Alignment.Center)
                                            .offset(y = 25.dp),
                                        value = settingsItem.slideValue.value,
                                        onValueChange = { settingsItem.slideValue.value = it },
                                        onValueChangeFinished = { saveToDataStore.value = true },
                                        valueRange = settingsItem.min..settingsItem.max,
                                        steps = ((settingsItem.max - settingsItem.min) / settingsItem.interval).toInt() - 1
                                    )
                                    Text(
                                        text = if (settingsItem.interval < 1) String.format("%.1f", settingsItem.slideValue.value) else String.format("%.0f", settingsItem.slideValue.value),
                                        fontSize = 20.sp,
                                        color = white,
                                        fontFamily = myFont,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .offset(x = (-30).dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    DrawReturnButton(offsetY = (-50).dp) {
        if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
            lastClickTime = SystemClock.elapsedRealtime()
            navController.popBackStack()
        }
    }
}

data class SettingsUtil(var initialized: Boolean = false) {
    private val cameraSettings: MutableList<SettingsItem> = emptyList<SettingsItem>().toMutableList()
    private val gameSettings: MutableList<SettingsItem> = emptyList<SettingsItem>().toMutableList()

    enum class Type {
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
        var slideValue = mutableStateOf(0f)
        var switchOn = mutableStateOf(false)
    }

    fun getSectionItems(section: String): List<SettingsItem> {
        return when(section) {
            "Camera" -> cameraSettings
            "Game" -> gameSettings
            else -> emptyList()
        }
    }

    fun initializeSettings() {
        cameraSettings.clear()
        gameSettings.clear()
        var s = SettingsItem(
            title = "Show Camera in Game",
            type = Type.Switch,
            onText = "Yes", offText = "No"
        )
        s.switchOn = showCameraSettings
        cameraSettings.add(s)
        s = SettingsItem(
            title = "Rotation",
            type = Type.Slider,
            interval = 90f, min = 0f, max = 360f
        )
        s.slideValue = cameraRotationSettings
        cameraSettings.add(s)
        s = SettingsItem(
            title = "Outer Scale",
            type = Type.Slider,
            interval = 0.1f, min = 0.5f, max = 1.5f
        )
        s.slideValue = cameraOutScaleSettings
        cameraSettings.add(s)
        s = SettingsItem(
            title = "Inner Scale",
            type = Type.Slider,
            interval = 0.1f, min = 0.5f, max = 1.5f
        )
        s.slideValue = cameraInScaleSettings
        cameraSettings.add(s)
        s = SettingsItem(
            title = "Facing",
            type = Type.Switch,
            onText = "Front", offText = "Back"
        )
        s.switchOn = lensFacing
        cameraSettings.add(s)
        s = SettingsItem(
            title = "Left Eye Sensitivity",
            type = Type.Slider,
            interval = 0.1f, min = 0f, max = 1f
        )
        s.slideValue = faceLeftSensitivity
        gameSettings.add(s)
        s = SettingsItem(
            title = "Right Eye Sensitivity",
            type = Type.Slider,
            interval = 0.1f, min = 0f, max = 1f
        )
        s.slideValue = faceRightSensitivity
        gameSettings.add(s)
        s = SettingsItem(
            title = "Smiling Sensitivity",
            type = Type.Slider,
            interval = 0.1f, min = 0f, max = 1f
        )
        s.slideValue = faceSmileSensitivity
        gameSettings.add(s)
        s = SettingsItem(
            title = "In Game Text Helper",
            type = Type.Switch,
            onText = "Enabled", offText = "Disabled"
        )
        s.switchOn = textHelperOption
        gameSettings.add(s)
        s = SettingsItem(
            title = "ML approach for Smiling",
            type = Type.Switch,
            onText = "Classification", offText = "Landmark"
        )
        s.switchOn = mlModeOption
        gameSettings.add(s)
        initialized = true
    }
}
