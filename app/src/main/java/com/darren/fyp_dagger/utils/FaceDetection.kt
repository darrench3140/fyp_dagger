package com.darren.fyp_dagger.utils

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.MutableState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector

class FaceDetectionAnalyzer(
    private val faceDetector: FaceDetector,
    private val lensFacing: MutableState<Int>,
) : ImageAnalysis.Analyzer {
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            cameraReady.value = true
            val image = InputImage.fromMediaImage(mediaImage, 0)
            faceDetector.process(image).addOnSuccessListener { faces ->
                for (face in faces) {
                    if (face.smilingProbability != null) smileP.value = face.smilingProbability!!
                    if (face.leftEyeOpenProbability != null) leftP.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) face.rightEyeOpenProbability!! else face.leftEyeOpenProbability!!
                    if (face.rightEyeOpenProbability != null) rightP.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) face.leftEyeOpenProbability!! else face.rightEyeOpenProbability!!
//                    Log.d("game", "face values: ${leftP.value}, ${rightP.value}, ${smileP.value}")
                    break
                }
                imageProxy.close()
            }
        }
    }
}