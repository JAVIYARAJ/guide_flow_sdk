package com.rajjaviya.guideflow.spotlight

/**
 * Shape of the transparent spotlight cutout drawn over the target view.
 */
enum class SpotlightShape {
    /**
     * Perfect circle. The radius is based on the maximum dimension (width or height)
     * of the target view, ensuring the entire view is enclosed.
     */
    CIRCLE,

    /**
     * Oval / ellipse inscribed inside the spotlight bounding rect.
     * Good for wide or tall pill-shaped buttons.
     */
    OVAL,

    /**
     * Rounded rectangle. Corner radius is driven by [com.rajjaviya.guideflow.model.TourConfig.spotlightCornerRadius].
     * Good for cards, buttons, and list items.
     */
    ROUNDED_RECT,

    /**
     * Sharp rectangle with no rounding. Good for toolbar items and exact-fit highlights.
     */
    RECT,
}
