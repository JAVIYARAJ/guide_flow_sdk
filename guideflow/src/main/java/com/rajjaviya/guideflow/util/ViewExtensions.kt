package com.rajjaviya.guideflow.util

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// ---------------------------------------------------------------------------
// Layout readiness
// ---------------------------------------------------------------------------

/**
 * `true` when the view is both attached to a window and has been through
 * at least one layout pass (i.e. its width/height are non-zero and valid).
 */
internal val View.isReadyForCoordinates: Boolean
    get() = isAttachedToWindow && isLaidOut && width > 0 && height > 0

/**
 * Suspends until the view has been laid out.
 *
 * - If the view is already laid out, returns immediately.
 * - If the view is detached, suspends until it is re-attached **and** laid out.
 * - The suspension is cancellable — cancelling the parent [Job] cleans up all listeners.
 */
internal suspend fun View.awaitLayout() {
    if (isReadyForCoordinates) return

    suspendCancellableCoroutine { continuation ->
        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isReadyForCoordinates) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    if (continuation.isActive) continuation.resume(Unit)
                }
            }
        }

        val attachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
            }

            override fun onViewDetachedFromWindow(v: View) {
                // Remove the layout listener — it will be re-added on re-attach
                v.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
            }
        }

        if (isAttachedToWindow) {
            viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        }
        addOnAttachStateChangeListener(attachListener)

        continuation.invokeOnCancellation {
            removeOnAttachStateChangeListener(attachListener)
            if (isAttachedToWindow) {
                viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Coordinate helpers
// ---------------------------------------------------------------------------

/**
 * Returns the bounding [Rect] of this view in **screen** (absolute) coordinates.
 * Returns an empty [Rect] if the view is not ready.
 */
internal fun View.screenRect(): Rect {
    if (!isReadyForCoordinates) return Rect()
    val location = IntArray(2)
    getLocationOnScreen(location)
    return Rect(location[0], location[1], location[0] + width, location[1] + height)
}

/**
 * Returns the bounding [Rect] of this view in **window** coordinates.
 * Returns an empty [Rect] if the view is not ready.
 */
internal fun View.windowRect(): Rect {
    if (!isReadyForCoordinates) return Rect()
    val location = IntArray(2)
    getLocationInWindow(location)
    return Rect(location[0], location[1], location[0] + width, location[1] + height)
}

// ---------------------------------------------------------------------------
// Density conversion
// ---------------------------------------------------------------------------

/** Converts [dp] to pixels using this view's display metrics. */
internal fun View.dpToPx(dp: Float): Float =
    dp * resources.displayMetrics.density

/** Converts [dp] (Int) to pixels using this view's display metrics. */
internal fun View.dpToPx(dp: Int): Int =
    (dp * resources.displayMetrics.density).toInt()

/** Converts [px] to dp using this view's display metrics. */
internal fun View.pxToDp(px: Float): Float =
    px / resources.displayMetrics.density
