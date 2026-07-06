package com.rajjaviya.guideflow.spotlight

import android.graphics.RectF

/**
 * Resolved, drawable spotlight geometry.
 *
 * Produced by [SpotlightCalculator] and consumed by [com.rajjaviya.guideflow.overlay.GuideOverlayView].
 *
 * @property bounds       The bounding rect in overlay-view coordinates (includes padding).
 * @property shape        Which shape to draw for the cutout.
 * @property cornerRadius Corner radius in pixels — only used when [shape] is [SpotlightShape.ROUNDED_RECT].
 */
internal data class SpotlightBounds(
    val bounds: RectF,
    val shape: SpotlightShape = SpotlightShape.ROUNDED_RECT,
    val cornerRadius: Float = 0f,
) {
    /** `true` when bounds have been calculated (non-zero size). */
    val isValid: Boolean get() = !bounds.isEmpty

    companion object {
        /** Sentinel representing "no spotlight" — full overlay, no cutout. */
        val NONE = SpotlightBounds(RectF())
    }
}
