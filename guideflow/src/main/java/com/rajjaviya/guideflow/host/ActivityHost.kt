package com.rajjaviya.guideflow.host

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner

/**
 * [TourHost] implementation for a standard [Activity].
 *
 * The overlay is attached to the Activity's `DecorView` so it sits above
 * every application view, including the ActionBar and status bar scrim.
 *
 * @param activity The host Activity. Must not be finishing or destroyed.
 */
class ActivityHost(private val activity: Activity) : TourHost {

    override fun getRootView(): ViewGroup =
        activity.window.decorView as ViewGroup

    override fun getLifecycleOwner(): LifecycleOwner {
        check(activity is LifecycleOwner) {
            "ActivityHost requires the Activity to implement LifecycleOwner. " +
                "Use AppCompatActivity or ComponentActivity."
        }
        return activity as LifecycleOwner
    }

    override fun getContext(): Context = activity
}
