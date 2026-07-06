package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.spotlight.SpotlightShape
import com.rajjaviya.guideflow.tooltip.TooltipPosition

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Very simple programmatic UI for the sample app
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(64, 64, 64, 64)
        }
        
        val headerText = TextView(this).apply {
            text = "Welcome to GuideFlow"
            textSize = 24f
            setPadding(0, 0, 0, 64)
        }
        
        val actionButton = Button(this).apply {
            text = "Tap me"
        }
        
        val startTourButton = Button(this).apply {
            text = "Start Onboarding Tour"
            setPadding(0, 100, 0, 0)
        }

        layout.addView(headerText)
        layout.addView(actionButton)
        layout.addView(startTourButton)
        setContentView(layout)

        startTourButton.setOnClickListener {
            // Using GuideFlow API
            GuideFlow.with(this)
                .setTourId("main_demo_tour")
                .forceShow(true) // Always show it when the button is clicked for the demo
                .setTheme(TourTheme.dark())
                .setConfig(
                    TourConfig(
                        spotlightShape = SpotlightShape.ROUNDED_RECT,
                        spotlightPulseAnimation = true,
                        dismissOnOverlayClick = false
                    )
                )
                .addStep(
                    GuideStep(
                        targetView = headerText,
                        title = "Welcome Header",
                        description = "This is the main title of our screen.",
                        tooltipPosition = TooltipPosition.BOTTOM,
                        animationType = AnimationType.BOUNCE
                    )
                )
                .addStep(
                    GuideStep(
                        targetView = actionButton,
                        title = "Primary Action",
                        description = "Clicking this button will perform the primary action in the app.",
                        tooltipPosition = TooltipPosition.BOTTOM,
                        animationType = AnimationType.FADE
                    )
                )
                .addStep(
                    GuideStep(
                        targetView = startTourButton,
                        title = "Replay Tour",
                        description = "You can replay this tour at any time by clicking here again.",
                        tooltipPosition = TooltipPosition.TOP,
                        animationType = AnimationType.SLIDE_UP
                    )
                )
                .start()
        }
    }
}
