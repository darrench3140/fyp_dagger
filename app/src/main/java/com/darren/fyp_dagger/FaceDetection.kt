package com.darren.fyp_dagger

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.MutableState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector

class FaceDetectionAnalyzer(
    private val faceDetector: FaceDetector,
    private val lensFacing: MutableState<Int>,
    private val leftEyeProbability: MutableState<Float>,
    private val rightEyeProbability: MutableState<Float>,
    private val smileProbability: MutableState<Float>,
) : ImageAnalysis.Analyzer {
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, 0)
            faceDetector.process(image).addOnSuccessListener { faces ->
                for (face in faces) {
                    if (face.smilingProbability != null) smileProbability.value = face.smilingProbability!!
                    if (face.leftEyeOpenProbability != null) leftEyeProbability.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) face.rightEyeOpenProbability!! else face.leftEyeOpenProbability!!
                    if (face.rightEyeOpenProbability != null) rightEyeProbability.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) face.leftEyeOpenProbability!! else face.rightEyeOpenProbability!!
//                    Log.d("game", "face values: ${leftEyeProbability.value}, ${rightEyeProbability.value}, ${smileProbability.value}")
                    break
                }
                imageProxy.close()
            }
        }
    }
}