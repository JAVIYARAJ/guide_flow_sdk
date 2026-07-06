package com.rajjaviya.guideflow.spotlight

import android.view.View
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.positioning.PositionCalculator

/**
 * Converts a target [View] into [SpotlightBounds] expressed in the coordinate
 * space of the overlay view.
 *
 * This is a stateless utility object — all context is passed per call.
 */
internal object SpotlightCalculator {

    /**
     * Calculates [SpotlightBounds] for [targetView] relative to [overlayView].
     *
     * @param targetView  The view the user should focus on.
     * @param overlayView The [com.rajjaviya.guideflow.overlay.GuideOverlayView] instance.
     * @param config      Provides [TourConfig.spotlightPadding] and [TourConfig.spotlightCornerRadius].
     * @param shape       The desired cutout shape. Defaults to [SpotlightShape.ROUNDED_RECT].
     */
    fun calculate(
        targetView: View,
        overlayView: View,
        config: TourConfig,
        shape: SpotlightShape = SpotlightShape.ROUNDED_RECT,
    ): SpotlightBounds {
        val paddingPx = dpToPx(targetView, config.spotlightPadding)
        var bounds = PositionCalculator.calculate(targetView, overlayView, paddingPx)

        if (bounds.isEmpty) return SpotlightBounds.NONE

        if (shape == SpotlightShape.CIRCLE) {
            val cx = bounds.centerX()
            val cy = bounds.centerY()
            val size = maxOf(bounds.width(), bounds.height())
            val half = size / 2f
            bounds.set(cx - half, cy - half, cx + half, cy + half)
        }

        val cornerRadiusPx = dpToPx(targetView, config.spotlightCornerRadius).toFloat()

        return SpotlightBounds(
            bounds = bounds,
            shape = shape,
            cornerRadius = cornerRadiusPx,
        )
    }

    private fun dpToPx(view: View, dp: Int): Int {
        val density = view.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
