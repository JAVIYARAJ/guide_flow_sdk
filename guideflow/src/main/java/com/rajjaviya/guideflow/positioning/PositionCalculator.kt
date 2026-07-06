package com.rajjaviya.guideflow.positioning

import android.graphics.Rect
import android.graphics.RectF
import android.view.View

/**
 * Utility that converts a [View]'s screen position into coordinates relative
 * to a given overlay [ViewGroup].
 *
 * Using `getLocationOnScreen` for both the target and the overlay then
 * subtracting offsets makes this work correctly for every host type:
 * - Activity (overlay = DecorView at 0,0)
 * - Fragment  (overlay = fragment root at some offset)
 * - Dialog    (overlay = dialog DecorView at some offset)
 */
internal object PositionCalculator {

    /**
     * Returns the bounding [RectF] of [targetView] expressed in the
     * coordinate space of [overlayView], expanded by [paddingPx] on each side.
     *
     * Returns an empty [RectF] if either view is not yet laid out or not
     * attached to a window.
     */
    @Suppress("ReturnCount")
    fun calculate(
        targetView: View,
        overlayView: View,
        paddingPx: Int = 0,
    ): RectF {
        if (!targetView.isAttachedToWindow || !overlayView.isAttachedToWindow) return RectF()
        if (targetView.width == 0 || targetView.height == 0) return RectF()

        val targetLocation = IntArray(2)
        val overlayLocation = IntArray(2)

        targetView.getLocationOnScreen(targetLocation)
        overlayView.getLocationOnScreen(overlayLocation)

        val left = (targetLocation[0] - overlayLocation[0] - paddingPx).toFloat()
        val top = (targetLocation[1] - overlayLocation[1] - paddingPx).toFloat()
        val right = left + targetView.width.toFloat() + paddingPx * 2
        val bottom = top + targetView.height.toFloat() + paddingPx * 2

        return RectF(left, top, right, bottom)
    }

    /**
     * Returns a [Rect] (integer) version for use with canvas clipping operations.
     */
    fun calculateInt(
        targetView: View,
        overlayView: View,
        paddingPx: Int = 0,
    ): Rect {
        val f = calculate(targetView, overlayView, paddingPx)
        return Rect(f.left.toInt(), f.top.toInt(), f.right.toInt(), f.bottom.toInt())
    }
}
