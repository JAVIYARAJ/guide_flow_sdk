package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.model.StepIndicatorStyle

class StepIndicatorDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_indicator_demo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Progress Indicators"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val btnDots = findViewById<View>(R.id.btnDots)
        val btnText = findViewById<View>(R.id.btnText)
        val btnNone = findViewById<View>(R.id.btnNone)

        btnDots.setOnClickListener { startTour(StepIndicatorStyle.DOTS) }
        btnText.setOnClickListener { startTour(StepIndicatorStyle.TEXT) }
        btnNone.setOnClickListener { startTour(StepIndicatorStyle.NONE) }
    }

    private fun startTour(style: StepIndicatorStyle) {
        GuideFlow.with(this)
            .forceShow(true)
            .setConfig(
                TourConfig(
                    stepIndicatorStyle = style,
                    enablePreviousButton = true
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.btnDots),
                    title = "Step 1",
                    description = "This is the first step in the sequence."
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.btnText),
                    title = "Step 2",
                    description = "This is the middle step."
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.btnNone),
                    title = "Step 3",
                    description = "This is the final step. The indicator updates automatically!"
                )
            )
            .start()
    }
}
