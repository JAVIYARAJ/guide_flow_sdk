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
    private val initialSteps: List<GuideStep>,
    val tourId: String? = null,
    val theme: TourTheme = TourTheme.light(),
    val config: TourConfig = TourConfig.default(),
    val tooltipViewProvider: com.rajjaviya.guideflow.tooltip.TooltipViewProvider? = null,
    val startedAt: Long = System.currentTimeMillis(),
) {
    private val _steps = initialSteps.toMutableList()
    
    /** Ordered list of all active [GuideStep]s in this tour. */
    val steps: List<GuideStep> get() = _steps.toList()

    init {
        require(_steps.isNotEmpty()) { "A TourSession must have at least one step." }
    }

    /** Total number of steps in this session. */
    val totalSteps: Int get() = _steps.size

    /** Returns the step at [index], or `null` if out of bounds. */
    fun stepAt(index: Int): GuideStep? = _steps.getOrNull(index)

    /** Inserts a new step immediately after the step with the given tag. */
    fun addStepAfter(tag: String, newStep: GuideStep) {
        val index = _steps.indexOfFirst { it.tag == tag }
        if (index != -1) {
            _steps.add(index + 1, newStep)
        } else {
            _steps.add(newStep) // Fallback to appending if tag not found
        }
    }

    /** Removes all steps matching the given tag. */
    fun removeStep(tag: String) {
        _steps.removeAll { it.tag == tag }
    }
}
