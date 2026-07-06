package com.rajjaviya.guideflow.tooltip

import android.graphics.RectF
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.spotlight.SpotlightBounds

/**
 * Handles adding, removing, and positioning the [TooltipView] relative to
 * the spotlight cutout.
 */
internal class TooltipRenderer(
    private val overlayContainer: ViewGroup,
    private val onNext: () -> Unit,
    private val onPrevious: () -> Unit,
    private val onSkip: () -> Unit,
) {

    private var tooltipView: TooltipView? = null

    /**
     * Creates (if needed), updates, and positions the tooltip.
     * Must be called AFTER the spotlight bounds are calculated.
     */
    fun render(
        step: GuideStep,
        theme: TourTheme,
        config: TourConfig,
        currentIndex: Int,
        totalSteps: Int,
        spotlight: SpotlightBounds,
    ) {
        val view = ensureTooltipView()
        
        view.bind(
            step = step,
            theme = theme,
            config = config,
            isFirstStep = currentIndex == 0,
            isLastStep = currentIndex == totalSteps - 1,
            onNext = onNext,
            onPrevious = onPrevious,
            onSkip = onSkip,
        )

        // We must wait for the TooltipView to measure itself so we know its width/height
        // before we can position it relative to the spotlight.
        view.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(overlayContainer.width, android.view.View.MeasureSpec.AT_MOST),
            android.view.View.MeasureSpec.makeMeasureSpec(overlayContainer.height, android.view.View.MeasureSpec.UNSPECIFIED)
        )

        val tooltipWidth = view.measuredWidth
        val tooltipHeight = view.measuredHeight

        positionTooltip(
            view = view,
            preferredPosition = step.tooltipPosition,
            spotlightBounds = spotlight.bounds,
            containerWidth = overlayContainer.width,
            containerHeight = overlayContainer.height,
            tooltipWidth = tooltipWidth,
            tooltipHeight = tooltipHeight,
            animationType = step.animationType,
        )
    }

    /** Hides and cleans up the tooltip. */
    fun clear() {
        tooltipView?.let { view ->
            overlayContainer.removeView(view)
        }
        tooltipView = null
    }

    private fun ensureTooltipView(): TooltipView {
        tooltipView?.let { return it }

        val view = TooltipView(overlayContainer.context)
        
        // Add to overlay container (which is a FrameLayout)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
        )
        // Ensure tooltip has margin from screen edges
        val margin = (16 * overlayContainer.resources.displayMetrics.density).toInt()
        params.setMargins(margin, margin, margin, margin)
        
        overlayContainer.addView(view, params)
        tooltipView = view
        return view
    }

    private fun positionTooltip(
        view: TooltipView,
        preferredPosition: TooltipPosition,
        spotlightBounds: RectF,
        containerWidth: Int,
        containerHeight: Int,
        tooltipWidth: Int,
        tooltipHeight: Int,
        animationType: com.rajjaviya.guideflow.animation.AnimationType,
    ) {
        // If there's no spotlight (e.g. view not found), just center it
        if (spotlightBounds.isEmpty) {
            view.translationX = (containerWidth - tooltipWidth) / 2f
            view.translationY = (containerHeight - tooltipHeight) / 2f
            return
        }

        val isRtl = ViewCompat.getLayoutDirection(overlayContainer) == ViewCompat.LAYOUT_DIRECTION_RTL
        
        val margin = (16 * overlayContainer.resources.displayMetrics.density)
        val spacing = (12 * overlayContainer.resources.displayMetrics.density) // Space between spotlight and tooltip

        // Calculate available space on each side
        val spaceTop = spotlightBounds.top
        val spaceBottom = containerHeight - spotlightBounds.bottom
        val spaceStart = if (isRtl) containerWidth - spotlightBounds.right else spotlightBounds.left
        val spaceEnd = if (isRtl) spotlightBounds.left else containerWidth - spotlightBounds.right

        // Determine actual position
        val position = if (preferredPosition == TooltipPosition.AUTO) {
            when {
                spaceBottom >= tooltipHeight + spacing + margin -> TooltipPosition.BOTTOM
                spaceTop >= tooltipHeight + spacing + margin -> TooltipPosition.TOP
                spaceEnd >= tooltipWidth + spacing + margin -> TooltipPosition.END
                spaceStart >= tooltipWidth + spacing + margin -> TooltipPosition.START
                else -> {
                    // Fallback to whichever vertical side has more space
                    if (spaceBottom > spaceTop) TooltipPosition.BOTTOM else TooltipPosition.TOP
                }
            }
        } else {
            preferredPosition
        }

        var x = 0f
        var y = 0f

        when (position) {
            TooltipPosition.TOP -> {
                y = spotlightBounds.top - tooltipHeight - spacing
                x = spotlightBounds.centerX() - (tooltipWidth / 2f)
            }
            TooltipPosition.BOTTOM -> {
                y = spotlightBounds.bottom + spacing
                x = spotlightBounds.centerX() - (tooltipWidth / 2f)
            }
            TooltipPosition.START -> {
                val startX = if (isRtl) spotlightBounds.right + spacing else spotlightBounds.left - tooltipWidth - spacing
                x = startX
                y = spotlightBounds.centerY() - (tooltipHeight / 2f)
            }
            TooltipPosition.END -> {
                val endX = if (isRtl) spotlightBounds.left - tooltipWidth - spacing else spotlightBounds.right + spacing
                x = endX
                y = spotlightBounds.centerY() - (tooltipHeight / 2f)
            }
            TooltipPosition.AUTO -> {} // Already resolved above
        }

        // Clamp to screen edges
        x = x.coerceIn(margin, containerWidth - tooltipWidth - margin)
        y = y.coerceIn(margin, containerHeight - tooltipHeight - margin)

        view.translationX = x
        view.translationY = y
        
        com.rajjaviya.guideflow.animation.StepAnimator.animateEnter(view, animationType)

    }
}
