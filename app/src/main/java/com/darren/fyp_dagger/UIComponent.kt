package com.darren.fyp_dagger

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@Composable
fun DrawCamera(showCamera: MutableState<Boolean>) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val lensFacing = remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val faceDetector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build())
    }
    if (PermissionUtil.hasPermission() && showCamera.value) {
        val cameraAlpha = animateFloatAsState(targetValue = if (gameState.value.isOver()) 0f else 1f, animationSpec = tween(500))
        val cameraOffset = animateDpAsState(targetValue = if (!cameraReady.value) screenWidthDp else 0.dp, animationSpec = if (cameraReady.value) tween(500) else tween(0))
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomEnd)
                    .offset((-25).dp + cameraOffset.value, (-25).dp)
                    .rotate(-90f)
                    .clip(CircleShape)
                    .scale(1.5f)
                    .alpha(cameraAlpha.value),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder()
                            .build()
                            .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing.value)
                            .build()
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(previewView.width, previewView.height))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build().also {
                                it.setAnalyzer(
                                    cameraExecutor,
                                    FaceDetectionAnalyzer(faceDetector, lensFacing)
                                )
                            }
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
    val iconAlpha = animateFloatAsState(targetValue = if (gameState.value.isOver() || gameState.value.isWipe() || !cameraReady.value) 0f else 1f, animationSpec = if (!gameState.value.isWipe()) tween(1000) else tween(0))
    if (gameDifficulty.value >= 2) {
        Box(modifier = Modifier.fillMaxSize().alpha(iconAlpha.value)) {
            Image( //Left Eye
                painter = painterResource(id = if (gameMode.value.isLeft() || gameMode.value.isBoth()) R.drawable.eye_close else R.drawable.eye_open),
                contentDescription = "left eye icon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (-30).dp, y = 40.dp),
                alpha = if (gameMode.value.isLeft() || gameMode.value.isBoth()) 1f else 0.7f
            )
            Image( //Right Eye
                painter = painterResource(id = if (gameMode.value.isRight() || gameMode.value.isBoth()) R.drawable.eye_close else R.drawable.eye_open),
                contentDescription = "right eye icon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 30.dp, y = 40.dp),
                alpha = if (gameMode.value.isRight() || gameMode.value.isBoth()) 1f else 0.7f
            )
        }
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
    Box(modifier = Modifier.fillMaxSize()) {
        DrawButton(text = buttonText, offsetY = offsetY, id = R.drawable.button1) {
            onClick()
        }
        DrawFruit(modifier = Modifier
            .align(Alignment.Center)
            .offset(x = 90.dp, y = offsetY))
        DrawFruit(modifier = Modifier
            .align(Alignment.Center)
            .offset(x = (-90).dp, y = offsetY))
        DrawFruit(modifier = Modifier
            .align(Alignment.Center)
            .offset(x = 120.dp, y = offsetY)
            .scale(2f))
        Text(
            text = rewardText,
            fontSize = 25.sp,
            color = Color(0xFFFEC868),
            fontFamily = myFont,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 150.dp, y = offsetY)
        )
        Text(
            text = descriptionText,
            fontSize = 18.sp,
            color = white,
            fontFamily = myFont,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = offsetY + 45.dp)
        )
    }
}

@Composable
fun DrawButton(text: String, offsetX: Dp = 0.dp, offsetY: Dp = 0.dp, id: Int = R.drawable.button,onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)
    ) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "button",
            modifier = Modifier
                .align(Alignment.Center)
                .scale(2.5f)
                .clickable { onClick() }
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
fun DrawReturnButton(offsetX: Dp = 50.dp, offsetY: Dp = 0.dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(offsetX, offsetY)) {
        Image(
            painter = painterResource(id = R.drawable.return_button),
            contentDescription = "return menu",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .scale(2.5f)
                .clickable { onClick() }
        )
    }
}

@Composable
fun DrawTopBar(topBarOffset: State<Dp>) {
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
fun DrawSettingsIcon(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "settings",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(20.dp)
                .offset(25.dp, 40.dp)
                .clickable { onClick() }
        )
    }
}

@Composable
fun DrawScoreBoard(
    navController: NavHostController,
    scoreBoardOffset: State<Dp>,
    showTopScore: MutableState<Boolean>,
) {
    var lastClickTime by remember { mutableStateOf(0L) }
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
            contentDescription = "top_score",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-5).dp, y = 100.dp)
                .size(130.dp),
            alpha = if(showTopScore.value) 1f else 0f
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
            Text(
                text = fruitGained.value.toString(),
                fontSize = 40.sp,
                color = white,
                fontFamily = myFont,
                fontWeight = FontWeight.Bold,
            )
        }
        DrawButton(text = "RESTART", offsetY = screenHeightDp.div(3.12222f)) { //screenHeight 843 -> offset 270
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                lastClickTime = SystemClock.elapsedRealtime()
                gameState.value.setWipe()
                showTopScore.value = false
            }
        }
        DrawShopButton(offsetY = screenHeightDp.div(2.2784f)) { //screenHeight 843 -> offset 370
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                lastClickTime = SystemClock.elapsedRealtime()
                navController.navigate("shop_screen")
            }
        }
        DrawReturnButton(offsetY = 50.dp) {
            if (SystemClock.elapsedRealtime() - lastClickTime > 2000L) {
                lastClickTime = SystemClock.elapsedRealtime()
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun DrawShopButton(offsetY: Dp, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.shop_button_bg),
            contentDescription = "shop_bg",
            modifier = Modifier
                .align(Alignment.Center)
                .size(55.dp)
                .offset(y = offsetY)
                .clickable { onClick() }
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
                .size(size)
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