package com.rajjaviya.guideflow.sample

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.tooltip.TooltipPosition
import com.rajjaviya.guideflow.tooltip.TooltipViewProvider
import androidx.core.graphics.toColorInt

class CustomViewDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view_demo)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Custom Views"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val cardCustomContent = findViewById<View>(R.id.cardCustomContent)
        val cardFullCustomProvider = findViewById<View>(R.id.cardFullCustomProvider)

        cardCustomContent.setOnClickListener {
            GuideFlow.clearTourCompletion(this, "demo_custom_content")
            
            GuideFlow.with(this)
                .setTourId("demo_custom_content")
                .addStep(
                    GuideStep(
                        targetView = cardCustomContent,
                        customContentLayoutRes = R.layout.custom_guide_step, // <--- INJECTED CONTENT
                        tooltipPosition = TooltipPosition.BOTTOM
                    )
                )
                .start()
        }

        cardFullCustomProvider.setOnClickListener {
            GuideFlow.clearTourCompletion(this, "demo_full_custom")

            GuideFlow.with(this)
                .setTourId("demo_full_custom")
                .setTooltipViewProvider(MyStrictDesignTooltipProvider()) // <--- FULL CUSTOM UI
                .addStep(
                    GuideStep(
                        targetView = cardFullCustomProvider,
                        title = "Strict Design System",
                        description = "This entire tooltip was built manually, bypassing the SDK's default card layout.",
                    )
                )
                .start()
        }
    }

    /**
     * An example of a fully custom TooltipViewProvider.
     * Developers can inflate whatever they want and just hook up the clicks!
     */
    class MyStrictDesignTooltipProvider : TooltipViewProvider {
        override fun getView(
            context: Context,
            step: GuideStep,
            theme: TourTheme,
            config: TourConfig,
            currentIndex: Int,
            totalSteps: Int,
            onNext: () -> Unit,
            onPrevious: () -> Unit,
            onSkip: () -> Unit
        ): View {
            // Build a completely custom view programmatically (or inflate an XML layout)
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor("#E91E63".toColorInt()) // Hot Pink background
                setPadding(40, 40, 40, 40)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val title = TextView(context).apply {
                text = step.title
                textSize = 20f
                setTextColor(Color.WHITE)
            }
            container.addView(title)

            val desc = TextView(context).apply {
                text = step.description
                textSize = 14f
                setTextColor(Color.WHITE)
                setPadding(0, 16, 0, 32)
            }
            container.addView(desc)

            val nextBtn = Button(context).apply {
                text = "AWESOME!"
                setBackgroundColor(Color.WHITE)
                setTextColor(Color.parseColor("#E91E63"))
                setOnClickListener { onNext() }
            }
            container.addView(nextBtn)

            return container
        }
    }
}
