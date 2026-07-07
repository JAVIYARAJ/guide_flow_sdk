package com.rajjaviya.guideflow.tooltip

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.StepIndicatorStyle
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
        clipChildren = false
        clipToPadding = false
    }
    
    private val arrowView = ArrowView(context)

    private val indicatorContainer = FrameLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            bottomMargin = dpToPx(12)
        }
    }

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

    private val customContentContainer = FrameLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            bottomMargin = dpToPx(24)
        }
        visibility = GONE
    }

    private val buttonsRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        clipChildren = false
        clipToPadding = false
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
    
    private var nextButtonAnimator: android.animation.ValueAnimator? = null

    init {
        clipChildren = false
        clipToPadding = false
        
        addView(arrowView) // Add arrow first so it draws BEHIND the container
        addView(container)
        
        container.addView(indicatorContainer)
        container.addView(titleView)
        container.addView(descriptionView)
        container.addView(customContentContainer)
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
        currentIndex: Int,
        totalSteps: Int,
        onNext: () -> Unit,
        onPrevious: () -> Unit,
        onSkip: () -> Unit,
    ) {
        // --- Reset Layout for Clean Measurement ---
        val params = container.layoutParams as? LayoutParams ?: LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, 0)
        container.layoutParams = params
        arrowView.visibility = GONE

        val isFirstStep = currentIndex == 0
        val isLastStep = currentIndex == totalSteps - 1

        // --- Indicator ---
        setupIndicator(config.stepIndicatorStyle, theme, currentIndex, totalSteps)

        // --- Content ---
        if (step.customContentView != null || step.customContentLayoutRes != null) {
            titleView.visibility = GONE
            descriptionView.visibility = GONE
            customContentContainer.visibility = VISIBLE
            customContentContainer.removeAllViews()
            
            if (step.customContentView != null) {
                // If it already has a parent, remove it first
                (step.customContentView.parent as? ViewGroup)?.removeView(step.customContentView)
                customContentContainer.addView(step.customContentView)
            } else if (step.customContentLayoutRes != null) {
                LayoutInflater.from(context).inflate(step.customContentLayoutRes, customContentContainer, true)
            }
        } else {
            customContentContainer.visibility = GONE
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
        }

        // --- Next Button Pulse Animation ---
        nextButtonAnimator?.cancel()
        nextButton.scaleX = 1f
        nextButton.scaleY = 1f

        if (step.showNextButton) {
            nextButton.visibility = VISIBLE
            nextButton.text = if (isLastStep) "Finish" else step.nextButtonLabel
            
            // Subtle 5% pulse after 3 seconds of inactivity
            nextButtonAnimator = android.animation.ValueAnimator.ofFloat(1f, 1.05f).apply {
                duration = 800L
                startDelay = 3000L
                repeatCount = android.animation.ValueAnimator.INFINITE
                repeatMode = android.animation.ValueAnimator.REVERSE
                addUpdateListener { animator ->
                    val scale = animator.animatedValue as Float
                    nextButton.scaleX = scale
                    nextButton.scaleY = scale
                }
                start()
            }
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

    private fun setupIndicator(
        style: StepIndicatorStyle,
        theme: TourTheme,
        currentIndex: Int,
        totalSteps: Int
    ) {
        indicatorContainer.removeAllViews()
        
        if (style == StepIndicatorStyle.NONE || totalSteps <= 1) {
            indicatorContainer.visibility = GONE
            return
        }
        
        indicatorContainer.visibility = VISIBLE

        when (style) {
            StepIndicatorStyle.TEXT -> {
                val tv = TextView(context).apply {
                    text = "${currentIndex + 1} of $totalSteps"
                    textSize = 13f
                    setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
                    setTextColor(theme.tooltipDescriptionColor)
                    alpha = 0.7f
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.START or Gravity.CENTER_VERTICAL
                    )
                }
                indicatorContainer.addView(tv)
            }
            StepIndicatorStyle.DOTS -> {
                val dotsContainer = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.START or Gravity.CENTER_VERTICAL
                    )
                }
                
                for (i in 0 until totalSteps) {
                    val dot = android.view.View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(dpToPx(6), dpToPx(6)).apply {
                            marginEnd = if (i == totalSteps - 1) 0 else dpToPx(6)
                        }
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(if (i == currentIndex) theme.nextButtonColor else theme.tooltipDescriptionColor)
                            alpha = if (i == currentIndex) 255 else 76 // 30% opacity for inactive
                        }
                    }
                    dotsContainer.addView(dot)
                }
                indicatorContainer.addView(dotsContainer)
            }
            StepIndicatorStyle.LINEAR -> {
                val pb = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(4),
                        Gravity.CENTER_VERTICAL
                    )
                    max = totalSteps
                    progress = currentIndex + 1
                    
                    progressDrawable = android.graphics.drawable.LayerDrawable(arrayOf(
                        GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            setColor(theme.tooltipDescriptionColor)
                            alpha = 50
                            setCornerRadius(dpToPx(2).toFloat())
                        },
                        android.graphics.drawable.ClipDrawable(
                            GradientDrawable().apply {
                                shape = GradientDrawable.RECTANGLE
                                setColor(theme.nextButtonColor)
                                setCornerRadius(dpToPx(2).toFloat())
                            },
                            Gravity.START,
                            android.graphics.drawable.ClipDrawable.HORIZONTAL
                        )
                    )).apply {
                        setId(0, android.R.id.background)
                        setId(1, android.R.id.progress)
                    }
                }
                indicatorContainer.addView(pb)
            }
            else -> {}
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
