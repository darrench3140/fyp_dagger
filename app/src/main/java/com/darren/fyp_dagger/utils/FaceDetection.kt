package com.darren.fyp_dagger.utils

import android.graphics.PointF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.MutableState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.abs

class FaceDetectionAnalyzer(
    private val faceDetector: FaceDetector,
    private val lensFacing: MutableState<Boolean>,
    private val landmarksList: MutableList<PointF>
) : ImageAnalysis.Analyzer {

    private var noFaceCount = 0
    private lateinit var image: InputImage

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            cameraReady.value = true
            image = InputImage.fromMediaImage(mediaImage, 0)
            faceDetector.process(image).addOnSuccessListener { faces ->
                landmarksList.clear()
                if (faces.isNotEmpty()) {
                    noFaceCount = 0
                    faceDetected.value = true
                    for (face in faces) {
                        val landmarks = face.allLandmarks
                        for (landmark in landmarks) {
                            val point = landmark.position
                            landmarksList.add(PointF(point.x, point.y))
                        }
                        smileDetection(face)
                        if (face.smilingProbability != null) smileP.value = face.smilingProbability!!
                        if (face.leftEyeOpenProbability != null) leftP.value = if (!lensFacing.value) face.rightEyeOpenProbability!! else face.leftEyeOpenProbability!!
                        if (face.rightEyeOpenProbability != null) rightP.value = if (!lensFacing.value) face.leftEyeOpenProbability!! else face.rightEyeOpenProbability!!
                        break
                    }
                } else {
                    noFaceCount++
                    if (noFaceCount >= 5) {
                        faceDetected.value = false
                    }
                }
                imageProxy.close()
            }
        }
    }
}

private fun smileDetection(face: Face) {

    val nose = face.getLandmark(FaceLandmark.NOSE_BASE)?.position
    val mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position
    val mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position

    if (mouthLeft != null) leftYDeltaList.add(mouthLeft.y)
    if (mouthRight != null) rightYDeltaList.add(mouthRight.y)
    if (nose != null) {
        noseXDeltaList.add(nose.x)
        noseYDeltaList.add(nose.y)
    }
    if (leftYDeltaList.size >= 5 && rightYDeltaList.size >= 5 && noseXDeltaList.size >= 5) {

        // check if mouth left and right Y coordinates exceeded threshold
        val noseXExceedThreshold = abs(noseXDeltaList.max() - noseXDeltaList.min()) >= 10f
        val noseYExceedThreshold = abs(noseYDeltaList.max() - noseYDeltaList.min()) >= 10f
        val leftYExceedThreshold = abs(leftYDeltaList.max() - leftYDeltaList.min()) >= 5f
        val rightYExceedThreshold = abs(rightYDeltaList.max() - rightYDeltaList.min()) >= 5f

        if (!noseXExceedThreshold && !noseYExceedThreshold && leftYExceedThreshold && rightYExceedThreshold) {
            //check whether the values are increasing
            val deltaLeftY = leftYDeltaList.windowed(2).count { it[1] > it[0] }
            val deltaRightY = rightYDeltaList.windowed(2).count { it[1] > it[0] }

            /* 1: increasing trend (>= 4), 0: decreasing trend (<=2), 2: half half (3)
                which also implies: leftY decrease = going up, rightY decrease = going up
            */
            val movementLeftY = deltaLeftY >= 3
            val movementRightY = deltaRightY >= 3

            // conditions
            if (!movementLeftY && !movementRightY) {
                smiling.value = true
//                Log.d("gameValues", "Smiling")
            } else if (movementLeftY && movementRightY) {
                smiling.value = false
//                Log.d("gameValues", "Not Smiling")
            }
        }
        leftYDeltaList.removeAt(0)
        rightYDeltaList.removeAt(0)
        noseXDeltaList.removeAt(0)
        noseYDeltaList.removeAt(0)
    }
}