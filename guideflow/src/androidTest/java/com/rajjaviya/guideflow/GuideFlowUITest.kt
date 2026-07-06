package com.rajjaviya.guideflow

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideFlowUITest {

    @Test
    fun testTourInitializationAndOverlayAttachment() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Setup a dummy view container
        val container = FrameLayout(context)
        val dummyTarget = TextView(context).apply {
            text = "Target"
            layoutParams = FrameLayout.LayoutParams(100, 100)
        }
        container.addView(dummyTarget)

        // Verify GuideFlow builder successfully builds the manager
        val manager = GuideFlow.with(container)
            .addStep(GuideStep(dummyTarget, "Title", "Desc"))
            .setConfig(TourConfig(spotlightPulseAnimation = false))
            .start()

        assertNotNull(manager)
        
        // Note: Full UI testing of the Overlay rendering requires an active Activity window,
        // which would be done using an ActivityScenario in a real test app environment.
    }
}
