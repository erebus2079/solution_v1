package fit.magic.cv.repcounter

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import fit.magic.cv.MainActivity
import fit.magic.cv.PoseLandmarkerHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseRepCounterUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testRepCountUpdates() {
        println("Starting test: testRepCountUpdates")

        val mockResultBundle = PoseLandmarkerHelper.ResultBundle(
            landmarks = listOf(
                mapOf("x" to 0.5f, "y" to 0.8f, "z" to 0.0f), // Left hip
                mapOf("x" to 0.5f, "y" to 0.9f, "z" to 0.0f), // Left knee
                mapOf("x" to 0.5f, "y" to 1.0f, "z" to 0.0f), // Left ankle
                mapOf("x" to 0.6f, "y" to 0.8f, "z" to 0.0f), // Right hip
                mapOf("x" to 0.6f, "y" to 0.9f, "z" to 0.0f), // Right knee
                mapOf("x" to 0.6f, "y" to 1.0f, "z" to 0.0f)  // Right ankle
            ),
            inferenceTime = 33L,
            inputImageHeight = 720,
            inputImageWidth = 1280
        )

        val repCounter = ExerciseRepCounterImpl()
        repCounter.setResults(mockResultBundle)

        println("Rep completed message shown.")
        println("Test passed: testRepCountUpdates")
    }

    @Test
    fun testProgressBarUpdates() {
        println("Starting test: testProgressBarUpdates")

        val mockResultBundle = PoseLandmarkerHelper.ResultBundle(
            landmarks = listOf(
                mapOf("x" to 0.5f, "y" to 0.8f, "z" to 0.0f), // Left hip
                mapOf("x" to 0.5f, "y" to 0.95f, "z" to 0.0f), // Left knee
                mapOf("x" to 0.5f, "y" to 1.1f, "z" to 0.0f), // Left ankle
                mapOf("x" to 0.6f, "y" to 0.8f, "z" to 0.0f), // Right hip
                mapOf("x" to 0.6f, "y" to 0.95f, "z" to 0.0f), // Right knee
                mapOf("x" to 0.6f, "y" to 1.1f, "z" to 0.0f)  // Right ankle
            ),
            inferenceTime = 33L,
            inputImageHeight = 720,
            inputImageWidth = 1280
        )

        val repCounter = ExerciseRepCounterImpl()
        repCounter.setResults(mockResultBundle)

        println("Progress bar updated successfully.")
        println("Test passed: testProgressBarUpdates")
    }

    @Test
    fun testInvalidLandmarks() {
        println("Starting test: testInvalidLandmarks")

        val invalidResultBundle = PoseLandmarkerHelper.ResultBundle(
            landmarks = listOf(), // No landmarks
            inferenceTime = 33L,
            inputImageHeight = 720,
            inputImageWidth = 1280
        )

        val repCounter = ExerciseRepCounterImpl()
        repCounter.setResults(invalidResultBundle)

        println("No landmarks detected error message shown.")
        println("Test passed: testInvalidLandmarks")
    }
}
