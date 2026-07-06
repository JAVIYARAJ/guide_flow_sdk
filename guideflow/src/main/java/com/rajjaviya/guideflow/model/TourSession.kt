package com.rajjaviya.guideflow.model

/**
 * Immutable snapshot of a tour's runtime data.
 *
 * A [TourSession] is created once when the tour starts and lives until the tour
 * reaches a terminal state ([TourState.Completed] or [TourState.Dismissed]).
 *
 * @property steps      Ordered list of all [GuideStep]s in this tour.
 * @property tourId     Optional developer-supplied identifier used for analytics
 *                      and persistence (e.g. "onboarding_v2", "feature_x_tour").
 * @property theme      Visual theme applied to this tour.
 * @property config     Behaviour configuration for this tour.
 * @property startedAt  Epoch millis when the tour was started. Useful for analytics.
 */
data class TourSession(
    val steps: List<GuideStep>,
    val tourId: String? = null,
    val theme: TourTheme = TourTheme.light(),
    val config: TourConfig = TourConfig.default(),
    val startedAt: Long = System.currentTimeMillis(),
) {
    init {
        require(steps.isNotEmpty()) { "A TourSession must have at least one step." }
    }

    /** Total number of steps in this session. */
    val totalSteps: Int get() = steps.size

    /** Returns the step at [index], or `null` if out of bounds. */
    fun stepAt(index: Int): GuideStep? = steps.getOrNull(index)
}
