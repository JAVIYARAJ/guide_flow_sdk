package com.rajjaviya.guideflow.tooltip

import android.content.Context
import android.view.View
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme

/**
 * Interface for providing a completely custom tooltip layout for an entire tour.
 * 
 * If provided to the `GuideFlowBuilder`, the SDK will delegate the creation of the tooltip
 * to this provider instead of using the default SDK tooltip.
 *
 * It is your responsibility to handle background bubbles, arrows, text styling, and clicking 
 * the provided navigation callbacks (`onNext`, `onPrevious`, `onSkip`).
 */
interface TooltipViewProvider {
    /**
     * Called when a new step needs to be rendered or an existing tooltip needs to be updated.
     * 
     * @return The [View] to be displayed as the tooltip. You can inflate a new view or recycle an existing one.
     */
    fun getView(
        context: Context,
        step: GuideStep,
        theme: TourTheme,
        config: TourConfig,
        currentIndex: Int,
        totalSteps: Int,
        onNext: () -> Unit,
        onPrevious: () -> Unit,
        onSkip: () -> Unit
    ): View

    /**
     * Called when the spotlight bounds are resolved and the tooltip is positioned.
     * This provides the arrow orientation and offset so your custom view can draw an arrow
     * pointing at the target view, if desired.
     */
    fun setupArrow(position: TooltipPosition, offset: Float, theme: TourTheme) {}
}
