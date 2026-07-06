package com.rajjaviya.guideflow.model

/**
 * Sealed class representing every possible state of a GuideFlow tour.
 *
 * Consumers can observe `TourController.state` and react to each branch:
 *
 * ```kotlin
 * controller.state.collect { state ->
 *     when (state) {
 *         is TourState.Idle      -> { /* nothing running */ }
 *         is TourState.Active    -> updateProgressIndicator(state.currentIndex, state.totalSteps)
 *         is TourState.Paused    -> showResumeButton()
 *         is TourState.Completed -> trackAnalyticsEvent("tour_completed")
 *         is TourState.Dismissed -> trackAnalyticsEvent("tour_skipped", state.atStepIndex)
 *     }
 * }
 * ```
 */
sealed class TourState {

    /** Tour has not started, or has been fully torn down. */
    data object Idle : TourState()

    /**
     * Tour is running and a step is visible on screen.
     *
     * @property session     The live [TourSession] driving the tour.
     * @property currentIndex Zero-based index of the currently visible step.
     * @property totalSteps  Total number of steps in the tour.
     */
    data class Active(
        val session: TourSession,
        val currentIndex: Int,
        val totalSteps: Int,
    ) : TourState() {
        val isFirstStep: Boolean get() = currentIndex == 0
        val isLastStep: Boolean get() = currentIndex == totalSteps - 1
        val currentStep: GuideStep get() = session.steps[currentIndex]
    }

    /**
     * Tour is temporarily paused (e.g. app went to background).
     *
     * @property session The [TourSession] that was active when paused.
     */
    data class Paused(val session: TourSession) : TourState()

    /**
     * Tour completed naturally — the user went through every step.
     *
     * @property tourId Optional ID supplied when the tour was started.
     */
    data class Completed(val tourId: String?) : TourState()

    /**
     * Tour was dismissed before completion.
     *
     * @property tourId      Optional ID of the dismissed tour.
     * @property atStepIndex The step index at which the user chose to skip.
     */
    data class Dismissed(val tourId: String?, val atStepIndex: Int) : TourState()
}
