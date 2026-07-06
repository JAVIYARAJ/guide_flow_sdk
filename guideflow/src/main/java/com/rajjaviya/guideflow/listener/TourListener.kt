package com.rajjaviya.guideflow.listener

import com.rajjaviya.guideflow.model.TourSession

/**
 * Callback interface for GuideFlow tour lifecycle events.
 *
 * All methods have default (no-op) implementations so consumers only
 * override the callbacks they care about.
 *
 * All callbacks are invoked on the **main thread**.
 *
 * Usage:
 * ```kotlin
 * GuideFlow.with(this)
 *     .addStep(...)
 *     .setListener(object : TourListener {
 *         override fun onTourStarted(session: TourSession) { ... }
 *         override fun onTourCompleted(session: TourSession) { ... }
 *     })
 *     .start()
 * ```
 */
interface TourListener {

    /**
     * Called once when the tour starts, before the first step is shown.
     */
    fun onTourStarted(session: TourSession) {}

    /**
     * Called every time a step becomes active (including the first one).
     *
     * @param stepIndex   Zero-based index of the now-visible step.
     * @param totalSteps  Total number of steps in the tour.
     */
    fun onStepVisible(stepIndex: Int, totalSteps: Int) {}

    /**
     * Called when the user taps "Next" and moves forward.
     *
     * @param completedIndex Zero-based index of the step that was just completed.
     */
    fun onStepCompleted(completedIndex: Int) {}

    /**
     * Called when the user taps "Skip" and dismisses the tour mid-way.
     *
     * @param atStepIndex Index of the step that was visible when the user skipped.
     */
    fun onTourDismissed(atStepIndex: Int) {}

    /**
     * Called when the last step is completed and the tour ends naturally.
     */
    fun onTourCompleted(session: TourSession) {}
}
