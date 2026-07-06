package com.rajjaviya.guideflow.tooltip

/**
 * Preferred on-screen position of the tooltip relative to the highlighted target view.
 *
 * When [AUTO] is selected the SDK picks whichever side has the most available space
 * and flips automatically if the tooltip would otherwise clip off-screen.
 */
enum class TooltipPosition {
    /** SDK automatically selects the best position. */
    AUTO,

    /** Tooltip appears above the target. */
    TOP,

    /** Tooltip appears below the target. */
    BOTTOM,

    /** Tooltip appears to the left of the target (respects RTL). */
    START,

    /** Tooltip appears to the right of the target (respects RTL). */
    END,
}
