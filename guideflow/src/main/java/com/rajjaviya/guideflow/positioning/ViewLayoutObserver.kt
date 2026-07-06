package com.rajjaviya.guideflow.positioning

import android.view.View
import android.view.ViewTreeObserver

/**
 * Wraps [ViewTreeObserver.OnGlobalLayoutListener] and [View.OnAttachStateChangeListener]
 * to deliver a single unified callback whenever:
 * - The view tree is re-laid out (keyboard shown/hidden, orientation change with
 *   `android:configChanges`, system bar inset changes, etc.)
 * - The target view is re-attached after being detached.
 *
 * ### Usage
 * ```kotlin
 * val observer = ViewLayoutObserver(targetView) { onLayoutChanged() }
 * observer.start()
 * // …later…
 * observer.stop()
 * ```
 *
 * @param view     The view whose layout changes should be observed.
 * @param onChange Called on the main thread whenever a layout change is detected.
 */
internal class ViewLayoutObserver(
    private val view: View,
    private val onChange: () -> Unit,
) {

    private var isObserving = false

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        onChange()
    }

    private val attachStateListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            // Re-register GlobalLayoutListener — it becomes invalid when the view detaches.
            v.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            onChange()
        }

        override fun onViewDetachedFromWindow(v: View) {
            v.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    /**
     * Starts observing layout changes. Idempotent — safe to call multiple times.
     */
    fun start() {
        if (isObserving) return
        isObserving = true

        view.addOnAttachStateChangeListener(attachStateListener)
        if (view.isAttachedToWindow) {
            view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    /**
     * Stops observing and cleans up all listeners. Safe to call even if [start] was never called.
     */
    fun stop() {
        if (!isObserving) return
        isObserving = false

        view.removeOnAttachStateChangeListener(attachStateListener)
        if (view.isAttachedToWindow) {
            view.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
    }
}
