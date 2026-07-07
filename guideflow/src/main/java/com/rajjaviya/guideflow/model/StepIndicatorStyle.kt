package com.rajjaviya.guideflow.model

/**
 * Defines the visual style for the progress indicator shown in the tooltip.
 */
enum class StepIndicatorStyle {
    /** No indicator is shown. */
    NONE,
    
    /** A simple text label, e.g., "Step 1 of 5". */
    TEXT,
    
    /** iOS-style pagination dots. */
    DOTS,
    
    /** A horizontal progress bar. */
    LINEAR
}
