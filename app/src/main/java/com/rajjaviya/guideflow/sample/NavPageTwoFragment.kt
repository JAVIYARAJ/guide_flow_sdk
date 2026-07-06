package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep

class NavPageTwoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav_page_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFinish = view.findViewById<View>(R.id.btnFinish)

        btnFinish.setOnClickListener {
            Toast.makeText(requireContext(), "Flow finished!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        view.post {
            GuideFlow.with(this)
                .forceShow(true)
                .addStep(GuideStep(
                    targetView = btnFinish,
                    title = "Screen Two",
                    description = "We navigated to a completely different fragment, and the tour seamlessly continued here!",
                    nextButtonLabel = "Done"
                ))
                .start()
        }
    }
}
