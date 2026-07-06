package com.rajjaviya.guideflow.host

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner

/**
 * Abstraction over the Android component that hosts a GuideFlow tour.
 *
 * A host provides three things the SDK needs to run:
 * 1. A **root ViewGroup** to attach the overlay to.
 * 2. A **LifecycleOwner** to auto-cancel the tour when the host is destroyed.
 * 3. A **Context** for resource and theme access.
 *
 * Concrete implementations:
 * - [ActivityHost]
 * - [FragmentHost]
 * - [DialogHost]
 * - [BottomSheetHost]
 * - [ViewHost]
 */
interface TourHost {

    /**
     * Returns the [ViewGroup] the overlay should be attached to.
     * This view must be attached to a window when [TourController.start] is called.
     */
    fun getRootView(): ViewGroup

    /**
     * Returns the [LifecycleOwner] associated with this host.
     * The SDK uses this to cancel the tour when the host is destroyed.
     */
    fun getLifecycleOwner(): LifecycleOwner

    /**
     * Returns a [Context] scoped to the host component.
     */
    fun getContext(): Context

    /**
     * Called by the SDK when it needs to locate a specific [View] within this host.
     * Default implementation uses [ViewGroup.findViewWithTag], but hosts backed by
     * Fragments or Dialogs can override to restrict search to their own view hierarchy.
     */
    fun findViewByTag(tag: Any): View? = getRootView().findViewWithTag(tag)
}
