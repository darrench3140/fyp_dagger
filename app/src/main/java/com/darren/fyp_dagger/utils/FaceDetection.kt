package com.darren.fyp_dagger.utils

import android.graphics.PointF
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.MutableState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.sqrt

class FaceDetectionAnalyzer(
    private val faceDetector: FaceDetector,
    private val lensFacing: MutableState<Boolean>,
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
                    if (face.leftEyeOpenProbability != null) leftP.value = if (!lensFacing.value) face.rightEyeOpenProbability!! else face.leftEyeOpenProbability!!
                    if (face.rightEyeOpenProbability != null) rightP.value = if (!lensFacing.value) face.leftEyeOpenProbability!! else face.rightEyeOpenProbability!!
                    Log.d("gameValues", "face values: ${leftP.value}, ${rightP.value}, ${smileP.value}")
                    break
                }
                imageProxy.close()
            }
        }
    }
}

private fun calculateSmilingRatio(face: Face): Float {

    //Get the mouth landmarks for the face first
    val mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position
    val mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position
    val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position

    // Calculate the distance between the left and right mouth corners
    val mouthWidth = mouthLeft?.let { left ->
        mouthRight?.let { right ->
            distanceBetweenPoints(left , right)
        }
    }

    // Calculate the distance between the top and bottom mouth corners
    val mouthHeight = mouthBottom?.let { bottom ->
        mouthLeft?.let { left ->
            distanceBetweenPoints(left, bottom)
        }
    }

    // Calculate the smiling ratio as the ratio of the mouth height to the mouth width
    return mouthWidth?.let { width ->
        mouthHeight?.let { height ->
            height / width
        }
    } ?: 0f
}

// Define a function to calculate the distance between two points
fun distanceBetweenPoints(p1: PointF?, p2: PointF?): Float {
    return if (p1 != null && p2 != null) {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    } else {
        0f
    }
}