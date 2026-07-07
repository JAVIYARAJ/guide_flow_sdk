package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme

class GlassmorphismDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glassmorphism_demo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Glassmorphism"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val cardTarget1 = findViewById<View>(R.id.cardTarget1)
        val cardTarget2 = findViewById<View>(R.id.cardTarget2)
        val btnStart = findViewById<View>(R.id.btnStart)

        btnStart.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setTheme(TourTheme.dark())
                .setConfig(
                    TourConfig(
                        enablePreviousButton = true
                    )
                )
                .addStep(
                    GuideStep(
                        targetView = cardTarget1,
                        title = "Glassmorphism Effect",
                        animationType = AnimationType.SPRING_PHYSICS,
                        description = "Notice how the colorful background is beautifully blurred behind the overlay, just like native iOS or modern web."
                    )
                )
                .addStep(
                    GuideStep(
                        targetView = cardTarget2,
                        title = "Crystal Clear Target",
                        animationType = AnimationType.SPRING_PHYSICS,
                        description = "Even though the background is blurred, the target view remains perfectly sharp and readable!"
                    )
                )
                .start()
        }
    }
}
