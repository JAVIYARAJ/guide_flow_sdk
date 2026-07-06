package com.rajjaviya.guideflow.host

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner

/**
 * [TourHost] implementation for an arbitrary [ViewGroup].
 *
 * Use this when you need to attach a tour to a specific section of the UI
 * rather than to a full-screen host (Activity / Fragment).
 *
 * The [LifecycleOwner] is resolved automatically from the view tree via
 * [findViewTreeLifecycleOwner]. You may supply one explicitly if needed.
 *
 * ```kotlin
 * // Auto-resolve lifecycle from view tree (preferred)
 * GuideFlow.with(binding.cardContainer).addStep(...).start()
 *
 * // Explicit lifecycle owner
 * val host = ViewHost(binding.cardContainer, viewLifecycleOwner)
 * GuideFlow.with(host).addStep(...).start()
 * ```
 *
 * @param rootView       The [ViewGroup] to attach the overlay to.
 * @param lifecycleOwner The lifecycle that controls the tour's lifespan.
 *                       Defaults to the owner resolved from the view tree.
 */
class ViewHost(
    private val rootView: ViewGroup,
    private val lifecycleOwner: LifecycleOwner? = null,
) : TourHost {

    override fun getRootView(): ViewGroup = rootView

    override fun getLifecycleOwner(): LifecycleOwner {
        return lifecycleOwner
            ?: rootView.findViewTreeLifecycleOwner()
            ?: error(
                "ViewHost: could not resolve a LifecycleOwner from the view tree. " +
                    "Pass a LifecycleOwner explicitly: ViewHost(view, viewLifecycleOwner).",
            )
    }

    override fun getContext(): Context = rootView.context

    companion object {

        /**
         * Creates a [ViewHost] from any [View].
         *
         * If [view] is not itself a [ViewGroup], the first [ViewGroup] ancestor is used
         * as the overlay root.
         */
        fun from(view: View): ViewHost {
            val root: ViewGroup = when {
                view is ViewGroup -> view
                view.parent is ViewGroup -> view.parent as ViewGroup
                else -> error(
                    "ViewHost.from(): could not find a ViewGroup parent for the given view.",
                )
            }
            return ViewHost(root)
        }
    }
}
