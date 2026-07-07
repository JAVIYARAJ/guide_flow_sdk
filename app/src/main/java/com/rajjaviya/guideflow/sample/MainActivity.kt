package com.rajjaviya.guideflow.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dashboard)

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardActivityDemo).setOnClickListener {
            startActivity(Intent(this, ActivityDemo::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardFragmentDemo).setOnClickListener {
            startActivity(Intent(this, FragmentDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardListDemo).setOnClickListener {
            startActivity(Intent(this, ListDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardDialogDemo).setOnClickListener {
            DemoBottomSheet().show(supportFragmentManager, "demo_bottom_sheet")
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardSpotlightDemo).setOnClickListener {
            startActivity(Intent(this, SpotlightDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardErrorDemo).setOnClickListener {
            startActivity(Intent(this, ErrorHandlingDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardSmartTourDemo).setOnClickListener {
            startActivity(Intent(this, SmartTourDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardJsonTourDemo).setOnClickListener {
            startActivity(Intent(this, JsonTourDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardNavigationDemo).setOnClickListener {
            startActivity(Intent(this, NavigationDemoActivity::class.java))
        }
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardGithub).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com/JAVIYARAJ/guide_flow_sdk")))
        }
    }
}
