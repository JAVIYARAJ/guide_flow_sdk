package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.TourConfig

class JsonTourDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json_tour_demo)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val btnFetchTour = findViewById<Button>(R.id.btnFetchTour)
        btnFetchTour.setOnClickListener {
            startJsonTour()
        }
    }

    private fun startJsonTour() {
        // Simulate fetching this JSON from a remote server/API
        val jsonPayload = """
            {
              "steps": [
                {
                  "targetId": "btnLike",
                  "title": "Like this post",
                  "description": "Show some love by tapping the like button.",
                  "tooltipPosition": "TOP",
                  "animationType": "BOUNCE",
                  "spotlightShape": "CIRCLE",
                  "pointerOffset": 0.5,
                  "showNextButton": true,
                  "showSkipButton": true,
                  "nextButtonLabel": "Got it",
                  "skipButtonLabel": "Dismiss",
                  "previousButtonLabel": "Back",
                  "tag": "like_step"
                },
                {
                  "targetId": "btnShare",
                  "title": "Share with friends",
                  "description": "Spread the word by sharing this across your network.",
                  "tooltipPosition": "BOTTOM",
                  "animationType": "SLIDE_UP",
                  "spotlightShape": "ROUNDED_RECT",
                  "pointerOffset": 0.8,
                  "showNextButton": true,
                  "showSkipButton": false,
                  "nextButtonLabel": "Continue",
                  "skipButtonLabel": "Skip",
                  "previousButtonLabel": "Prev",
                  "tag": "share_step"
                },
                {
                  "targetId": "btnSubscribe",
                  "title": "Never miss out",
                  "description": "Subscribe to our channel for the latest updates.",
                  "tooltipPosition": "AUTO",
                  "animationType": "CIRCULAR_REVEAL",
                  "spotlightShape": "OVAL",
                  "pointerOffset": 0.2,
                  "showNextButton": true,
                  "showSkipButton": true,
                  "nextButtonLabel": "Finish",
                  "skipButtonLabel": "Skip",
                  "previousButtonLabel": "Go Back",
                  "tag": "subscribe_step"
                }
              ]
            }
        """.trimIndent()

        GuideFlow.clearTourCompletion(this, "json_demo")

        GuideFlow.with(this)
            .setTourId("json_demo")
            .setConfig(TourConfig(
                showStepIndicator = true,
                enablePreviousButton = true
            ))
            .loadFromJson(jsonPayload) // <--- LOAD JSON HERE
            .start()
    }
}
