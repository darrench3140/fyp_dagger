package com.darren.fyp_dagger.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.darren.fyp_dagger.utils.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Settings(navController: NavController, gameData: GameData) {
    DrawBackground()
    val sections = listOf("Camera", "Facial Detection")
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = PaddingValues(2.dp, 50.dp, 2.dp, 70.dp)
    ) {
        sections.forEach { section ->
            stickyHeader {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = section,
                        fontSize = 35.sp,
                        color = yellow,
                        fontFamily = myFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            val sectionItems = SettingsUtil.getSectionItems(section)
            items(items = sectionItems) { settingsItem ->
                Box(modifier = Modifier.size(screenWidthDp, 200.dp)) {
                    Box(modifier = Modifier.size(screenWidthDp, 200.dp).background(color = Color(0x7FFFB26B)))
                    Text(
                        text = settingsItem.title,
                        fontSize = 25.sp,
                        color = white,
                        fontFamily = myFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
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
                        description = "Higher sensitivity allows less eye blinking motion",
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