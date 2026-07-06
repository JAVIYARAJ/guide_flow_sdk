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

    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
        )
        val padding = dpToPx(24)
        setPadding(padding, padding, padding, dpToPx(20))
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
        addView(container)
        container.addView(titleView)
        container.addView(descriptionView)
        container.addView(buttonsRow)

        buttonsRow.addView(skipButton)
        buttonsRow.addView(previousButton)
        buttonsRow.addView(spacer)
        buttonsRow.addView(nextButton)
        
        // Setup elevation to create a beautiful shadow
        elevation = dpToPx(16).toFloat()
        outlineProvider = object : android.view.ViewOutlineProvider() {
            override fun getOutline(view: android.view.View, outline: android.graphics.Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, dpToPx(16).toFloat())
            }
        }
        clipToOutline = true
    }

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

        nextButton.text = if (isLastStep) "Finish" else "Next"

        // --- Configuration ---
        previousButton.visibility = if (config.enablePreviousButton && !isFirstStep) VISIBLE else GONE
        
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

    private fun applyTheme(theme: TourTheme) {
        val cornerRadius = dpToPx(16).toFloat()
        
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(theme.tooltipBackgroundColor)
            setCornerRadius(cornerRadius)
        }
        background = bgDrawable

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
