package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig

class SmartTourDemoActivity : AppCompatActivity() {

    private var isPremiumUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_tour_demo)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val btnStandard = findViewById<View>(R.id.btnStandard)
        val btnPremium = findViewById<View>(R.id.btnPremium)
        
        val cardProfile = findViewById<View>(R.id.cardProfile)
        val cardSettings = findViewById<View>(R.id.cardSettings)
        val cardProAnalytics = findViewById<View>(R.id.cardProAnalytics)

        btnStandard.setOnClickListener {
            isPremiumUser = false
            Toast.makeText(this, "Logged in as Standard User", Toast.LENGTH_SHORT).show()
            startSmartTour(cardProfile, cardSettings, cardProAnalytics)
        }

        btnPremium.setOnClickListener {
            isPremiumUser = true
            Toast.makeText(this, "Logged in as Premium User 🚀", Toast.LENGTH_SHORT).show()
            startSmartTour(cardProfile, cardSettings, cardProAnalytics)
        }
    }

    private fun startSmartTour(profile: View, settings: View, analytics: View) {
        // Clear previous completion so we can repeatedly test it
        GuideFlow.clearTourCompletion(this, "smart_tour")

        GuideFlow.with(this)
            .setTourId("smart_tour")
            .setConfig(TourConfig(
                showStepIndicator = true,
                enablePreviousButton = true,
            ))
            .addStep(GuideStep(
                targetView = profile,
                title = "Your Profile",
                description = "Everyone has a profile.",
            ))
            // Conditional step: Only shows if the user is Premium
            .addStep(GuideStep(
                targetView = analytics,
                title = "Pro Analytics",
                description = "Because you are a Premium user, you have access to advanced analytics!",
                condition = { isPremiumUser }, // <--- SMART ENGINE CONDITION
            ))
            .addStep(GuideStep(
                targetView = settings,
                title = "Account Settings",
                description = "Configure your notifications and preferences here.",
            ))
            .start()
    }
}
