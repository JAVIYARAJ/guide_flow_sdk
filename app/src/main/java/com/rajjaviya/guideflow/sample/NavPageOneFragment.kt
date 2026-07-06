package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourSession

class NavPageOneFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav_page_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardStepOne = view.findViewById<View>(R.id.cardStepOne)

        // Wait slightly to ensure view is laid out, or just start it directly
        view.post {
            GuideFlow.with(this)
                .forceShow(true)
                .addStep(GuideStep(
                    targetView = cardStepOne,
                    title = "Screen One",
                    description = "This is the first screen. When you hit Next, we will navigate to Screen Two and continue.",
                    nextButtonLabel = "Go to Screen 2"
                ))
                .setListener(object : TourListener {
                    override fun onTourCompleted(session: TourSession) {
                        // Navigate to Fragment 2
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragmentContainer, NavPageTwoFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    override fun onTourDismissed(atStepIndex: Int) {}
                })
                .start()
        }
    }
}
