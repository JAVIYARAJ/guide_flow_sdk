package com.rajjaviya.guideflow.tooltip

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.util.dpToPx

/**
 * The floating card UI that displays the step title, description, and navigation buttons.
 *
 * Built entirely programmatically to keep the SDK footprint small and
 * avoid resource merging conflicts with the consumer application.
 */
internal class TooltipView(context: Context) : FrameLayout(context) {

    private class ArrowView(context: Context) : android.view.View(context) {
        val path = android.graphics.Path()
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.FILL
        }
        var direction: TooltipPosition = TooltipPosition.TOP
        
        fun setArrow(color: Int, position: TooltipPosition) {
            paint.color = color
            direction = position
            invalidate()
        }

        override fun onDraw(canvas: android.graphics.Canvas) {
            path.reset()
            val w = width.toFloat()
            val h = height.toFloat()
            when (direction) {
                TooltipPosition.BOTTOM -> { // Tooltip is BELOW, arrow points UP
                    path.moveTo(0f, h)
                    path.lineTo(w / 2, 0f)
                    path.lineTo(w, h)
                }
                TooltipPosition.TOP -> { // Tooltip is ABOVE, arrow points DOWN
                    path.moveTo(0f, 0f)
                    path.lineTo(w / 2, h)
                    path.lineTo(w, 0f)
                }
                TooltipPosition.END -> { // Tooltip is to the RIGHT, arrow points LEFT
                    path.moveTo(w, 0f)
                    path.lineTo(0f, h / 2)
                    path.lineTo(w, h)
                }
                TooltipPosition.START -> { // Tooltip is to the LEFT, arrow points RIGHT
                    path.moveTo(0f, 0f)
                    path.lineTo(w, h / 2)
                    path.lineTo(0f, h)
                }
                else -> {}
            }
            path.close()
            canvas.drawPath(path, paint)
        }
    }

    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        val padding = dpToPx(24)
        setPadding(padding, padding, padding, dpToPx(20))
        
        elevation = dpToPx(16).toFloat()
        outlineProvider = object : android.view.ViewOutlineProvider() {
            override fun getOutline(view: android.view.View, outline: android.graphics.Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, dpToPx(16).toFloat())
            }
        }
        clipToOutline = true
    }
    
    private val arrowView = ArrowView(context)

    private val titleView = TextView(context).apply {
        textSize = 20f
        setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            bottomMargin = dpToPx(8)
        }
    }

    private val descriptionView = TextView(context).apply {
        textSize = 15f
        setLineSpacing(dpToPx(4).toFloat(), 1.1f)
        setTypeface(Typeface.create("sans-serif", Typeface.NORMAL))
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            bottomMargin = dpToPx(24)
        }
    }

    private val buttonsRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
    }

    private val skipButton = TextView(context).apply {
        text = "Skip"
        textSize = 14f
        setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
        setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        isClickable = true
        isFocusable = true
        addRippleEffect()
    }

    private val previousButton = TextView(context).apply {
        text = "Back"
        textSize = 14f
        setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
        setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        isClickable = true
        isFocusable = true
        addRippleEffect()
    }

    private val spacer = android.view.View(context).apply {
        layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
    }

    private val nextButton = TextView(context).apply {
        textSize = 14f
        setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
        setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10))
        isClickable = true
        isFocusable = true
    }

    init {
        clipChildren = false
        clipToPadding = false
        
        addView(arrowView) // Add arrow first so it draws BEHIND the container
        addView(container)
        
        container.addView(titleView)
        container.addView(descriptionView)
        container.addView(buttonsRow)

        buttonsRow.addView(skipButton)
        buttonsRow.addView(previousButton)
        buttonsRow.addView(spacer)
        buttonsRow.addView(nextButton)
    }

    @Suppress("LongParameterList")
    fun bind(
        step: GuideStep,
        theme: TourTheme,
        config: TourConfig,
        isFirstStep: Boolean,
        isLastStep: Boolean,
        onNext: () -> Unit,
        onPrevious: () -> Unit,
        onSkip: () -> Unit,
    ) {
        // --- Reset Layout for Clean Measurement ---
        val params = container.layoutParams as? LayoutParams ?: LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, 0)
        container.layoutParams = params
        arrowView.visibility = GONE

        // --- Content ---
        if (step.title.isNullOrBlank()) {
            titleView.visibility = GONE
        } else {
            titleView.visibility = VISIBLE
            titleView.text = step.title
        }

        if (step.description.isNullOrBlank()) {
            descriptionView.visibility = GONE
        } else {
            descriptionView.visibility = VISIBLE
            descriptionView.text = step.description
        }

        if (step.showNextButton) {
            nextButton.visibility = VISIBLE
            nextButton.text = if (isLastStep) "Finish" else step.nextButtonLabel
        } else {
            nextButton.visibility = GONE
        }

        if (step.showSkipButton && !isLastStep) {
            skipButton.visibility = VISIBLE
            skipButton.text = step.skipButtonLabel
        } else {
            skipButton.visibility = GONE
        }

        // --- Configuration ---
        previousButton.visibility = if (config.enablePreviousButton && !isFirstStep) VISIBLE else GONE
        previousButton.text = step.previousButtonLabel
        
        // If they click on the tooltip itself, don't let the touch fall through to the overlay
        setOnClickListener { }

        // --- Listeners ---
        nextButton.setOnClickListener { onNext() }
        previousButton.setOnClickListener { onPrevious() }
        skipButton.setOnClickListener { onSkip() }

        // --- Theming ---
        applyTheme(theme)
        // --- Accessibility ---
        val announcement = buildString {
            if (!step.title.isNullOrBlank()) append(step.title).append(". ")
            if (!step.description.isNullOrBlank()) append(step.description)
        }
        if (announcement.isNotBlank()) {
            announceForAccessibility(announcement)
        }
    }

    fun setupArrow(position: TooltipPosition, offset: Float, theme: TourTheme) {
        val arrowWidth = dpToPx(24) // Base width
        val arrowHeight = dpToPx(16) // Height of the triangle
        
        val isVertical = position == TooltipPosition.TOP || position == TooltipPosition.BOTTOM
        val viewWidth = if (isVertical) arrowWidth else arrowHeight
        val viewHeight = if (isVertical) arrowHeight else arrowWidth
        
        val arrowParams = LayoutParams(viewWidth, viewHeight)
        val containerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        
        // Coerce the arrow so it doesn't render outside the tooltip bounds
        // measuredWidth/Height here is exactly the container's size since we reset margins in bind()
        val safeLeft = offset.toInt().coerceIn(viewWidth, (measuredWidth - viewWidth).coerceAtLeast(viewWidth))
        val safeTop = offset.toInt().coerceIn(viewHeight, (measuredHeight - viewHeight).coerceAtLeast(viewHeight))

        val overlap = dpToPx(3) // Overlap to hide the seam seamlessly
        
        var shiftX = 0f
        var shiftY = 0f

        when (position) {
            TooltipPosition.BOTTOM -> {
                val margin = viewHeight - overlap
                containerParams.topMargin = margin
                arrowParams.gravity = Gravity.TOP or Gravity.LEFT
                arrowParams.leftMargin = safeLeft - (viewWidth / 2)
                shiftY = -margin.toFloat()
            }
            TooltipPosition.TOP -> {
                containerParams.bottomMargin = viewHeight - overlap
                arrowParams.gravity = Gravity.BOTTOM or Gravity.LEFT
                arrowParams.leftMargin = safeLeft - (viewWidth / 2)
                // Container stays at visual Y, no shift needed
            }
            TooltipPosition.END -> {
                val margin = viewWidth - overlap
                containerParams.leftMargin = margin
                arrowParams.gravity = Gravity.LEFT or Gravity.TOP
                arrowParams.topMargin = safeTop - (viewHeight / 2)
                shiftX = -margin.toFloat()
            }
            TooltipPosition.START -> {
                containerParams.rightMargin = viewWidth - overlap
                arrowParams.gravity = Gravity.RIGHT or Gravity.TOP
                arrowParams.topMargin = safeTop - (viewHeight / 2)
                // Container stays at visual X, no shift needed
            }
            else -> {
                arrowView.visibility = GONE
            }
        }
        
        // Compensate the tooltip translation so the container's visual position exactly matches 
        // what TooltipRenderer calculated before the arrow margins were added.
        translationX += shiftX
        translationY += shiftY
        
        arrowView.visibility = VISIBLE
        arrowView.layoutParams = arrowParams
        container.layoutParams = containerParams
        arrowView.setArrow(theme.tooltipBackgroundColor, position)
    }

    private fun applyTheme(theme: TourTheme) {
        val cornerRadius = dpToPx(16).toFloat()
        
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(theme.tooltipBackgroundColor)
            setCornerRadius(cornerRadius)
        }
        container.background = bgDrawable

        titleView.setTextColor(theme.tooltipTitleColor)
        descriptionView.setTextColor(theme.tooltipDescriptionColor)
        
        skipButton.setTextColor(theme.skipButtonTextColor)
        previousButton.setTextColor(theme.skipButtonTextColor)
        
        nextButton.setTextColor(theme.nextButtonTextColor)
        
        val nextBgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(theme.nextButtonColor)
            setCornerRadius(dpToPx(24).toFloat()) // Pill shape
        }
        
        val stateList = StateListDrawable().apply {
            val pressedDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(theme.nextButtonColor)
                alpha = 200 // Slightly dim when pressed
                setCornerRadius(dpToPx(24).toFloat())
            }
            addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            addState(intArrayOf(), nextBgDrawable)
        }
        nextButton.background = stateList
    }

    private fun android.view.View.addRippleEffect() {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        setBackgroundResource(outValue.resourceId)
    }
}
