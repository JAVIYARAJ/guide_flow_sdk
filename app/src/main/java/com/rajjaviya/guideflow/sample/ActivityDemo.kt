package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.spotlight.SpotlightShape
import com.rajjaviya.guideflow.tooltip.TooltipPosition
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.TourSession

class ActivityDemo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnStartLightTour = findViewById<Button>(R.id.btnStartLightTour)
        val btnStartDarkTour = findViewById<Button>(R.id.btnStartDarkTour)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        btnStartLightTour.setOnClickListener { startTour(TourTheme.light()) }
        btnStartDarkTour.setOnClickListener { startTour(TourTheme.dark()) }
        fab.setOnClickListener { Toast.makeText(this, "Action clicked!", Toast.LENGTH_SHORT).show() }
    }

    private fun startTour(theme: TourTheme) {
        GuideFlow.with(this)
            .setTourId("activity_demo_tour")
            .forceShow(true)
            .setTheme(theme)
            .setConfig(
                TourConfig(
                    spotlightShape = SpotlightShape.ROUNDED_RECT,
                    spotlightPulseAnimation = true,
                    dismissOnOverlayClick = false,
                    enablePreviousButton = true,
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.toolbar),
                    title = "App Toolbar",
                    description = "This spotlight highlights the entire toolbar cleanly.",
                    tooltipPosition = TooltipPosition.BOTTOM,
                    animationType = AnimationType.SLIDE_UP
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.welcomeCard),
                    title = "Smart Positioning",
                    description = "GuideFlow automatically calculates the best place to show the tooltip so it doesn't fall off-screen.",
                    tooltipPosition = TooltipPosition.AUTO,
                    animationType = AnimationType.FADE
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.btnSmall),
                    title = "Small Targets",
                    description = "Spotlights can adapt to any view size.",
                    tooltipPosition = TooltipPosition.TOP,
                    animationType = AnimationType.BOUNCE
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.iconProfile),
                    title = "Profile Icon",
                    description = "Perfect for profile pictures.",
                    tooltipPosition = TooltipPosition.START,
                    animationType = AnimationType.SLIDE_UP
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.inputLayout),
                    title = "Form Fields",
                    description = "Highlight text inputs to guide users through complex forms.",
                    tooltipPosition = TooltipPosition.TOP,
                    animationType = AnimationType.SLIDE_UP
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.fab),
                    title = "Floating Actions",
                    description = "Notice how the tooltip elegantly positions itself to the left (START) or top.",
                    tooltipPosition = TooltipPosition.AUTO,
                    animationType = AnimationType.BOUNCE
                )
            )
            .setListener(object : TourListener {
                override fun onTourCompleted(session: TourSession) {
                    Toast.makeText(this@ActivityDemo, "Tour Completed!", Toast.LENGTH_SHORT).show()
                }
                override fun onTourDismissed(atStepIndex: Int) {
                    Toast.makeText(this@ActivityDemo, "Tour Skipped at step $atStepIndex", Toast.LENGTH_SHORT).show()
                }
            })
            .start()
    }
}
