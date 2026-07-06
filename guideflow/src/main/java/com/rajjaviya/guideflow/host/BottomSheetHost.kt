package com.rajjaviya.guideflow.host

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * [TourHost] implementation for a [BottomSheetDialogFragment].
 *
 * The overlay is attached to the bottom sheet's content view (the sheet container),
 * so it is visually scoped to the sheet rather than covering the full screen.
 *
 * @param bottomSheet The host BottomSheetDialogFragment. Must be showing when the tour starts.
 */
class BottomSheetHost(private val bottomSheet: BottomSheetDialogFragment) : TourHost {

    override fun getRootView(): ViewGroup {
        val view = bottomSheet.view
        checkNotNull(view) {
            "BottomSheetHost: view is null. Start the tour after onViewCreated()."
        }
        check(view is ViewGroup) {
            "BottomSheetHost: root view must be a ViewGroup."
        }
        return view as ViewGroup
    }

    override fun getLifecycleOwner(): LifecycleOwner = bottomSheet.viewLifecycleOwner

    override fun getContext(): Context = bottomSheet.requireContext()
}
