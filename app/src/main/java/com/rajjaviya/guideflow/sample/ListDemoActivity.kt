package com.rajjaviya.guideflow.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme

class ListDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_demo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Auto-Scroll Targeting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val scrollView = findViewById<NestedScrollView>(R.id.scrollView)
        val container = findViewById<LinearLayout>(R.id.container)

        val views = mutableListOf<MaterialCardView>()
        for (i in 0..30) {
            val card = MaterialCardView(this).apply {
                radius = 24f
                cardElevation = 4f
                setCardBackgroundColor(Color.WHITE)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 32
                }
                layoutParams = params
            }

            val text = TextView(this).apply {
                text = "Scroll Item #${i + 1}"
                textSize = 18f
                setTextColor(Color.parseColor("#1E293B"))
                setPadding(64, 80, 64, 80)
            }
            
            card.addView(text)
            container.addView(card)
            views.add(card)
        }

        // Give views time to layout
        scrollView.post {
            GuideFlow.with(this)
                .forceShow(true)
                .setTheme(TourTheme.dark())
                .setConfig(TourConfig(scrollToTarget = true))
                .addStep(GuideStep(
                    targetView = views[0], 
                    title = "First Item", 
                    description = "This is item at the top of the list."
                ))
                .addStep(GuideStep(
                    targetView = views[5], 
                    title = "Auto Scroll", 
                    description = "GuideFlow automatically scrolled down to find this item!"
                ))
                .addStep(GuideStep(
                    targetView = views[25], 
                    title = "Deep Scroll", 
                    description = "It seamlessly scrolls deep into the hierarchy."
                ))
                .start()
        }
    }
}
