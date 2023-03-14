package com.darren.fyp_dagger.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.darren.fyp_dagger.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameData(private val context: Context) {
    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_storage")
        val MAX_SCORE = intPreferencesKey("max_score")
        val FRUIT_COUNT = intPreferencesKey("fruit_count")
        val DAGGER_IN_USE_ID = intPreferencesKey("dagger_in_use_ID")
        val PURCHASED_COUNT = intPreferencesKey("purchased_count")
        val SHOW_CAMERA = booleanPreferencesKey("show_camera")
        val CAMERA_ROTATION = floatPreferencesKey("camera_rotation")
        val CAMERA_OUT_SCALE = floatPreferencesKey("camera_out_scale")
        val CAMERA_IN_SCALE = floatPreferencesKey("camera_in_scale")
        val CAMERA_FACING = booleanPreferencesKey("camera_facing")
        val FACE_LEFT = floatPreferencesKey("face_left")
        val FACE_RIGHT = floatPreferencesKey("face_right")
        val FACE_SMILE = floatPreferencesKey("face_smile")
        val ICON_HELPER = booleanPreferencesKey("icon_helper_options")
    }
    val getMaxScore: Flow<Int> = context.dataStore.data.map { it[MAX_SCORE] ?: 0 }
    val getFruitCount: Flow<Int> = context.dataStore.data.map { it[FRUIT_COUNT] ?: 0 }
    val getDaggerInUseID: Flow<Int> = context.dataStore.data.map { it[DAGGER_IN_USE_ID] ?: 1 }
    val getPurchasedCount: Flow<Int> = context.dataStore.data.map { it[PURCHASED_COUNT] ?: 1 }
    val getShowCamera: Flow<Boolean> = context.dataStore.data.map { it[SHOW_CAMERA] ?: true }
    val getCameraRotation: Flow<Float> = context.dataStore.data.map { it[CAMERA_ROTATION] ?: 0f }
    val getCameraOutScale: Flow<Float> = context.dataStore.data.map { it[CAMERA_OUT_SCALE] ?: 1f }
    val getCameraInScale: Flow<Float> = context.dataStore.data.map { it[CAMERA_IN_SCALE] ?: 1f }
    val getCameraFacing: Flow<Boolean> = context.dataStore.data.map { it[CAMERA_FACING] ?: false }
    val getFaceLeft: Flow<Float> = context.dataStore.data.map { it[FACE_LEFT] ?: 0.2f }
    val getFaceRight: Flow<Float> = context.dataStore.data.map { it[FACE_RIGHT] ?: 0.2f }
    val getFaceSmile: Flow<Float> = context.dataStore.data.map { it[FACE_SMILE] ?: 0.5f }
    val getIconHelper: Flow<Boolean> = context.dataStore.data.map { it[ICON_HELPER] ?: false }
    suspend fun saveSettings() { context.dataStore.edit {
        it[SHOW_CAMERA] = showCameraSettings.value
        it[CAMERA_ROTATION] = cameraRotationSettings.value
        it[CAMERA_OUT_SCALE] = cameraOutScaleSettings.value
        it[CAMERA_IN_SCALE] = cameraInScaleSettings.value
        it[CAMERA_FACING] = lensFacing.value
        it[FACE_LEFT] = faceLeftSensitivity.value
        it[FACE_RIGHT] = faceRightSensitivity.value
        it[FACE_SMILE] = faceSmileSensitivity.value
        it[ICON_HELPER] = iconHelperOption.value
    }}
    suspend fun saveMaxScore(score: Int) { context.dataStore.edit { it[MAX_SCORE] = score } }
    suspend fun saveFruitCount(count: Int) { context.dataStore.edit { it[FRUIT_COUNT] = count }}
    suspend fun saveDaggerInUseID(id: Int) { context.dataStore.edit { it[DAGGER_IN_USE_ID] = id }}
    suspend fun savePurchasedCount(count: Int) { context.dataStore.edit { it[PURCHASED_COUNT] = count }}
}

object GameUtil {
    @Composable
    fun loadData(context: Context, gameMode: String): GameData {
        val gameData = GameData(context)
        LaunchedEffect(true) {
            when (gameMode) {
                "god" -> {
                    gameData.saveFruitCount(999)
                    gameData.savePurchasedCount(16)
                    gameData.saveDaggerInUseID(16)
                }
                "reset" -> {
                    gameData.saveFruitCount(0)
                    gameData.saveMaxScore(0)
                    gameData.savePurchasedCount(1)
                    gameData.saveDaggerInUseID(1)
                }
                "rich" -> {
                    gameData.saveFruitCount(1000)
                }
            }
        }
        maxScore.value = gameData.getMaxScore.collectAsState(initial = 0).value
        fruitCount.value = gameData.getFruitCount.collectAsState(initial = 0).value
        purchasedCount.value = gameData.getPurchasedCount.collectAsState(initial = 1).value
        showCameraSettings.value = gameData.getShowCamera.collectAsState(initial = true).value
        cameraRotationSettings.value = gameData.getCameraRotation.collectAsState(initial = 0f).value
        cameraOutScaleSettings.value = gameData.getCameraOutScale.collectAsState(initial = 1f).value
        cameraInScaleSettings.value = gameData.getCameraInScale.collectAsState(initial = 1f).value
        lensFacing.value = gameData.getCameraFacing.collectAsState(initial = false).value
        faceLeftSensitivity.value = gameData.getFaceLeft.collectAsState(initial = 0.2f).value
        faceRightSensitivity.value = gameData.getFaceRight.collectAsState(initial = 0.2f).value
        faceSmileSensitivity.value = gameData.getFaceSmile.collectAsState(initial = 0.5f).value
        iconHelperOption.value = gameData.getIconHelper.collectAsState(initial = false).value
        daggerUtil.value.Init(gameData.getDaggerInUseID.collectAsState(initial = 1).value)
        spinnerUtil.value.Init()
        return gameData
    }

    fun updateLevelInfo(randomSpeed: MutableState<Boolean>, clockwise: MutableState<Boolean>, spinSpeed: MutableState<Float>, minSpeed: MutableState<Int>, maxSpeed: MutableState<Int>, remainingDaggers: MutableState<Int>) {
        val minusFactor = when (gameDifficulty.value) {
            1 -> 1
            2 -> 1
            3 -> 2
            else -> 0
        }
        val level = gameLevel.value
        clockwise.value = (0..1).random() == 1
        randomSpeed.value = level >= 3
        spinSpeed.value = (2..4).random().toFloat()
        minSpeed.value = ((2+level/4)..(3+level/4)).random()
        maxSpeed.value = ((4+level/4)..(6+level/4)).random()
        remainingDaggers.value = level / 2 + (5..7).random() - minusFactor
    }
}

object StatusBarUtil {
    fun transparentStatusBar(activity: Activity) {
        with(activity) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val vis = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = option or vis
            window.statusBarColor = Color.TRANSPARENT
        }
    }
}

object OrientationUtil {
    @Composable
    fun LockScreenOrientation(orientation: Int) {
        val context = LocalContext.current
        DisposableEffect(Unit) {
            val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                // restore original orientation when view disappears
                activity.requestedOrientation = originalOrientation
            }
        }
    }

    private fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

object PermissionUtil {

    private val hasCameraPermission = mutableStateOf(false)
    private lateinit var launcher: ManagedActivityResultLauncher<String, Boolean>

    fun hasPermission() = hasCameraPermission.value

    @Composable
    fun Initialize(context: Context) {
        hasCameraPermission.value = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasCameraPermission.value = granted
        }
        LaunchedEffect(true) {
            if (!hasCameraPermission.value) launcher.launch(android.Manifest.permission.CAMERA)
        }
    }

    fun requestCameraPermission() {
        Log.d("game", "request camera perm")
        launcher.launch(android.Manifest.permission.CAMERA)
    }
}

data class SpinnerUtil(val totalSpinners: Int = 16) {
    private val spinnerList: MutableList<ImageBitmap> = emptyList<ImageBitmap>().toMutableList()

    @Composable
    fun Init() {
        spinnerList.clear()
        (1..totalSpinners).forEach{
            spinnerList.add(ImageBitmap.imageResource(id = getSpinner(it)))
        }
    }
    fun getRandomSpinner(): ImageBitmap = spinnerList[(0 until totalSpinners).random()]

    private fun getSpinner(id: Int): Int {
        return when(id) {
            1 -> R.drawable.spinner1
            2 -> R.drawable.spinner2
            3 -> R.drawable.spinner3
            4 -> R.drawable.spinner4
            5 -> R.drawable.spinner5
            6 -> R.drawable.spinner6
            7 -> R.drawable.spinner7
            8 -> R.drawable.spinner8
            9 -> R.drawable.spinner9
            10 -> R.drawable.spinner10
            11 -> R.drawable.spinner11
            12 -> R.drawable.spinner12
            13 -> R.drawable.spinner13
            14 -> R.drawable.spinner14
            15 -> R.drawable.spinner15
            16 -> R.drawable.spinner16
            else -> R.drawable.spinner0
        }
    }
}

data class DaggerUtil(val totalDaggers: Int = 16) {
    private val daggerList: MutableList<ImageBitmap> = emptyList<ImageBitmap>().toMutableList()
    private val lockedList: MutableList<ImageBitmap> = emptyList<ImageBitmap>().toMutableList()
    private val daggerInUseID: MutableState<Int> = mutableStateOf(0)
    @Composable
    fun Init(daggerToUse: Int) {
        daggerList.clear()
        lockedList.clear()
        daggerInUseID.value = daggerToUse
        (1..totalDaggers).forEach{ daggerList.add(ImageBitmap.imageResource(id = getDagger(it))) }
        (1..totalDaggers).forEach{ lockedList.add(ImageBitmap.imageResource(id = getLocked(it)))}
    }
    fun setDaggerInUseID(daggerID: Int) { daggerInUseID.value = daggerID }
    fun getDaggerInUseID() = daggerInUseID
    fun getDaggerResource(daggerID: Int = daggerInUseID.value): Int = getDagger(daggerID)
    fun getLockedResource(daggerID: Int) = getLocked(daggerID)
    fun getDaggerBitmap(daggerID: Int = daggerInUseID.value): ImageBitmap = daggerList[daggerID - 1]
    fun getRandomDagger() = daggerList[(0 until totalDaggers).random()]

    private fun getDagger(id: Int): Int {
        return when(id) {
            1 -> R.drawable.d1
            2 -> R.drawable.d2
            3 -> R.drawable.d3
            4 -> R.drawable.d4
            5 -> R.drawable.d5
            6 -> R.drawable.d6
            7 -> R.drawable.d7
            8 -> R.drawable.d8
            9 -> R.drawable.d9
            10 -> R.drawable.d10
            11 -> R.drawable.d11
            12 -> R.drawable.d12
            13 -> R.drawable.d13
            14 -> R.drawable.d14
            15 -> R.drawable.d15
            16 -> R.drawable.d16
            else -> R.drawable.d1
        }
    }
    private fun getLocked(id: Int): Int {
        return when(id) {
            2 -> R.drawable.d2s
            3 -> R.drawable.d3s
            4 -> R.drawable.d4s
            5 -> R.drawable.d5s
            6 -> R.drawable.d6s
            7 -> R.drawable.d7s
            8 -> R.drawable.d8s
            9 -> R.drawable.d9s
            10 -> R.drawable.d10s
            11 -> R.drawable.d11s
            12 -> R.drawable.d12s
            13 -> R.drawable.d13s
            14 -> R.drawable.d14s
            15 -> R.drawable.d15s
            16 -> R.drawable.d16s
            else -> R.drawable.d1
        }
    }
}