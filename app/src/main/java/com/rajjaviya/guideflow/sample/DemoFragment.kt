package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.spotlight.SpotlightShape

class DemoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_demo, container, false)
        val btnStart = view.findViewById<Button>(R.id.btnStartFragmentTour)
        val imgHeader = view.findViewById<View>(R.id.imgHeader)
        val text1 = view.findViewById<TextView>(R.id.text1)

        btnStart.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setConfig(TourConfig(spotlightShape = SpotlightShape.ROUNDED_RECT))
                .addStep(GuideStep(
                    targetView = imgHeader, 
                    title = "Profile Header", 
                    description = "Highlights any view perfectly. This uses a rounded rect spotlight."
                ))
                .addStep(GuideStep(
                    targetView = text1, 
                    title = "Fragment Title", 
                    description = "GuideFlow safely scopes the overlay directly inside the Fragment window."
                ))
                .addStep(GuideStep(
                    targetView = btnStart, 
                    title = "Interactive Actions", 
                    description = "Try pressing previous or finish the tour!"
                ))
                .start()
        }
        return view
    }
}
