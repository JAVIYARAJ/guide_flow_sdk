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
    private val provider: TooltipViewProvider?,
    private val onNext: () -> Unit,
    private val onPrevious: () -> Unit,
    private val onSkip: () -> Unit,
) {

    private var currentView: android.view.View? = null

    /**
     * Creates (if needed), updates, and positions the tooltip.
     * Must be called AFTER the spotlight bounds are calculated.
     */
    @Suppress("LongMethod")
    fun render(
        step: GuideStep,
        theme: TourTheme,
        config: TourConfig,
        currentIndex: Int,
        totalSteps: Int,
        spotlight: SpotlightBounds,
    ) {
        val view = if (provider != null) {
            val v = provider.getView(
                context = overlayContainer.context,
                step = step,
                theme = theme,
                config = config,
                currentIndex = currentIndex,
                totalSteps = totalSteps,
                onNext = onNext,
                onPrevious = onPrevious,
                onSkip = onSkip
            )
            // Add to overlay container if not already attached
            if (v.parent == null) {
                overlayContainer.addView(v)
            } else if (v.parent != overlayContainer) {
                (v.parent as? ViewGroup)?.removeView(v)
                overlayContainer.addView(v)
            }
            v
        } else {
            val v = ensureDefaultTooltipView()
            v.bind(
                step = step,
                theme = theme,
                config = config,
                currentIndex = currentIndex,
                totalSteps = totalSteps,
                onNext = onNext,
                onPrevious = onPrevious,
                onSkip = onSkip,
            )
            v
        }
        
        currentView = view

        val containerWidth = if (overlayContainer.width > 0) {
            overlayContainer.width
        } else {
            overlayContainer.resources.displayMetrics.widthPixels
        }
        val containerHeight = if (overlayContainer.height > 0) {
            overlayContainer.height
        } else {
            overlayContainer.resources.displayMetrics.heightPixels
        }

        // We must wait for the TooltipView to measure itself so we know its width/height
        // before we can position it relative to the spotlight.
        val marginPx = (16 * overlayContainer.resources.displayMetrics.density).toInt()
        val widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(
            containerWidth - (2 * marginPx),
            android.view.View.MeasureSpec.AT_MOST
        )
        val heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(
            containerHeight,
            android.view.View.MeasureSpec.UNSPECIFIED
        )
        view.measure(widthSpec, heightSpec)

        val tooltipWidth = view.measuredWidth
        val tooltipHeight = view.measuredHeight

        positionTooltip(
            view = view,
            preferredPosition = step.tooltipPosition,
            spotlightBounds = spotlight.bounds,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            tooltipWidth = tooltipWidth,
            tooltipHeight = tooltipHeight,
            animationType = step.animationType,
            theme = theme,
            pointerOffset = step.pointerOffset,
        )
    }

    /** Hides and cleans up the tooltip. */
    fun clear() {
        currentView?.let { view ->
            overlayContainer.removeView(view)
        }
        currentView = null
    }

    private fun ensureDefaultTooltipView(): TooltipView {
        (currentView as? TooltipView)?.let { return it }

        val view = TooltipView(overlayContainer.context)
        
        // Add to overlay container (which is a FrameLayout)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
        )
        // Add margins so the Android layout pass inherently restricts the width to screenWidth - 32dp.
        // This is necessary to prevent the view from exceeding the screen bounds during measurement.
        val margin = (16 * overlayContainer.resources.displayMetrics.density).toInt()
        params.setMargins(margin, margin, margin, margin)
        
        overlayContainer.addView(view, params)
        currentView = view
        return view
    }

    @Suppress("LongParameterList", "CyclomaticComplexMethod", "LongMethod")
    private fun positionTooltip(
        view: android.view.View,
        preferredPosition: TooltipPosition,
        spotlightBounds: RectF,
        containerWidth: Int,
        containerHeight: Int,
        tooltipWidth: Int,
        tooltipHeight: Int,
        animationType: com.rajjaviya.guideflow.animation.AnimationType,
        theme: TourTheme,
        pointerOffset: Float,
    ) {
        // If there's no spotlight (e.g. view not found), just center it
        if (spotlightBounds.isEmpty) {
            view.translationX = (containerWidth - tooltipWidth) / 2f
            view.translationY = (containerHeight - tooltipHeight) / 2f
            return
        }

        val isRtl = ViewCompat.getLayoutDirection(overlayContainer) == ViewCompat.LAYOUT_DIRECTION_RTL
        
        val margin = (16 * overlayContainer.resources.displayMetrics.density)
        val spacing = (24 * overlayContainer.resources.displayMetrics.density) // Space between spotlight and tooltip

        // Calculate available space on each side
        val spaceTop = spotlightBounds.top
        val spaceBottom = containerHeight - spotlightBounds.bottom
        val spaceStart = if (isRtl) containerWidth - spotlightBounds.right else spotlightBounds.left
        val spaceEnd = if (isRtl) spotlightBounds.left else containerWidth - spotlightBounds.right

        val safePointerOffset = pointerOffset.coerceIn(0f, 1f)

        // Ensure preferred position actually fits on screen
        var position = preferredPosition
        if (position != TooltipPosition.AUTO) {
            val fits = when (position) {
                TooltipPosition.BOTTOM -> spaceBottom >= tooltipHeight + spacing + margin
                TooltipPosition.TOP -> spaceTop >= tooltipHeight + spacing + margin
                TooltipPosition.END -> spaceEnd >= tooltipWidth + spacing + margin
                TooltipPosition.START -> spaceStart >= tooltipWidth + spacing + margin
                else -> true
            }
            if (!fits) position = TooltipPosition.AUTO
        }

        // Determine actual position
        if (position == TooltipPosition.AUTO) {
            position = when {
                spaceBottom >= tooltipHeight + spacing + margin -> TooltipPosition.BOTTOM
                spaceTop >= tooltipHeight + spacing + margin -> TooltipPosition.TOP
                spaceEnd >= tooltipWidth + spacing + margin -> TooltipPosition.END
                spaceStart >= tooltipWidth + spacing + margin -> TooltipPosition.START
                else -> {
                    // Fallback to whichever vertical side has more space
                    if (spaceBottom > spaceTop) TooltipPosition.BOTTOM else TooltipPosition.TOP
                }
            }
        }

        var x = 0f
        var y = 0f
        
        val pointerX = spotlightBounds.left + (spotlightBounds.width() * safePointerOffset)
        val pointerY = spotlightBounds.top + (spotlightBounds.height() * safePointerOffset)

        when (position) {
            TooltipPosition.TOP -> {
                y = spotlightBounds.top - tooltipHeight - spacing
                x = pointerX - (tooltipWidth / 2f)
            }
            TooltipPosition.BOTTOM -> {
                y = spotlightBounds.bottom + spacing
                x = pointerX - (tooltipWidth / 2f)
            }
            TooltipPosition.START -> {
                val startX = if (isRtl) {
                    spotlightBounds.right + spacing
                } else {
                    spotlightBounds.left - tooltipWidth - spacing
                }
                x = startX
                y = pointerY - (tooltipHeight / 2f)
            }
            TooltipPosition.END -> {
                val endX = if (isRtl) spotlightBounds.left - tooltipWidth - spacing else spotlightBounds.right + spacing
                x = endX
                y = pointerY - (tooltipHeight / 2f)
            }
            TooltipPosition.AUTO -> {} // Already resolved above
        }

        val minX = margin
        val maxX = maxOf(minX, containerWidth - tooltipWidth - margin)
        val minY = margin
        val maxY = maxOf(minY, containerHeight - tooltipHeight - margin)

        x = x.coerceIn(minX, maxX)
        y = y.coerceIn(minY, maxY)

        // Because TooltipView has margins in its LayoutParams, its internal (0,0) is
        // actually at (margin, margin) visually. We must subtract the margin from our
        // absolute x and y to position it perfectly in screen space!
        view.translationX = x - margin
        view.translationY = y - margin
        
        // Calculate offset for the arrow so it points directly at the pointer coordinate
        val isHorizontal = position == TooltipPosition.START || position == TooltipPosition.END
        val arrowOffset = if (isHorizontal) {
            pointerY - y
        } else {
            pointerX - x
        }
        
        if (provider != null) {
            provider.setupArrow(position, arrowOffset, theme)
        } else if (view is TooltipView) {
            view.setupArrow(position, arrowOffset, theme)
        }
        
        com.rajjaviya.guideflow.animation.StepAnimator.animateEnter(view, animationType)

    }
}
