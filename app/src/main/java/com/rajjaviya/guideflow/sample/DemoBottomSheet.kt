package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep

class DemoBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_demo, container, false)
        val btnStart = view.findViewById<Button>(R.id.btnStartFragmentTour)
        val text1 = view.findViewById<TextView>(R.id.text1)
        val imgHeader = view.findViewById<View>(R.id.imgHeader)
        
        text1.text = "Dialog Scope"

        btnStart.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .addStep(GuideStep(targetView = imgHeader, title = "Sheet Header", description = "We can highlight views even inside a bottom sheet."))
                .addStep(GuideStep(targetView = text1, title = "Dialog Isolation", description = "The guide doesn't bleed out into the host activity!"))
                .start()
        }
        return view
    }
}
