package com.darren.fyp_dagger.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
                                fontSize = 45.sp,
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
                                fontSize = 30.sp,
                                color = white,
                                fontFamily = myFont,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterStart).padding(30.dp, 0.dp, 0.dp, 0.dp)
                            )
                            if (settingsItem.type == SettingsUtil.Type.Switch) {
//                                Switch()
                            } else if (settingsItem.type == SettingsUtil.Type.Slider) {

                            } else if (settingsItem.type == SettingsUtil.Type.Button) {

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