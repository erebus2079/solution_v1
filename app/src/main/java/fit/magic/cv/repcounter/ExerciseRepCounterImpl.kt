// Copyright (c) 2024 Magic Tech Ltd

package fit.magic.cv.repcounter

import fit.magic.cv.PoseLandmarkerHelper
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

class ExerciseRepCounterImpl : ExerciseRepCounter() {

    private var lastHipKneeAngle: Float = 0f
    private var isInLunge: Boolean = false
    private var lastProgress: Float = 0f

    // Constants for key landmark indices
    private object Landmarks {
        const val LEFT_HIP = 23
        const val LEFT_KNEE = 25
        const val LEFT_ANKLE = 27
        const val RIGHT_HIP = 24
        const val RIGHT_KNEE = 26
        const val RIGHT_ANKLE = 28
    }

    // Constants for progress calculation
    private object LungeThresholds {
        const val MAX_ANGLE = 90f // Deepest lunge position
        const val MIN_ANGLE = 170f // Standing position
        const val ENTER_LUNGE = 0.8f // Progress threshold to enter lunge
        const val EXIT_LUNGE = 0.2f // Progress threshold to exit lunge
    }

    override fun setResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        val landmarks = resultBundle.landmarks

        // Validate landmarks
        if (!isValidLandmarks(landmarks)) {
            sendFeedbackMessage("Invalid landmarks detected.")
            return
        }

        // Extract key landmarks
        val leftLeg = extractLegLandmarks(landmarks, Landmarks.LEFT_HIP, Landmarks.LEFT_KNEE, Landmarks.LEFT_ANKLE)
        val rightLeg = extractLegLandmarks(landmarks, Landmarks.RIGHT_HIP, Landmarks.RIGHT_KNEE, Landmarks.RIGHT_ANKLE)

        if (leftLeg == null || rightLeg == null) {
            sendFeedbackMessage("Key landmarks not detected.")
            return
        }

        // Calculate angles
        val leftAngle = calculateAngle(leftLeg.first, leftLeg.second, leftLeg.third)
        val rightAngle = calculateAngle(rightLeg.first, rightLeg.second, rightLeg.third)

        // Calculate progress
        val currentProgress = calculateProgress(leftAngle, rightAngle)
        sendProgressUpdate(currentProgress)

        // Check for lunge completion
        handleLungeProgress(currentProgress)

        // Store last state for consistency
        lastHipKneeAngle = maxOf(leftAngle, rightAngle)
        lastProgress = currentProgress
    }

    private fun isValidLandmarks(landmarks: List<Map<String, Float>>?): Boolean {
        return !landmarks.isNullOrEmpty() && landmarks.size >= 33
    }

    private fun extractLegLandmarks(
        landmarks: List<Map<String, Float>>,
        hipIndex: Int,
        kneeIndex: Int,
        ankleIndex: Int
    ): Triple<Map<String, Float>, Map<String, Float>, Map<String, Float>>? {
        val hip = landmarks.getOrNull(hipIndex)
        val knee = landmarks.getOrNull(kneeIndex)
        val ankle = landmarks.getOrNull(ankleIndex)

        return if (hip != null && knee != null && ankle != null) {
            Triple(hip, knee, ankle)
        } else null
    }

    private fun calculateAngle(a: Map<String, Float>, b: Map<String, Float>, c: Map<String, Float>): Float {
        val ab = Pair(b["x"]!! - a["x"]!!, b["y"]!! - a["y"]!!)
        val bc = Pair(c["x"]!! - b["x"]!!, c["y"]!! - b["y"]!!)
        val dotProduct = ab.first * bc.first + ab.second * bc.second
        val magnitudeAB = sqrt(ab.first * ab.first + ab.second * ab.second)
        val magnitudeBC = sqrt(bc.first * bc.first + bc.second * bc.second)
        val cosine = dotProduct / (magnitudeAB * magnitudeBC)
        return acos(cosine) * (180.0 / Math.PI).toFloat()
    }

    private fun calculateProgress(leftAngle: Float, rightAngle: Float): Float {
        val leftProgress = (LungeThresholds.MIN_ANGLE - leftAngle) / (LungeThresholds.MIN_ANGLE - LungeThresholds.MAX_ANGLE)
        val rightProgress = (LungeThresholds.MIN_ANGLE - rightAngle) / (LungeThresholds.MIN_ANGLE - LungeThresholds.MAX_ANGLE)
        return maxOf(0f, minOf(1f, (leftProgress + rightProgress) / 2f))
    }

    private fun handleLungeProgress(currentProgress: Float) {
        if (currentProgress > LungeThresholds.ENTER_LUNGE && !isInLunge) {
            isInLunge = true
        } else if (currentProgress < LungeThresholds.EXIT_LUNGE && isInLunge) {
            isInLunge = false
            incrementRepCount()
            sendFeedbackMessage("Rep completed!")
        }
    }
}
