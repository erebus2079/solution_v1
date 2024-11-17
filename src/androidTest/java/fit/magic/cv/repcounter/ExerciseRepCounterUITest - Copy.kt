package fit.magic.cv.repcounter

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import fit.magic.cv.MainActivity
import fit.magic.cv.PoseLandmarkerHelper
import fit.magic.cv.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseRepCounterUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testRepCountUpdates() {
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

        onView(withId(R.id.exercise_info_view))
            .check(matches(withText("Rep completed!")))

        onView(withId(R.id.rep_count_view))
            .check(matches(withText("1"))) // Assuming the rep count is displayed.
    }

    @Test
    fun testProgressBarUpdates() {
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

        onView(withId(R.id.progress_bar))
            .check(matches(hasProgress(50))) // Check if progress is updated.
    }

    @Test
    fun testInvalidLandmarks() {
        val invalidResultBundle = PoseLandmarkerHelper.ResultBundle(
            landmarks = listOf(), // No landmarks
            inferenceTime = 33L,
            inputImageHeight = 720,
            inputImageWidth = 1280
        )

        val repCounter = ExerciseRepCounterImpl()
        repCounter.setResults(invalidResultBundle)

        onView(withId(R.id.exercise_info_view))
            .check(matches(withText("No landmarks detected.")))
    }
}

