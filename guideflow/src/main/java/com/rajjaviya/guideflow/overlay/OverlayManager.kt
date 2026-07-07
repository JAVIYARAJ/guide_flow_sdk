package com.rajjaviya.guideflow.overlay

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.rajjaviya.guideflow.host.TourHost
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourState
import com.rajjaviya.guideflow.positioning.ViewResolver
import com.rajjaviya.guideflow.tooltip.TooltipRenderer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages the full lifecycle of [GuideOverlayView] for a single tour run.
 *
 * Responsibilities:
 * - **Attach** — inserts [GuideOverlayView] as the topmost child of the host's root view.
 * - **Resolve** — delegates target-view coordinate resolution to [ViewResolver], which
 *                 waits for layout, detects detachment, and observes layout changes.
 * - **Update** — drives [GuideOverlayView.updateSpotlight] whenever bounds change.
 * - **Detach** — removes the overlay on terminal states and on host destruction.
 * - **Dismiss** — forwards overlay-tap events to the controller via [onDismissRequested].
 */
internal class OverlayManager(
    private val host: TourHost,
    private val stateFlow: StateFlow<TourState>,
    private val lifecycleOwner: LifecycleOwner,
    private val onDismissRequested: () -> Unit,
    private val onNext: () -> Unit,
    private val onPrevious: () -> Unit,
) {

    private var overlayView: GuideOverlayView? = null
    private var tooltipRenderer: TooltipRenderer? = null
    private var isFirstStep = true

    /**
     * Active [Job] that collects [ViewResolver.observeBounds] for the current step.
     * Cancelled and replaced whenever a new step becomes active.
     */
    private var boundsJob: Job? = null

    private var blurredBitmap: android.graphics.Bitmap? = null
    private var blurredCanvas: android.graphics.Canvas? = null

    // -------------------------------------------------------------------------
    // Start
    // -------------------------------------------------------------------------

    fun start() {
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                teardown()
            }
        })

        lifecycleOwner.lifecycleScope.launch {
            stateFlow.collect { state -> handleState(state) }
        }
    }

    // -------------------------------------------------------------------------
    // State handling
    // -------------------------------------------------------------------------

    private fun handleState(state: TourState) {
        when (state) {
            is TourState.Active -> onActive(state)

            is TourState.Paused -> {
                // Keep overlay visible — just block dismiss-on-tap interaction.
                boundsJob?.cancel()
                overlayView?.onOutsideTapped = null
            }

            is TourState.Idle,
            is TourState.Completed,
            is TourState.Dismissed,
            -> teardown()
        }
    }

    private fun onActive(state: TourState.Active) {
        val session = state.session
        ensureOverlayAttached(session)

        val overlay = overlayView ?: return
        overlay.applyTheme(session.theme)

        // Cancel the previous step's bounds observation before starting the new one.
        boundsJob?.cancel()
        boundsJob = lifecycleOwner.lifecycleScope.launch {
            ViewResolver.observeBounds(
                targetView = state.currentStep.targetView,
                overlayView = overlay,
                config = session.config,
                step = state.currentStep,
            ).collect { spotlight ->
                if (state.currentStep.animationType == com.rajjaviya.guideflow.animation.AnimationType.GLASSMORPHISM &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ) {
                    val root = host.getRootView()
                    val w = root.width
                    val h = root.height
                    if (w > 0 && h > 0) {
                        if (blurredBitmap == null || blurredBitmap!!.width != w || blurredBitmap!!.height != h) {
                            blurredBitmap?.recycle()
                            blurredBitmap = android.graphics.Bitmap.createBitmap(
                                w, h, android.graphics.Bitmap.Config.ARGB_8888
                            )
                            blurredCanvas = android.graphics.Canvas(blurredBitmap!!)
                        }
                        val prevVisibility = overlay.visibility
                        overlay.visibility = View.INVISIBLE
                        blurredBitmap!!.eraseColor(android.graphics.Color.TRANSPARENT)
                        root.draw(blurredCanvas!!)
                        overlay.visibility = prevVisibility
                        overlay.setBlurredBackground(blurredBitmap!!)
                    }
                }

                // isFirstStep flag skips animation on the very first step.
                overlay.updateSpotlight(
                    bounds = spotlight,
                    animated = !isFirstStep,
                    pulseEnabled = session.config.spotlightPulseAnimation
                )
                
                tooltipRenderer?.render(
                    step = state.currentStep,
                    theme = session.theme,
                    config = session.config,
                    currentIndex = state.currentIndex,
                    totalSteps = session.steps.size,
                    spotlight = spotlight,
                )
                
                isFirstStep = false
            }
        }
    }

    // -------------------------------------------------------------------------
    // Attach / Detach
    // -------------------------------------------------------------------------

    private fun ensureOverlayAttached(session: com.rajjaviya.guideflow.model.TourSession) {
        if (overlayView != null) return
        val config = session.config

        val root: ViewGroup = host.getRootView()
        val view = GuideOverlayView(host.getContext()).apply {
            onOutsideTapped = {
                if (config.dismissOnOverlayClick) onDismissRequested()
            }
        }

        root.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        overlayView = view
        
        tooltipRenderer = TooltipRenderer(
            overlayContainer = view, // GuideOverlayView is a FrameLayout
            provider = session.tooltipViewProvider,
            onNext = onNext,
            onPrevious = onPrevious,
            onSkip = onDismissRequested,
        )
    }

    private fun teardown() {
        boundsJob?.cancel()
        boundsJob = null
        tooltipRenderer?.clear()
        tooltipRenderer = null
        overlayView?.let { (it.parent as? ViewGroup)?.removeView(it) }
        overlayView = null
        isFirstStep = true
        
        blurredBitmap?.recycle()
        blurredBitmap = null
        blurredCanvas = null
    }
}
