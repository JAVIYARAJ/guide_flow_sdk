package com.rajjaviya.guideflow.host

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner

/**
 * [TourHost] implementation for a [DialogFragment].
 *
 * The overlay is attached to the dialog's own window DecorView so it fills
 * the dialog's bounds and does not bleed into the background Activity.
 *
 * @param dialogFragment The host DialogFragment. Its dialog must be showing
 *                       when the tour starts.
 */
class DialogHost(private val dialogFragment: DialogFragment) : TourHost {

    override fun getRootView(): ViewGroup {
        val dialog = dialogFragment.dialog
        checkNotNull(dialog) {
            "DialogHost: dialog is null. Start the tour after the dialog is shown."
        }
        check(dialog.isShowing) {
            "DialogHost: dialog is not showing."
        }
        return dialog.window?.decorView as? ViewGroup
            ?: error("DialogHost: could not obtain dialog's DecorView.")
    }

    override fun getLifecycleOwner(): LifecycleOwner = dialogFragment.viewLifecycleOwner

    override fun getContext(): Context = dialogFragment.requireContext()
}
