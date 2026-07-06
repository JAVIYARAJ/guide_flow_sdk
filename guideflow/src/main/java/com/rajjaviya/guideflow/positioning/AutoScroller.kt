package com.rajjaviya.guideflow.positioning

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.rajjaviya.guideflow.model.TourConfig
import kotlinx.coroutines.delay

/**
 * Handles scrolling target views into the visible area before they are highlighted.
 */
internal object AutoScroller {

    /**
     * Attempts to scroll the view hierarchy so that [targetView] is visible.
     * This supports standard [android.widget.ScrollView], [NestedScrollView],
     * and [RecyclerView] automatically through Android's native focus/scroll APIs.
     *
     * @return `true` if a scroll was likely performed, `false` otherwise.
     */
    suspend fun scrollToTargetIfNeeded(targetView: View, config: TourConfig) {
        if (!config.scrollToTarget) return

        // Wait a frame to ensure the view tree is settled
        delay(16)

        // Native Android request to scroll the view into the visible area
        val rect = Rect(0, 0, targetView.width, targetView.height)
        val scrolled = targetView.requestRectangleOnScreen(rect, true)

        if (scrolled) {
            // Give the smooth scroll time to finish before showing the spotlight.
            // In a production app, you might wait for a scroll state listener,
            // but a fixed delay is often sufficient for standard scroll speeds.
            delay(300)
        } else {
            // Fallback for deeply nested or custom scroll containers like ViewPager2
            // that might not honor requestRectangleOnScreen correctly.
            var parent: ViewParent? = targetView.parent
            while (parent != null && parent is ViewGroup) {
                if (parent is RecyclerView || parent is NestedScrollView) {
                    parent.requestChildFocus(targetView, targetView)
                    delay(300)
                    break
                }
                parent = parent.parent
            }
        }
    }
}
