package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep

class ErrorHandlingDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_handling_demo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Error Handling Demo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val viewValid = findViewById<View>(R.id.viewValid)
        val viewGone = findViewById<View>(R.id.viewGone)
        val btnStartTour = findViewById<Button>(R.id.btnStartTour)

        // Create a view that is not attached to the window
        val detachedView = View(this)

        btnStartTour.setOnClickListener {
            try {
                GuideFlow.with(this)
                    .forceShow(true)
                    .addStep(GuideStep(
                        targetView = viewValid,
                        title = "Valid View",
                        description = "This is a normal view on screen."
                    ))
                    .addStep(GuideStep(
                        targetView = viewGone,
                        title = "GONE View",
                        description = "View is GONE, SDK should skip it gracefully."
                    ))
                    .addStep(GuideStep(
                        targetView = detachedView,
                        title = "Detached View",
                        description = "View is not attached to window, SDK should skip it gracefully."
                    ))
                    .addStep(GuideStep(
                        targetView = btnStartTour,
                        title = "Survived!",
                        description = "We made it to the end without crashing!"
                    ))
                    .start()
            } catch (e: Exception) {
                Toast.makeText(this, "App Crashed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
