package com.darren.fyp_dagger.utils

import android.os.SystemClock
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.darren.fyp_dagger.R
import com.darren.fyp_dagger.states.FruitState
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@Composable
fun DrawCamera() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val faceDetector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build())
    }
    if (PermissionUtil.hasPermission() && showCamera.value) {
        val cameraAlpha = animateFloatAsState(targetValue = if (gameState.value.isOver()) 0f else 1f, animationSpec = tween(500))
        val cameraOffset = animateDpAsState(targetValue = if (!cameraReady.value) screenWidthDp else 0.dp, animationSpec = if (cameraReady.value) tween(500) else tween(0))
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .size(130.dp)
                    .offset(screenWidthDp.times(0.65f) + cameraOffset.value, screenHeightDp.times(0.75f))
                    .rotate(-90f + cameraRotationSettings.value)
                    .scale(cameraOutScaleSettings.value)
                    .clip(CircleShape)
                    .scale(1.5f * cameraInScaleSettings.value)
                    .alpha(if (showCameraSettings.value) cameraAlpha.value else 0f),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder()
                            .setTargetResolution(Size(previewView.width, previewView.height))
                            .build()
                            .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(if (lensFacing.value) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
                            .build()
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(previewView.width, previewView.height))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(cameraExecutor, FaceDetectionAnalyzer(faceDetector, lensFacing))
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("game", "camera preview failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )
        }
    }
}

@Composable
fun DrawControllerIcons() {
    val iconAlpha = animateFloatAsState(targetValue = if (gameState.value.isOver() || gameState.value.isWipe()) 0f else 1f, animationSpec = if (!gameState.value.isWipe()) tween(1000) else tween(0))
    Box(modifier = Modifier.fillMaxSize().alpha(iconAlpha.value)) {
        Text(
            text = if (gameMode.value.isTap()) "TAP" else if (gameMode.value.isSmile()) "SMILE" else "BLINK",
            fontSize = 30.sp,
            color = white,
            fontFamily = myFont,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 80.dp)
                .alpha(0.8f)
        )
    }
}

@Composable
fun DrawBackground() {
    Image( //Background
        painter = painterResource(id = R.drawable.bg),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun DrawSword(alpha: Float) {
    Box(
        modifier = Modifier.fillMaxSize()
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
fun DrawSparkle(id: Int, sparkleAnim: MutableState<Int>) {
    val scale = animateFloatAsState(targetValue = if (sparkleAnim.value == id) 1f else 0f, animationSpec = tween(500))
    var offsetX by remember { mutableStateOf(0.dp) }
    var offsetY by remember { mutableStateOf(0.dp) }
    LaunchedEffect(sparkleAnim.value == id) {
        if (sparkleAnim.value == id) {
            offsetX = (60..140).random().dp
            offsetY = (50..80).random().dp
        }
    }
    Image(
        painter = painterResource(id = R.drawable.sparkle),
        contentDescription = "sparkles",
        modifier = Modifier
            .size(20.dp)
            .offset(x = offsetX, y = -screenHeightDp.times(0.25f) + offsetY)
            .scale(scale.value),
    )
}

@Composable
fun DrawDagger(modifier: Modifier, daggerID: Int = daggerUtil.value.getDaggerResource()) {
    Image(
        modifier = modifier.size(100.dp),
        painter = painterResource(id = daggerID),
        contentDescription = "dagger"
    )
}

@Composable
fun DrawFruit(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.fruit_crack),
        contentDescription = "fruit",
        modifier = modifier
    )
}

@Composable
fun DrawGameModeItem(buttonText: String, descriptionText: String, rewardText: String, offsetY: Dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .alpha(if (buttonText != "TAP" && !PermissionUtil.hasPermission()) 0.5f else 1f)) {
        DrawButton(text = buttonText, offsetY = offsetY, id = R.drawable.button1) {
            onClick()
        }
        DrawFruit(modifier = Modifier
            .align(Alignment.Center)
            .offset(x = 90.dp, y = offsetY))
        DrawFruit(modifier = Modifier
            .align(Alignment.Center)
            .offset(x = (-90).dp, y = offsetY))
        if (buttonText != "TAP") {
            DrawFruit(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 120.dp, y = offsetY)
                    .scale(2f)
            )
            Text(
                text = rewardText,
                fontSize = 25.sp,
                color = Color(0xFFFEC868),
                fontFamily = myFont,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 150.dp, y = offsetY)
            )
        }
        Text(
            text = descriptionText,
            fontSize = 18.sp,
            color = white,
            fontFamily = myFont,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = offsetY + 45.dp)
        )
        if (buttonText != "TAP") {
            Text(
                text = "+ Level Up Bonus: " + when(buttonText) { "SMILE" -> "2"; else -> "3" },
                fontSize = 15.sp,
                color = white,
                fontFamily = myFont,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = offsetY + 65.dp)
            )
            DrawFruit(modifier = Modifier
                .align(Alignment.Center)
                .size(15.dp)
                .offset(x = 75.dp, y = offsetY + 65.dp))
        }
    }
}

@Composable
fun DrawButton(text: String, offsetX: Dp = 0.dp, offsetY: Dp = 0.dp, id: Int = R.drawable.button, alpha: State<Float> = mutableStateOf(1f), onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)
        .alpha(alpha.value)
    ) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "button",
            modifier = Modifier
                .align(Alignment.Center)
                .scale(2.5f)
                .clickable { if (alpha.value == 1f) onClick() }
        )
        Text(
            text = text,
            fontSize = 35.sp,
            color = white,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DrawReturnButton(offsetX: Dp = 50.dp, offsetY: Dp = 0.dp, alpha: State<Float> = mutableStateOf(1f), onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)
        .alpha(alpha.value)) {
        Image(
            painter = painterResource(id = R.drawable.return_button),
            contentDescription = "return menu",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .scale(2.5f)
                .clickable { if (alpha.value == 1f) onClick() }
        )
    }
}

@Composable
fun DrawTopBar() {
    val topBarOffset = animateDpAsState(targetValue = if (gameState.value.isOver()) (-200).dp else 0.dp, animationSpec = tween(durationMillis = 2000))
    Row(
        Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp)
            .offset(0.dp, topBarOffset.value)
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Text( //Score
            text = gameScore.value.toString(),
            modifier = Modifier.weight(0.2f),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            color = yellow,
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Text(
            text = "STAGE ${gameLevel.value}",
            fontSize = 30.sp,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            color = white
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
fun DrawTopFruit() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = fruitCount.value.toString(),
            modifier = Modifier.offset(x = (-25).dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = myFont,
            color = yellow,
        )
        Image(
            painter = painterResource(id = R.drawable.fruit_crack),
            contentDescription = "fruit_top_bar",
            modifier = Modifier
                .size(30.dp)
                .offset(x = (-20).dp)
        )
    }
}

@Composable
fun DrawSwitch(
    checkedText: String = "ON",
    uncheckedText: String = "OFF",
    switchON: MutableState<Boolean>,
    onClick: () -> Unit
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
                            onClick()
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

@Composable
fun DrawSettingsIcon(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "settings",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(45.dp)
                .offset(25.dp, 40.dp)
                .clickable { onClick() }
        )
    }
}

@Composable
fun DrawScoreBoard(
    navController: NavHostController,
    fruitState: FruitState,
) {
    var lastClickTime by remember { mutableStateOf(0L) }
    val stickerAlpha = animateFloatAsState(targetValue = if (showTopScore.value) 1f else 0f, animationSpec = tween(500, 500))
    val stickerScale = animateFloatAsState(targetValue = if (showTopScore.value) 1f else 1.5f, animationSpec = tween(1000, 500))
    val showMulText = remember { mutableStateOf(false) }
    val mulTextAlpha = animateFloatAsState(targetValue = if (showMulText.value) 1f else 0f, animationSpec = tween(500))
    val mulTextScale = animateFloatAsState(targetValue = if (showMulText.value) 1f else 1.5f, animationSpec = tween(1000))
    val scaleRewardText = remember { mutableStateOf(false) }
    val rewardTextScale = animateFloatAsState(targetValue = if (scaleRewardText.value) 1.5f else 1f, animationSpec = tween(500))
    val scoreBoardOffset = animateDpAsState(targetValue = if (gameState.value.isOver()) (-100).dp else -screenHeightDp-200.dp, animationSpec = tween(durationMillis = 500))

    val buttonAlpha = animateFloatAsState(targetValue = if (showScoreBoardButton.value) 1f else 0f, animationSpec = tween(500))

    LaunchedEffect(gameState.value.isOver()) {
        if (gameState.value.isOver() && !showScoreBoardButton.value) {
            var mulFactor = when(gameDifficulty.value) {
                1 -> 2
                2 -> 3
                3 -> 4
                else -> 1
            }
            if (mulFactor != 1 && fruitGained.value != 0) {
                delay(700)
                showMulText.value = true
                delay(1000)
                scaleRewardText.value = true
                delay(500)
                mulFactor = fruitGained.value * (mulFactor - 1)
                fruitGained.value += mulFactor
                showMulText.value = false
                delay(500)
                scaleRewardText.value = false
                while(mulFactor > 0) {
                    fruitState.addBonusFruit()
                    delay(100)
                    mulFactor--
                }
            }
            showScoreBoardButton.value = true
        } else {
            showMulText.value = false
            scaleRewardText.value = false
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = scoreBoardOffset.value)
    ) {
        Image(
            painter = painterResource(id = R.drawable.score_board),
            contentDescription = "score_board",
            modifier = Modifier
                .align(Alignment.Center)
                .size(345.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.top_score),
            contentDescription = "top score sticker",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-5).dp, y = 100.dp)
                .size(130.dp)
                .scale(stickerScale.value),
            alpha = stickerAlpha.value
        )
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = "SCORE (MAX: ${maxScore.value})",
                fontSize = 20.sp,
                color = yellow,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = gameScore.value.toString(),
                fontSize = 40.sp,
                color = white,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "\n\nREWARD",
                fontSize = 20.sp,
                color = yellow,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
            Row {
                Spacer(modifier = Modifier.weight(0.5f))
                Text(
                    text = fruitGained.value.toString(),
                    fontSize = 40.sp,
                    color = white,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.scale(rewardTextScale.value)
                )
                Text(
                    text = when(gameDifficulty.value) { 1 -> "x2"; 2 -> "x3"; 3 -> "x4"; else -> ""},
                    fontSize = 25.sp,
                    color = white,
                    fontFamily = myFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(0.5f)
                        .scale(mulTextScale.value)
                        .alpha(mulTextAlpha.value)
                        .offset(x = 10.dp)
                )
            }
        }
        Box(modifier = Modifier.alpha(buttonAlpha.value)) {
            DrawButton(text = "RESTART", offsetY = screenHeightDp.div(3.12222f), alpha = buttonAlpha) { //screenHeight 843 -> offset 270
                if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                    lastClickTime = SystemClock.elapsedRealtime()
                    gameState.value.setWipe()
                }
            }
            DrawShopButton(offsetY = screenHeightDp.div(2.2784f), alpha = buttonAlpha) { //screenHeight 843 -> offset 370
                if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                    lastClickTime = SystemClock.elapsedRealtime()
                    navController.navigate("shop_screen")
                }
            }
        }
    }
}

@Composable
fun DrawShopButton(offsetY: Dp, alpha: State<Float> = mutableStateOf(1f), onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().alpha(alpha.value)) {
        Image(
            painter = painterResource(id = R.drawable.shop_button_bg),
            contentDescription = "shop_bg",
            modifier = Modifier
                .align(Alignment.Center)
                .size(55.dp)
                .offset(y = offsetY)
                .clickable { if (alpha.value == 1f) onClick() }
        )
        Image(
            painter = painterResource(id = R.drawable.shop_button_fg),
            contentDescription = "shop_fg",
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp)
                .offset(y = offsetY)
        )
    }
}

@Composable
fun DrawShopLight(lightAlpha: State<Float> = mutableStateOf(1f), rotation: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        val shopLightFilter = floatArrayOf(
            1f, 0f, 0f, 0f, 255f,
            0f, 1f, 0f, 0f, 255f,
            0f, 0f, 1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
        Image(
            painter = painterResource(id = R.drawable.shop_light),
            contentDescription = "shop_light",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -screenHeightDp.times(0.3f))
                .size(screenWidthDp.div(2.2833f))
                .rotate(rotation),
            colorFilter = ColorFilter.colorMatrix(ColorMatrix(shopLightFilter)),
            alpha = lightAlpha.value
        )
    }
}

@Composable
fun DrawShopLights() {
    val lightControl = remember{ mutableStateOf(false) }
    val lightAlpha1 = animateFloatAsState(targetValue = if (!lightControl.value) 1f else 0f, animationSpec = tween(500))
    val lightAlpha2 = animateFloatAsState(targetValue = if (lightControl.value) 1f else 0f, animationSpec = tween(500))

    LaunchedEffect(true) {
        while(true) {
            lightControl.value = !lightControl.value
            delay(500)
        }
    }
    DrawShopLight(lightAlpha = lightAlpha1, rotation = 0f)
    DrawShopLight(lightAlpha = lightAlpha2, rotation = 20f)
    DrawShopLight(rotation = 40f)
}

@Composable
fun DrawShopBanner(offset: Dp) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = offset)) {
        Image(
            painter = painterResource(id = R.drawable.shop_banner),
            contentDescription = "shop_banner",
            modifier = Modifier
                .align(Alignment.Center)
                .size(screenWidthDp.div(1.2088f), screenWidthDp.div(10.2748f))
        )
        Text(
            text = "Dagger  Shop",
            fontSize = 25.sp,
            color = white,
            fontFamily = myFont,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DrawShopItem(id: Int, pinkBoxID: MutableState<Int>, greenBoxID: MutableState<Int>) {
    val size = screenWidthDp.div(4.8352f) - 2.dp //size of an item box
    Box(modifier = Modifier.size(size)) {
        Image(
            painter = painterResource(id = R.drawable.shop_grid_bg),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size)
                .clickable {
                    if (id <= purchasedCount.value) {
                        greenBoxID.value = 0
                        pinkBoxID.value = id
                    } else {
                        greenBoxID.value = id
                    }
                }
        )
        DrawDagger(modifier = Modifier
            .align(Alignment.Center)
            .size(size.div(1.10667f))
            .rotate(45f), daggerID = if (id <= purchasedCount.value) daggerUtil.value.getDaggerResource(id) else daggerUtil.value.getLockedResource(id))
        Image(
            painter = painterResource(id = R.drawable.shop_pink_box),
            contentDescription = "selected_dagger",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size),
            alpha = if (id == pinkBoxID.value) 1f else 0f
        )
        Image(
            painter = painterResource(id = R.drawable.shop_green_box),
            contentDescription = "view_dagger",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size),
            alpha = if (id == greenBoxID.value) 1f else 0f
        )
        Image(
            painter = painterResource(id = R.drawable.lock),
            contentDescription = "locked",
            modifier = Modifier
                .align(Alignment.Center)
                .size(size.div(2.5f))
                .offset(x = size.div(4), y = size.div(4)),
            alpha = if (id > purchasedCount.value + 1) 1f else 0f
        )
    }
}

@Composable
fun ShopPurchaseButton(offsetY: Dp, greenBoxID: MutableState<Int>, onClick: (Int) -> Unit) {
    val btnOffset = animateDpAsState(targetValue = if (greenBoxID.value != 0) offsetY else screenHeightDp, animationSpec = tween(500))
    val price = remember{ mutableStateOf(0) }

    LaunchedEffect(greenBoxID.value) {
        if (greenBoxID.value != 0) price.value = ((greenBoxID.value - 1) * 15)
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(y = btnOffset.value)) {
        Image(
            painter = painterResource(id = R.drawable.button1),
            contentDescription = "purchase button",
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp, 64.58.dp)
                .clickable { onClick(price.value) }
        )
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp, 64.58.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = "BUY",
                fontSize = 30.sp,
                color = white,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = price.value.toString(),
                modifier = Modifier.weight(0.3f),
                fontSize = 20.sp,
                color = white,
                fontFamily = myFont,
                textAlign = TextAlign.End
            )
            Image(
                painter = painterResource(id = R.drawable.fruit_crack),
                contentDescription = "fruit",
                modifier = Modifier
                    .size(23.dp)
                    .weight(0.2f)
            )
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}