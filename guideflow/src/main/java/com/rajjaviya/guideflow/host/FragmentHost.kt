package com.rajjaviya.guideflow.host

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 * [TourHost] implementation for a [Fragment].
 *
 * The overlay is attached to the Fragment's own root view, so the tour is
 * scoped visually to the fragment's area rather than the full screen.
 *
 * If you need a full-screen overlay from a Fragment, wrap the Activity instead:
 * ```kotlin
 * GuideFlow.with(ActivityHost(requireActivity())).addStep(...).start()
 * ```
 *
 * @param fragment The host Fragment. Must have a non-null view when the tour starts.
 */
class FragmentHost(private val fragment: Fragment) : TourHost {

    override fun getRootView(): ViewGroup {
        val root = fragment.view
        checkNotNull(root) {
            "FragmentHost: the fragment's view is null. " +
                "Start the tour after onViewCreated() has been called."
        }
        check(root is ViewGroup) {
            "FragmentHost: the fragment's root view must be a ViewGroup."
        }
        return root as ViewGroup
    }

    override fun getLifecycleOwner(): LifecycleOwner = fragment.viewLifecycleOwner

    override fun getContext(): Context = fragment.requireContext()
}
