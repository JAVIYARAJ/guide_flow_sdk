package com.rajjaviya.guideflow.model

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Defines the visual appearance of a GuideFlow tour.
 *
 * Use the factory functions for quick setup:
 * ```kotlin
 * GuideFlow.with(this)
 *     .setTheme(TourTheme.dark())
 *     .addStep(...)
 *     .start()
 * ```
 *
 * Or build a fully custom theme:
 * ```kotlin
 * val theme = TourTheme(
 *     overlayColor          = Color.parseColor("#CC000000"),
 *     tooltipBackgroundColor = Color.WHITE,
 *     nextButtonColor       = Color.parseColor("#6200EE"),
 * )
 * ```
 *
 * @property overlayColor            Color of the dim background (ARGB). Default: 70 % black.
 * @property tooltipBackgroundColor  Background color of the tooltip card.
 * @property tooltipTitleColor       Text color for the step title.
 * @property tooltipDescriptionColor Text color for the step description.
 * @property nextButtonColor         Background color of the "Next / Finish" button.
 * @property nextButtonTextColor     Text color of the "Next / Finish" button label.
 * @property skipButtonTextColor     Text color of the "Skip" button label.
 * @property spotlightBorderColor    Optional color drawn as a stroke around the spotlight.
 *                                   Pass [Color.TRANSPARENT] to hide the border.
 * @property spotlightBorderWidth    Stroke width in pixels for the spotlight border.
 */
data class TourTheme(
    @ColorInt val overlayColor: Int = DEFAULT_OVERLAY_COLOR,
    @ColorInt val tooltipBackgroundColor: Int = Color.WHITE,
    @ColorInt val tooltipTitleColor: Int = Color.parseColor("#1A1A2E"),
    @ColorInt val tooltipDescriptionColor: Int = Color.parseColor("#4A4A6A"),
    @ColorInt val nextButtonColor: Int = Color.parseColor("#6200EE"),
    @ColorInt val nextButtonTextColor: Int = Color.WHITE,
    @ColorInt val skipButtonTextColor: Int = Color.parseColor("#6200EE"),
    @ColorInt val spotlightBorderColor: Int = Color.TRANSPARENT,
    val spotlightBorderWidth: Float = 0f,
) {

    companion object {

        @ColorInt
        private val DEFAULT_OVERLAY_COLOR = Color.parseColor("#B3000000") // 70 % black

        /** Light theme — white tooltip on a dark overlay (default). */
        fun light(): TourTheme = TourTheme(
            overlayColor = Color.parseColor("#B30F172A"), // Subtle slate tinted overlay
            tooltipBackgroundColor = Color.WHITE,
            tooltipTitleColor = Color.parseColor("#0F172A"), // Slate 900
            tooltipDescriptionColor = Color.parseColor("#475569"), // Slate 600
            nextButtonColor = Color.parseColor("#2563EB"), // Blue 600
            nextButtonTextColor = Color.WHITE,
            skipButtonTextColor = Color.parseColor("#64748B"), // Slate 500
        )

        /** Dark theme — dark tooltip card, lighter overlay. */
        fun dark(): TourTheme = TourTheme(
            overlayColor = Color.parseColor("#CC000000"),
            tooltipBackgroundColor = Color.parseColor("#1E293B"), // Slate 800
            tooltipTitleColor = Color.WHITE,
            tooltipDescriptionColor = Color.parseColor("#94A3B8"), // Slate 400
            nextButtonColor = Color.parseColor("#3B82F6"), // Blue 500
            nextButtonTextColor = Color.WHITE,
            skipButtonTextColor = Color.parseColor("#94A3B8"), // Slate 400
        )

        /** Material You / dynamic color placeholder — consumers can replace with extracted colors. */
        fun materialYou(
            @ColorInt primaryColor: Int,
            @ColorInt onPrimaryColor: Int,
        ): TourTheme = TourTheme(
            nextButtonColor = primaryColor,
            nextButtonTextColor = onPrimaryColor,
            skipButtonTextColor = primaryColor,
            spotlightBorderColor = primaryColor,
            spotlightBorderWidth = 3f,
        )
    }
}
