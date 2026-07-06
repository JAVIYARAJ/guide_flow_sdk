package com.rajjaviya.guideflow.controller

import com.rajjaviya.guideflow.host.TourHost
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourSession
import com.rajjaviya.guideflow.model.TourState
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.overlay.OverlayManager
import kotlinx.coroutines.flow.StateFlow

/**
 * Top-level orchestrator for a GuideFlow tour.
 *
 * [TourManager] wires a [TourHost] to a [TourController] and an [OverlayManager],
 * then exposes the minimal control surface that consumers need after calling `.start()`.
 *
 * ```kotlin
 * val manager: TourManager = GuideFlow.with(this)
 *     .setTheme(TourTheme.dark())
 *     .setConfig(TourConfig.showcase())
 *     .addStep(...)
 *     .start()
 *
 * // Control later:
 * manager.next()
 * manager.skip()
 * ```
 */
class TourManager internal constructor(
    private val host: TourHost,
    steps: List<GuideStep>,
    tourId: String?,
    theme: TourTheme,
    config: TourConfig,
    listener: TourListener?,
) {

    private val session = TourSession(
        steps = steps,
        tourId = tourId,
        theme = theme,
        config = config,
    )

    private val controller = TourController(
        session = session,
        lifecycleOwner = host.getLifecycleOwner(),
        context = host.getContext(),
        listener = listener,
    )

    private val overlayManager = OverlayManager(
        host = host,
        stateFlow = controller.state,
        lifecycleOwner = host.getLifecycleOwner(),
        onDismissRequested = { controller.skip() },
        onNext = { controller.next() },
        onPrevious = { controller.previous() },
    )

    /** Observe the live [TourState] — useful for custom UI that reacts to step changes. */
    val state: StateFlow<TourState> = controller.state

    /** `true` while a step is actively visible on screen. */
    val isActive: Boolean get() = state.value is TourState.Active

    // -------------------------------------------------------------------------
    // Navigation API
    // -------------------------------------------------------------------------

    /** Advance to the next step, or complete if on the last step. */
    fun next() = controller.next()

    /** Go back one step. No-op on the first step. */
    fun previous() = controller.previous()

    /** Pause the tour (e.g. app goes to background). */
    fun pause() = controller.pause()

    /** Resume a paused tour at the same step. */
    fun resume() = controller.resume()

    /** Dismiss the tour at the current step. */
    fun skip() = controller.skip()

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the [TourSession] driving this tour (steps, theme, config). */
    fun getSession(): TourSession = session

    /** Returns the [TourHost] this manager is bound to. */
    fun getHost(): TourHost = host

    /**
     * Starts the overlay and the tour controller.
     * Called internally by [com.rajjaviya.guideflow.api.GuideFlowBuilder.start].
     */
    internal fun start() {
        overlayManager.start()  // begins observing state
        controller.start()      // emits first Active state → overlay reacts
    }
}
