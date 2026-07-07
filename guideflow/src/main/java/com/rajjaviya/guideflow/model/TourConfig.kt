package com.rajjaviya.guideflow.model

import com.rajjaviya.guideflow.spotlight.SpotlightShape

/**
 * Runtime behaviour configuration for a GuideFlow tour.
 *
 * All properties have sensible defaults so consumers only override what they need:
 *
 * ```kotlin
 * GuideFlow.with(this)
 *     .setConfig(
 *         TourConfig(
 *             dismissOnOverlayClick = false,
 *             showStepIndicator     = true,
 *         )
 *     )
 *     .addStep(...)
 *     .start()
 * ```
 *
 * @property dismissOnOverlayClick  Whether tapping outside the spotlight dismisses the tour.
 * @property dismissOnBackPress     Whether the device back button dismisses the tour.
 * @property showStepIndicator      Whether to show a step counter (e.g. "2 / 5") in the tooltip.
 * @property enablePreviousButton   Whether to show a "Back" button on non-first steps.
 * @property spotlightPadding       Extra space (dp) added around the target view spotlight.
 * @property spotlightCornerRadius  Corner radius (dp) for a rounded-rect spotlight.
 *                                  Set to 0 for a pure oval / circle spotlight.
 * @property spotlightPulseAnimation Whether to continuously pulse the spotlight to draw attention.
 * @property scrollToTarget         Whether the SDK should scroll the target view into the
 *                                  visible area before showing the step (requires ScrollView
 *                                  or RecyclerView support — Milestone 5+).
 * @property autoAdvanceDelayMs     When > 0, each step auto-advances after this many
 *                                  milliseconds without user interaction. 0 = manual only.
 * @property resumeWhereLeftOff     If true, checking the tourId will automatically resume
 *                                  from the last seen step if the tour was interrupted.
 */
data class TourConfig(
    val dismissOnOverlayClick: Boolean = true,
    val dismissOnBackPress: Boolean = true,
    val showStepIndicator: Boolean = false,
    val enablePreviousButton: Boolean = false,
    val spotlightPadding: Int = 16,
    val spotlightCornerRadius: Int = 8,
    val spotlightShape: SpotlightShape = SpotlightShape.ROUNDED_RECT,
    val spotlightPulseAnimation: Boolean = true,
    val scrollToTarget: Boolean = false,
    val autoAdvanceDelayMs: Long = 0L,
    val resumeWhereLeftOff: Boolean = false,
) {
    companion object {

        /** Default config — sensible out-of-the-box behaviour. */
        fun default(): TourConfig = TourConfig()

        /**
         * Strict config — back press and overlay tap do NOT dismiss the tour.
         * Useful for mandatory onboarding flows.
         */
        fun strict(): TourConfig = TourConfig(
            dismissOnOverlayClick = false,
            dismissOnBackPress = false,
        )

        /**
         * Showcase config — shows step indicator and previous button.
         * Good for feature-discovery tours where the user wants to explore freely.
         */
        fun showcase(): TourConfig = TourConfig(
            showStepIndicator = true,
            enablePreviousButton = true,
        )
    }
}
