package com.rajjaviya.guideflow.positioning

import android.util.Log
import android.view.View
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.spotlight.SpotlightBounds
import com.rajjaviya.guideflow.spotlight.SpotlightCalculator
import com.rajjaviya.guideflow.util.awaitLayout
import com.rajjaviya.guideflow.util.isReadyForCoordinates
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * View Resolution Engine — the single place responsible for safely converting
 * a [View] reference into accurate [SpotlightBounds].
 *
 * Handles all the edge cases between a view reference and its usable coordinates:
 * - **Not yet laid out** — waits using [View.awaitLayout] before first resolution.
 * - **Detached views** — emits [SpotlightBounds.NONE] and resumes when re-attached.
 * - **Layout changes** — re-calculates on every global layout pass (rotation, IME, insets).
 * - **Zero-size views** — logs a warning and emits [SpotlightBounds.NONE].
 *
 * ### Usage
 * ```kotlin
 * ViewResolver.observeBounds(targetView, overlayView, config)
 *     .collect { bounds -> overlay.updateSpotlight(bounds) }
 * ```
 */
internal object ViewResolver {

    private const val TAG = "GuideFlow.ViewResolver"

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns a [Flow] that:
     * 1. Waits (suspends) until [targetView] is laid out.
     * 2. Emits [SpotlightBounds] immediately once ready.
     * 3. Re-emits whenever the layout changes (rotation, IME, insets, etc.).
     * 4. Emits [SpotlightBounds.NONE] when the view is detached.
     * 5. Emits are **deduplicated** — only distinct bounds trigger an update.
     *
     * The flow completes when the collector's coroutine is cancelled.
     *
     * @param targetView  The step's target view to highlight.
     * @param overlayView The [com.rajjaviya.guideflow.overlay.GuideOverlayView] acting as
     *                    the coordinate origin.
     * @param config      Provides padding and shape configuration.
     */
    fun observeBounds(
        targetView: View,
        overlayView: View,
        config: TourConfig,
    ): Flow<SpotlightBounds> = callbackFlow<SpotlightBounds> {

        // Step 1 — wait for the view to be ready before registering the layout observer
        targetView.awaitLayout()
        
        // Step 1.5 — auto-scroll to the view if needed (RecyclerView, ScrollView, etc)
        AutoScroller.scrollToTargetIfNeeded(targetView, config)

        // Step 2 — calculate and emit immediately
        trySend(resolveBounds(targetView, overlayView, config))

        // Step 3 — watch for layout changes and re-emit
        val observer = ViewLayoutObserver(targetView) {
            trySend(resolveBounds(targetView, overlayView, config))
        }
        observer.start()

        // Step 4 — handle attach/detach
        val attachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                trySend(resolveBounds(v, overlayView, config))
            }

            override fun onViewDetachedFromWindow(v: View) {
                Log.w(TAG, "Target view detached from window during tour step.")
                trySend(SpotlightBounds.NONE)
            }
        }
        targetView.addOnAttachStateChangeListener(attachListener)

        // Clean up when the collector cancels
        awaitClose {
            observer.stop()
            targetView.removeOnAttachStateChangeListener(attachListener)
        }
    }.distinctUntilChanged()

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    /**
     * Returns a [ViewIssue] describing why [view] cannot be resolved,
     * or `null` if the view is ready.
     */
    fun detectIssue(view: View): ViewIssue? = when {
        !view.isAttachedToWindow -> ViewIssue.NOT_ATTACHED
        !view.isLaidOut -> ViewIssue.NOT_LAID_OUT
        view.width == 0 || view.height == 0 -> ViewIssue.ZERO_SIZE
        view.visibility == View.GONE || view.visibility == View.INVISIBLE -> ViewIssue.NOT_VISIBLE
        else -> null
    }

    // -------------------------------------------------------------------------
    // Internal resolution
    // -------------------------------------------------------------------------

    private fun resolveBounds(
        targetView: View,
        overlayView: View,
        config: TourConfig,
    ): SpotlightBounds {
        val issue = detectIssue(targetView)
        if (issue != null) {
            Log.w(TAG, issue.message)
            return SpotlightBounds.NONE
        }

        return SpotlightCalculator.calculate(
            targetView = targetView,
            overlayView = overlayView,
            config = config,
            shape = config.spotlightShape,
        )
    }
}
