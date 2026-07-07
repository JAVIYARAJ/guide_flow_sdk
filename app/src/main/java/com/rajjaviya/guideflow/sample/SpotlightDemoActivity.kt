package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.spotlight.SpotlightShape

class SpotlightDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotlight_demo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Spotlight Shapes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val viewCircle = findViewById<View>(R.id.viewCircle)
        val viewRectangle = findViewById<View>(R.id.viewRectangle)
        val viewRounded = findViewById<View>(R.id.viewRounded)
        val viewOval = findViewById<View>(R.id.viewOval)

        viewCircle.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setConfig(com.rajjaviya.guideflow.model.TourConfig(spotlightShape = SpotlightShape.CIRCLE))
                .addStep(GuideStep(targetView = viewCircle, title = "Circle", description = "A perfect circle spotlight."))
                .start()
        }

        viewRectangle.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setConfig(com.rajjaviya.guideflow.model.TourConfig(spotlightShape = SpotlightShape.RECT))
                .addStep(GuideStep(targetView = viewRectangle, title = "Rectangle", description = "Sharp edges for traditional views."))
                .start()
        }

        viewRounded.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setConfig(com.rajjaviya.guideflow.model.TourConfig(spotlightShape = SpotlightShape.ROUNDED_RECT))
                .addStep(GuideStep(targetView = viewRounded, title = "Rounded Rect", description = "Modern UI cards look great with this."))
                .start()
        }

        viewOval.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setConfig(com.rajjaviya.guideflow.model.TourConfig(spotlightShape = SpotlightShape.OVAL))
                .addStep(GuideStep(targetView = viewOval, title = "Oval", description = "Adapts perfectly to pill-shaped views."))
                .start()
        }
    }
}
